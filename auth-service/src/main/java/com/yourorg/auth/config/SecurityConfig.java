package com.yourorg.auth.config;

import com.yourorg.auth.security.JwtAuthenticationFilter;
import com.yourorg.auth.security.JwtAuthorizationFilter;
import com.yourorg.auth.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter authFilter,
                                           JwtAuthorizationFilter authzFilter) throws Exception {

        http
                // 1) Disable CSRF for our stateless REST API
                .csrf(csrf -> csrf.disable())

                // 2) Stateless sessions
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3) REMOVE CORS CONFIG HERE!

                // 4) Authorize: allow signup/login, secure everything else
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 5) JWT filters
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(authzFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // DELETE THIS WHOLE BEAN:
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     ...
    // }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http,
                                             UserService userService,
                                             PasswordEncoder encoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
