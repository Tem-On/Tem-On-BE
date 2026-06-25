package com.example.temon.gateway.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtProvider jwtProvider;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();

		if (isPermitAll(path)) {
			return chain.filter(exchange);
		}

		String token = resolveToken(exchange.getRequest());

		if (token == null || !jwtProvider.validateToken(token)) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		Long userId = jwtProvider.getUserId(token);
		String role = jwtProvider.getRole(token);

		ServerHttpRequest request = exchange.getRequest()
				.mutate()
				.header("X-User-Id", String.valueOf(userId))
				.header("X-User-Role", role)
				.build();

		return chain.filter(exchange.mutate().request(request).build());
	}

	private String resolveToken(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}

	private boolean isPermitAll(String path) {
		return path.startsWith("/api/auth")
				|| path.startsWith("/swagger-ui")
				|| path.startsWith("/v3/api-docs")
				|| path.startsWith("/webjars")
				|| path.startsWith("/favicon.ico");
	}
}