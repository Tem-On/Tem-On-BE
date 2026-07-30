# 빌드 단계
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /workspace

ARG SERVICE

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

COPY temon-common temon-common
COPY temon-gateway temon-gateway
COPY temon-auth-service temon-auth-service
COPY temon-commerce-service temon-commerce-service
COPY temon-order-payment-service temon-order-payment-service
COPY temon-queue-stock-service temon-queue-stock-service

RUN chmod +x gradlew

RUN ./gradlew ":${SERVICE}:bootJar" \
    --no-daemon \
    -x test

RUN JAR_FILE=$(find "${SERVICE}/build/libs" \
        -maxdepth 1 \
        -type f \
        -name "*.jar" \
        ! -name "*-plain.jar" \
        | head -n 1) \
    && test -n "${JAR_FILE}" \
    && cp "${JAR_FILE}" /app.jar


# 실행 단계
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
