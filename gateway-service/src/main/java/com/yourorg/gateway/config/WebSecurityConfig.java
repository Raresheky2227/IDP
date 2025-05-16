package com.yourorg.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // disable CSRF so POSTs (signup/login) aren’t rejected
                .csrf(csrf -> csrf.disable())
                // allow everything—JWT is enforced in the gateway routes
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());

        return http.build();
    }
}
