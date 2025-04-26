package com.raresheky.apigateway.config;

import com.raresheky.apigateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder,
                                     AuthenticationFilter authFilter) {
        return builder.routes()
                // Public auth endpoints
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("lb://USER-AUTH-SERVICE"))
                // All /users/** require JWT
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://USER-AUTH-SERVICE"))
                // All /events/** require JWT
                .route("event-manager", r -> r
                        .path("/events/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://EVENT-MANAGER-SERVICE"))
                .build();
    }
}
