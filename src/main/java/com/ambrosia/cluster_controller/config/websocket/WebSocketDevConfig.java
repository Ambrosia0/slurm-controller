package com.ambrosia.cluster_controller.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ambrosia.cluster_controller.controller.api.ws.AdminTerminalHandler;
import com.ambrosia.cluster_controller.controller.api.ws.ServerTerminal;
import com.ambrosia.cluster_controller.controller.api.ws.StatisticsHandler;
import com.ambrosia.cluster_controller.controller.api.ws.UserTerminalHandler;

import lombok.RequiredArgsConstructor;

// Веб-сокеты для динамического обновления данных с кластеров
@Configuration
@RequiredArgsConstructor
@EnableWebSocket
@Profile("dev")
public class WebSocketDevConfig implements WebSocketConfigurer {
    private final UserTerminalHandler userTerminalHandler;

    private final AdminTerminalHandler adminTerminalHandler;

    private final ServerTerminal serverTerminal;

    private final StatisticsHandler statisticsHandler;

    private final JwtInterceptor jwtInterceptor;

    private final ClusterInterceptor clusterInterceptor;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(userTerminalHandler, "/api/ws/user/terminal")
            .addInterceptors(jwtInterceptor, clusterInterceptor)
            .setAllowedOrigins("*");
        registry.addHandler(serverTerminal, "/api/ws/terminal")
            .addInterceptors(jwtInterceptor)
            .setAllowedOrigins("*");
        registry.addHandler(adminTerminalHandler, "/api/ws/admin/terminal")
            .addInterceptors(jwtInterceptor, clusterInterceptor)
            .setAllowedOrigins("*");
        registry.addHandler(statisticsHandler, "/api/ws/admin/statistics")
            .addInterceptors(jwtInterceptor, clusterInterceptor)
            .setAllowedOrigins("*");
    }

}
