package com.yourorg.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yourorg.gateway.security.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${event.service.url}")
    private String eventServiceUrl;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,
                                      JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri(authServiceUrl)
                )
                .route("user-service", r -> r
                        .path("/api/users/**")
//                        .filters(f -> f.filter(jwtFilter))
                        .uri(authServiceUrl)
                )
                .route("event-service", r -> r
                        .path("/api/events/**")
//                        .filters(f -> f.filter(jwtFilter))
                        .uri(eventServiceUrl)
                )
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("http://notification-service:8083")
                )
                .build();
    }
}
