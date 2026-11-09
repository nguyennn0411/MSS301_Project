package com.fucar.gateway.filter;

import com.fucar.gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final JwtService jwtService;

    public JwtAuthorizationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = jwtService.validateToken(header.substring(7));
            String role = claims.get("role", String.class);
            Long userId = readUserId(claims);
            if (!isAllowed(path, method, role, userId)) {
                return reject(exchange, HttpStatus.FORBIDDEN);
            }

            ServerHttpRequest mutatedRequest = withIdentityHeaders(request, userId, role);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception ex) {
            log.warn("JWT authorization failed for {} {}", method, path, ex);
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublic(String path, HttpMethod method) {
        if (path.equals("/actuator/health") && method == HttpMethod.GET) {
            return true;
        }
        if (path.equals("/api/auth/login")) {
            return true;
        }
        if (path.equals("/api/customers/register") && method == HttpMethod.POST) {
            return true;
        }
        return method == HttpMethod.GET && (
                path.equals("/api/cars")
                        || path.equals("/api/cars/available")
                        || path.matches("/api/cars/\\d+")
        );
    }

    private boolean isAllowed(String path, HttpMethod method, String role, Long userId) {
        if ("ADMIN".equals(role)) {
            return true;
        }
        if (!"CUSTOMER".equals(role)) {
            return false;
        }
        if (path.equals("/api/rentings") && method == HttpMethod.POST) {
            return true;
        }
        if (path.equals("/api/rentings/my-history") && method == HttpMethod.GET) {
            return true;
        }
        if (path.matches("/api/rentings/\\d+") && method == HttpMethod.GET) {
            return true;
        }
        if (path.matches("/api/rentings/\\d+/cancel") && method == HttpMethod.PATCH) {
            return true;
        }
        if ((method == HttpMethod.GET || method == HttpMethod.PUT) && path.matches("/api/customers/\\d+")) {
            Long pathId = Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
            return pathId.equals(userId);
        }
        return List.of("/api/customers/profile", "/api/customers/me").contains(path)
                && (method == HttpMethod.GET || method == HttpMethod.PUT);
    }

    private Long readUserId(Claims claims) {
        Object claim = claims.get("userId");
        if (claim instanceof Number number) {
            return number.longValue();
        }
        if (claim != null) {
            return Long.valueOf(claim.toString());
        }
        throw new IllegalArgumentException("JWT does not contain userId");
    }

    private ServerHttpRequest withIdentityHeaders(ServerHttpRequest request, Long userId, String role) {
        HttpHeaders mutableHeaders = new HttpHeaders();
        mutableHeaders.putAll(request.getHeaders());
        mutableHeaders.set("X-User-Id", String.valueOf(userId));
        mutableHeaders.set("X-User-Role", role);
        HttpHeaders readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(mutableHeaders);

        return new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return readOnlyHeaders;
            }
        };
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}
