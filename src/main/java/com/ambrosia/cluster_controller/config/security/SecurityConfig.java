package com.ambrosia.cluster_controller.config.security;

import java.net.http.HttpClient;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.DefaultConfigFileHostEntryResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.config.security.tokenManager.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@EnableConfigurationProperties(AppConfigurationProperties.class)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
                HttpSecurity httpSecurity, 
                JwtManager jwtManager, 
                CustomUserDetailsService customUserDetailsService) throws Exception{
        var filter = new AuthenticationFilter(jwtManager, customUserDetailsService);
        return httpSecurity
            .cors(Customizer.withDefaults())
            .csrf(
                csrf -> csrf
                    .disable())
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
            .formLogin(
                formLogin -> formLogin
                    .disable()
            )
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(
                httpBasic -> httpBasic
                    .disable()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    TextEncryptor textEncryptor(AppConfigurationProperties appConfigurationProperties){
        return Encryptors.text(
            appConfigurationProperties.getPasswordEncryptionKey(), 
            appConfigurationProperties.getPasswordEncryptionSalt()
        );
    }
    

    @Bean
    SshClient sshClient(){
        var sshClient = SshClient.setUpDefaultClient();
        var hostEntryResolver = new DefaultConfigFileHostEntryResolver(true);
        sshClient.setHostConfigEntryResolver(hostEntryResolver);
        sshClient.start();
        return sshClient;
    }

    @Bean
    RestClient restClient(ObjectMapper objectMapper){
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        var factory = new JdkClientHttpRequestFactory(jdkHttpClient);
        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
        return restClient;
    }
}
