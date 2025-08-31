package com.library_services.gateway_server.filter;

import com.library_services.gateway_server.service.UserService;
import com.library_services.gateway_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

   private final JwtUtil jwtUtil;
     private final UserService userService;

     public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
         this.jwtUtil = jwtUtil;
         this.userService = userService;

     }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        String token = authHeader.substring(7);
        String username= jwtUtil.extractUsername(token,false);

        if(username != null){
            UserDetails  userDetails= userService.loadUserByUsername(username);
            if(jwtUtil.validateToken(token,false)){
                Authentication authToken=new UsernamePasswordAuthenticationToken(
                  userDetails,null,userDetails.getAuthorities()
                );

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
            }
        }


        return chain.filter(exchange);
    }
}
