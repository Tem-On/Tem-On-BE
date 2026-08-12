# TEM-ON Backend

> **한정된 이벤트 상품에 트래픽이 집중되는 상황에서 대기열, 재고, 주문, 결제의 안정적인 처리를 목표로 구현한 이벤트 커머스 플랫폼**

TEM-ON은 선착순 이벤트 및 타임세일 상황에서 다수의 사용자가 동시에 한정된 상품을 구매하는 상황을 가정한 커머스 프로젝트입니다.

일반적인 상품 조회와 주문 기능을 구현하는 것에서 끝내지 않고,

- 이벤트 시작 순간 요청이 집중되면 어떻게 처리할 것인가?
- 동시에 여러 사용자가 같은 상품을 구매하면 재고를 어떻게 관리할 것인가?
- 서비스 하나의 장애가 다른 서비스로 전파되는 것을 어떻게 줄일 것인가?
- 실제 트래픽이 증가했을 때 어디가 병목인지 어떻게 확인할 것인가?

와 같은 문제를 직접 다루는 것을 목표로 했습니다.

이를 위해 Redis 기반 대기열, Kafka 기반 비동기 이벤트 처리, MSA 구조를 적용했으며 AWS EKS 환경에 배포했습니다.

또한 Prometheus/Grafana를 통해 운영 메트릭을 수집하고 nGrinder 부하 테스트를 수행하여 실제 병목을 발견하고 개선하는 과정까지 진행했습니다.

---

## 🔗 Tech Stack

### Language & Framework

![Java](https://img.shields.io/badge/Java%2017-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)

### Database & Cache

![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

### Messaging

![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white)

### Security

![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)

### Monitoring & Test

![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat-square&logo=grafana&logoColor=white)
![nGrinder](https://img.shields.io/badge/nGrinder-4B4B4B?style=flat-square)



---


# 1. Architecture

<img width="1661" height="948" alt="image" src="https://github.com/user-attachments/assets/fb20f20c-79b7-4cae-a4e9-3fd3bf496877" />


## 왜 서비스를 분리했는가?

처음부터 단순히 "MSA를 사용해보기 위해" 서비스를 나눈 것은 아닙니다.

TEM-ON의 기능을 분석했을 때 각 영역마다 책임과 트래픽 특성이 달랐습니다.

특히 이벤트가 시작되는 순간에는 일반적인 인증 요청보다 **대기열과 재고 영역에 트래픽이 집중**됩니다.

반면 주문과 결제는 재고 처리 이후 수행되며 데이터 정합성이 중요한 영역입니다.

따라서 서로 다른 책임과 부하 특성을 가진 기능을 하나의 애플리케이션에서 관리하기보다 기능별 책임을 분리했습니다.

| Service | Port | Responsibility | 분리 기준 |
|---|---:|---|---|
| Gateway | 8080 | Routing, JWT | 외부 요청의 단일 진입점 |
| Auth | 8081 | 인증, 사용자 | 인증 책임 분리 |
| Commerce | 8082 | 상품, 이벤트 | 상품/이벤트 도메인 관리 |
| Order-Payment | 8083 | 주문, 결제 | 구매 트랜잭션 관리 |
| Queue-Stock | 8084 | 대기열, 재고 | 이벤트 순간 집중되는 트래픽 처리 |

이를 통해 서비스별 책임을 명확하게 하고, 특정 영역의 변경이나 부하가 다른 영역에 미치는 영향을 줄이는 방향으로 구성했습니다.


---

# 2. Redis 기반 대기열

## 왜 대기열이 필요했는가?

TEM-ON에서는 이벤트가 시작되는 순간 여러 사용자가 동시에 같은 상품에 접근할 수 있습니다.

모든 요청을 즉시

```text
상품 접근
    ↓
재고 확인
    ↓
주문 생성
    ↓
DB 접근
```

으로 처리한다면 순간적인 트래픽이 주문, 재고 및 DB 계층까지 그대로 전달됩니다.

따라서 이벤트 시작 시 발생하는 요청을 바로 주문 처리 계층으로 전달하기보다 **앞단에서 요청의 진입량을 제어할 수 있는 대기열**이 필요하다고 판단했습니다.

```text
사용자
   │
   ▼
대기열
   │
   │ 입장 허용
   ▼
재고 선점
   │
   ▼
주문
   │
   ▼
결제
```

---

## 왜 Redis를 선택했는가?

대기열은 사용자의 진입과 순위 조회가 빈번하게 발생합니다.

이를 관계형 DB에서 처리할 경우 이벤트 순간마다 대기열 INSERT와 순위 조회가 DB 부하로 이어질 수 있다고 판단했습니다.

대기열 데이터는 주문과 같은 영구 데이터보다

- 빠른 읽기/쓰기
- 사용자 순위 조회
- 일시적인 상태 관리

가 더 중요했습니다.

따라서 메모리 기반으로 빠르게 데이터를 처리할 수 있는 Redis를 대기열 저장소로 선택했습니다.

---

## 왜 Sorted Set을 사용했는가?

단순히 사용자가 대기 중인지 확인하는 것뿐만 아니라 **누가 먼저 들어왔는지와 현재 몇 번째인지**를 관리해야 했습니다.

따라서 Redis의 `ZSET`을 사용했습니다.

```text
Key
waitingQueue:{eventProductId}

Member
userId

Score
진입 시간
```

Score에 진입 시간을 저장하면 사용자 진입 순서를 자연스럽게 정렬할 수 있고, Redis의 Rank 연산을 이용해 현재 대기 순서를 조회할 수 있습니다.

즉,

```text
빠른 데이터 처리
        +
순서 보장
        +
순위 조회
```

라는 요구사항에 적합하다고 판단하여 Sorted Set을 선택했습니다.

---

# 3. Queue Flow

```text
이벤트 상품 접근
        │
        ▼
대기열 진입
        │
        ▼
Redis ZSET 등록
        │
        ▼
현재 Rank 조회
        │
        ├──────────────┐
        │              │
        ▼              ▼
     WAITING        입장 허용
                       │
                       ▼
                   재고 선점
                       │
                       ▼
                    주문 생성
                       │
                       ▼
                      결제
```

사용자는 대기열 진입 이후 자신의 순위를 조회할 수 있으며 입장이 허용된 사용자만 구매 단계로 이동하도록 구성했습니다.

대기열 상태 역시 상품별로 관리하여 운영자가 필요에 따라 대기열을 제어할 수 있도록 했습니다.

```text
OPEN
CLOSED
RESET
```

---

# 4. Kafka 기반 비동기 이벤트 처리

## 왜 Kafka를 사용했는가?

주문과 결제 과정에서는 하나의 작업이 완료된 이후 다른 서비스에서 후속 작업이 필요한 경우가 발생합니다.

예를 들어 결제가 완료된 이후 후속 처리를 서비스 간 동기 호출로 강하게 연결한다면,

```text
Payment
   ↓
Stock
   ↓
다음 처리
```

앞선 요청은 뒤쪽 서비스의 응답이 완료될 때까지 영향을 받을 수 있습니다.

또한 특정 서비스에서 장애나 지연이 발생했을 때 호출 관계를 따라 다른 서비스까지 영향을 받을 가능성이 있습니다.

따라서 모든 후속 처리를 직접적인 동기 호출로 연결하기보다 **이벤트를 발행하고 필요한 서비스가 이를 소비하는 방식**을 적용했습니다.

```text
Order / Payment
       │
       ▼
     Kafka
       │
 ┌─────┼───────────┐
 ▼     ▼           ▼
결제   재고        기타
이벤트 변경        후속 처리
```

이를 통해 서비스 간 직접적인 의존 관계를 줄이고 후속 작업을 비동기로 분리하고자 했습니다.

### 주요 이벤트

```text
payment-completed
payment-failed
payment-canceled
stock-changed
event-status
```

---

# 5. API Gateway

## 왜 Gateway를 두었는가?

마이크로서비스가 분리되면서 클라이언트가 각각의 서비스 주소와 인증 방식을 직접 알아야 하는 구조는 피하고자 했습니다.

따라서 외부 요청의 단일 진입점을 Gateway로 통합했습니다.

```text
Client
   │
   ▼
Gateway
   │
   ├── /api/auth
   ├── /api/users
   ├── /api/products
   ├── /api/events
   ├── /api/orders
   ├── /api/payments
   ├── /api/queue
   └── /api/stocks
```

Gateway에서는 요청 경로에 따라 적절한 서비스로 요청을 전달합니다.

JWT 검증 역시 Gateway에서 수행하고 인증된 사용자 정보를 내부 서비스에 전달하여 인증 처리의 중복을 줄였습니다.

---

# 6. Monitoring

## 왜 모니터링을 구성했는가?

애플리케이션이 정상적으로 실행된다는 사실만으로 실제 운영 상태를 판단하기 어렵다고 생각했습니다.

특히 부하 테스트에서 응답시간이 증가하더라도

```text
CPU 문제인지
Memory 문제인지
DB 문제인지
Connection Pool 문제인지
```

로그만으로 판단하기 어려웠습니다.

따라서 애플리케이션 내부 상태를 수치로 확인하기 위해 **Prometheus + Grafana 기반 모니터링 환경**을 구성했습니다.

```text
Spring Boot Actuator
        │
        ▼
   ServiceMonitor
        │
        ▼
    Prometheus
        │
        ▼
      Grafana
```

Spring Boot Actuator/Micrometer를 통해 메트릭을 노출하고 Prometheus가 이를 수집하도록 구성했습니다.

Grafana에서는 수집된 데이터를 대시보드 형태로 확인할 수 있도록 했습니다.

---

## 왜 비즈니스 메트릭을 추가했는가?

CPU, Memory, HTTP 응답시간만으로는 TEM-ON의 핵심 기능이 정상적으로 동작하고 있는지 판단하기 어려웠습니다.

예를 들어 서버 CPU가 정상이어도 대기열 사용자가 비정상적으로 증가하거나 재고 처리가 멈출 수 있습니다.

따라서 시스템 메트릭뿐 아니라 서비스의 실제 상태를 확인할 수 있는 비즈니스 메트릭을 추가했습니다.

```text
temon_queue_enter_total
temon_queue_admitted_total
temon_queue_waiting_count

temon_stock_remaining
temon_stock_reserved
temon_stock_sold
```

이를 통해

```text
서버가 정상인가?
```

뿐만 아니라

```text
대기열이 정상적으로 처리되고 있는가?
재고가 정상적으로 변경되고 있는가?
```

까지 함께 확인할 수 있도록 했습니다.

---

# 7. Performance Test

## 왜 부하 테스트를 진행했는가?

선착순 이벤트 서비스는 일반적인 상황보다 **이벤트 시작 순간의 트래픽 처리 능력**이 중요합니다.

따라서 기능 테스트만으로 안정성을 판단하기 어렵다고 생각했습니다.

실제로 동시 사용자가 증가했을 때

- TPS가 어디까지 증가하는지
- 어느 지점부터 응답시간이 증가하는지
- 시스템의 병목이 어디에서 발생하는지

확인하기 위해 nGrinder를 이용하여 부하 테스트를 수행했습니다.

---

# 8. Troubleshooting
## DB Connection Pool 병목 분석 및 개선

### 1. 문제 발견

대기열 API를 대상으로 Vuser를 단계적으로 증가시키며 테스트했습니다.

| Vuser | TPS | 평균 응답시간 |
|---:|---:|---:|
| 2 | 222.3 | 8.22ms |
| 10 | 783.4 | 11.60ms |
| 50 | **884.9** | 54.22ms |
| 100 | 830.8 | 114.47ms |
| 300 | **599.1** | **456.73ms** |

50 Vuser까지는 동시 사용자가 증가하면서 TPS 역시 증가했습니다.

그러나 50 Vuser 이후부터는 사용자를 더 증가시켰음에도 처리량이 오히려 감소했습니다.

특히 50 → 300 Vuser 구간에서

```text
TPS
884.9 → 599.1
약 32% 감소

평균 응답시간
54.22ms → 456.73ms
약 8.4배 증가
```

했습니다.

단순히 사용자가 많아져 느려진 것으로 판단하지 않고 **시스템 내부의 어떤 자원이 먼저 한계에 도달했는지 확인할 필요가 있다고 판단했습니다.**

---

### 2. 병목 분석

동일한 부하 테스트를 수행하면서 Prometheus/Grafana를 통해 다음 메트릭을 함께 확인했습니다.

```text
CPU
Memory
HTTP Request Duration
HikariCP Active Connection
HikariCP Pending Connection
Connection 획득 대기시간
```

CPU와 Memory에는 상대적으로 여유가 있었습니다.

반면 HikariCP의 Active Connection은 부하가 증가하면서 설정된 최대값에 지속적으로 도달했습니다.

당시 설정은 다음과 같았습니다.

```yaml
maximum-pool-size: 3
```

Active Connection이 3개를 모두 사용하면서 새로운 요청이 즉시 DB Connection을 확보하지 못했고 Pending Connection과 Connection 획득 대기시간이 증가했습니다.

```text
동시 요청 증가
      │
      ▼
DB 요청 증가
      │
      ▼
Connection 3개 모두 사용
      │
      ▼
Connection 확보 실패
      │
      ▼
Pending 요청 증가
      │
      ▼
Connection 반환 대기
      │
      ▼
응답시간 증가
      │
      ▼
TPS 감소
```

따라서 서버의 CPU나 Memory 부족이 아니라 **DB Connection Pool이 먼저 포화되면서 요청이 대기하는 것이 주요 병목**이라고 판단했습니다.

---

### 3. 왜 Connection Pool을 무작정 늘리지 않았는가?

당시 RDS 설정은

```text
max_connections = 30
```

이었습니다.

단순히 HikariCP의

```text
maximum-pool-size
```

를 크게 설정하면 개별 서비스 성능은 일시적으로 좋아질 수 있습니다.

하지만 TEM-ON에서는 여러 마이크로서비스가 하나의 RDS를 공유합니다.

```text
                   RDS
              max = 30
                  │
       ┌──────────┼──────────┐
       │          │          │
      Auth     Commerce   Order-Payment
                              │
                         Queue-Stock
```

각 서비스가 독립적으로 많은 Connection을 확보하면 전체 Connection 수가 RDS 한도에 가까워질 수 있다고 판단했습니다.

또한 애플리케이션 이외에도

- DB 관리
- 장애 대응
- 모니터링
- 일시적인 Connection 증가

를 위한 Connection이 필요합니다.

따라서 **30개의 Connection을 애플리케이션이 모두 점유하지 않는 것**을 기준으로 삼았습니다.

---

### 4. 해결

서비스별 DB 접근 특성을 기준으로 Connection Pool을 조정했습니다.

DB 접근량이 높은 서비스에는 상대적으로 많은 Connection을 할당하고 인증이나 조회 중심 서비스에는 필요한 수준의 Connection을 할당했습니다.

즉,

```text
모든 서비스의 Pool을 동일하게 증가
```

시키는 것이 아니라

```text
서비스별 DB 접근량
        +
RDS 최대 Connection
        +
운영 여유 Connection
```

을 함께 고려했습니다.

---

### 5. 동일 조건 재테스트

튜닝 효과를 확인하기 위해 조건을 변경하지 않고 다시 테스트했습니다.

```text
Vuser        : 300
Test Duration: 3분
```

| 지표 | 개선 전 | 개선 후 | 변화 |
|---|---:|---:|---:|
| TPS | 599.1 | 약 **850** | **약 +42%** |
| 평균 응답시간 | 456.73ms | 약 **320ms** | **약 -30%** |

TPS는

```text
599.1 → 약 850 TPS
```

로 약 **42% 증가**했고,

평균 응답시간은

```text
456.73ms → 약 320ms
```

로 약 **30% 감소**했습니다.

Grafana에서도 Connection을 획득하지 못하고 대기하는 Pending Connection이 감소하는 것을 확인했습니다.

---

## 이 문제를 통해 얻은 것

처음에는 동시 사용자가 증가하면서 응답시간이 증가했기 때문에 서버 자원 부족을 의심할 수도 있었습니다.

하지만 CPU와 Memory만 보고 서버를 Scale-Up하기보다 실제 메트릭을 확인했습니다.

```text
nGrinder
   │
   ▼
성능 저하 재현
   │
   ▼
Prometheus / Grafana
   │
   ▼
CPU / Memory 정상
   │
   ▼
HikariCP Pool 포화 발견
   │
   ▼
병목 원인 특정
   │
   ▼
Connection Pool 조정
   │
   ▼
동일 조건 재검증
   │
   ▼
TPS +42%
Response Time -30%
```

이를 통해 성능 문제에서는 단순히 서버 자원을 늘리기보다 **부하를 재현하고 메트릭을 통해 병목을 특정한 뒤 동일 조건에서 개선 효과를 검증하는 과정이 중요하다는 것을 확인했습니다.**

---

# 9. Infrastructure

## 왜 EKS를 사용했는가?

백엔드를 여러 서비스로 분리하면서 서비스별 컨테이너의 배포와 실행 상태를 관리할 필요가 있었습니다.

각 서비스를 개별 서버에 직접 배포하기보다 컨테이너 단위로 관리하고,

- 서비스별 배포
- Pod 상태 관리
- Service 기반 내부 통신
- 설정 및 Secret 관리
- 모니터링 연동

을 일관된 방식으로 관리하기 위해 Kubernetes 기반 환경을 구성했습니다.

AWS 환경에서는 EKS를 사용하여 Kubernetes Cluster를 운영했습니다.

---

## 왜 Terraform을 사용했는가?

AWS Console에서 인프라를 직접 생성할 경우 현재 인프라가 어떤 설정으로 만들어졌는지 한눈에 파악하기 어렵고 재구성 과정에서도 동일한 환경을 보장하기 어렵다고 판단했습니다.

따라서

```text
VPC
Subnet
EKS
RDS
ECR
ALB 관련 리소스
```

등의 인프라 구성을 Terraform 코드로 관리했습니다.

```text
modules/
├── vpc
├── eks
├── rds
├── ecr
└── alb
```

이를 통해 인프라 설정을 코드로 확인하고 환경을 재구성할 수 있도록 했습니다.

---

# 10. 주요 API

### Authentication

```http
POST /api/auth/**
GET  /api/users/me
```

### Event / Product

```http
GET /api/events
GET /api/events/{id}

GET /api/products
GET /api/products/{id}

GET /api/event-products/{id}
```

### Queue

```http
POST /api/queue/enter

GET /api/queue/rank
GET /api/queue/status
GET /api/queue/available
GET /api/queue/estimated-time
GET /api/queue/current-users
```

### Order / Payment

```http
POST /api/orders
GET  /api/orders/{id}

POST /api/payments
```

### Admin

```http
GET    /api/admin/dashboard
GET    /api/admin/orders

GET    /api/admin/queue/{id}
PATCH  /api/admin/queue/{id}/open
PATCH  /api/admin/queue/{id}/close
DELETE /api/admin/queue/{id}/reset

GET    /api/admin/stocks
```

---

# 11. 핵심 기술 선택 요약

| 문제 | 선택 | 선택한 이유 |
|---|---|---|
| 이벤트 순간 요청 집중 | **Redis 대기열** | 요청이 주문/DB까지 한 번에 전달되는 것을 제어하기 위해 |
| 대기 순서 및 Rank 관리 | **Redis Sorted Set** | 진입 순서를 유지하면서 빠르게 사용자 순위를 조회하기 위해 |
| 서비스 간 후속 처리 | **Kafka** | 동기 호출 의존도를 낮추고 후속 작업을 비동기로 분리하기 위해 |
| 서비스 책임 분리 | **MSA** | 도메인별 책임과 트래픽 특성을 분리하기 위해 |
| 외부 요청 관리 | **API Gateway** | MSA의 단일 진입점과 인증 처리를 통합하기 위해 |
| 운영 상태 파악 | **Prometheus / Grafana** | 로그만으로 확인하기 어려운 병목을 메트릭으로 분석하기 위해 |
| 성능 한계 검증 | **nGrinder** | 동시 사용자 증가에 따른 실제 처리량과 병목 지점을 확인하기 위해 |
| 컨테이너 운영 | **EKS** | 여러 서비스의 배포와 실행 상태를 일관된 방식으로 관리하기 위해 |
| 인프라 관리 | **Terraform** | AWS 인프라의 구성과 변경 사항을 코드로 관리하기 위해 |

---

# 12. What I Focused On

TEM-ON을 개발하면서 특정 기술을 사용하는 것 자체보다 **왜 이 기술과 구조가 필요한지를 먼저 고민하는 것**에 집중했습니다.

```text
트래픽이 몰린다
    ↓
왜 문제가 되는가?
    ↓
어디에서 제어해야 하는가?
    ↓
Redis 대기열
```

```text
서비스가 분리되어 있다
    ↓
후속 처리를 모두 동기 호출해야 하는가?
    ↓
장애와 지연이 전파될 수 있다
    ↓
Kafka 이벤트 처리
```

```text
응답시간이 증가한다
    ↓
서버가 부족한 것인가?
    ↓
메트릭으로 확인
    ↓
Connection Pool 병목 발견
    ↓
튜닝
    ↓
동일 조건 재검증
```

프로젝트의 목표는 많은 기술을 사용하는 것이 아니라, **문제를 정의하고 필요한 기술을 선택한 뒤 실제 데이터를 통해 그 선택과 개선 결과를 검증하는 것**이었습니다.
