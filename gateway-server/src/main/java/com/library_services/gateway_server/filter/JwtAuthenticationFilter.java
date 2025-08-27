package com.library_services.gateway_server.filter;

import com.library_services.gateway_server.util.JwtUtil;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
      String path=  exchange.getRequest().getPath().value();
      if(path.startsWith("/auth/") || path.startsWith("/actuator/health")) {
          return chain.filter(exchange);
      }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // optionally add user info to header forwarded to downstream
        String username = jwtUtil.extractUsername(token);
        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.headers(h -> h.add("X-User-Name", username)))
                .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
