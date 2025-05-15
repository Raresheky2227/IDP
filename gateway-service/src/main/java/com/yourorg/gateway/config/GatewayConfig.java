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

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,
                                      JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                // Public auth endpoints (login/signup) → Auth service
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(authServiceUrl)
                )
                // Secured user endpoints → Auth service, with JWT filter
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .stripPrefix(1)
                        )
                        .uri(authServiceUrl)
                )
                .build();
    }
}
