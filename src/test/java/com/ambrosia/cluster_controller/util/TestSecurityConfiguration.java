package com.ambrosia.cluster_controller.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration 
public class TestSecurityConfiguration{
    @Primary 
    @Bean 
    SecurityFilterChain security(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                    http -> http
                        .requestMatchers("/api/user/info").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user", "/api/user/**").hasRole("ADMIN")
                        .requestMatchers("/api/group", "/api/group/**").hasRole("ADMIN")
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico", "/manifest.json").permitAll()
                        .anyRequest().authenticated()
            )
            .build();
    }
}