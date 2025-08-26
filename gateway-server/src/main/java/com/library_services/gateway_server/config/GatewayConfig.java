package com.library_services.gateway_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customConfig(RouteLocatorBuilder builder){
        return builder.routes().route("user-service",r -> r.path("/users/**")
                .uri("lb://user-service")).build();
    }
}
