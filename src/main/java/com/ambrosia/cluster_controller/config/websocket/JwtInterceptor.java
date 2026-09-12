package com.ambrosia.cluster_controller.config.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ambrosia.cluster_controller.config.security.CustomUserDetailsService;
import com.ambrosia.cluster_controller.config.security.tokenManager.JwtManager;
import com.nimbusds.jwt.SignedJWT;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtInterceptor implements HandshakeInterceptor {
    private final CustomUserDetailsService customUserDetailsService;

    private final JwtManager jwtManager;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest servletRequest){
            var httpServletRequest = servletRequest;
            var cookies = httpServletRequest.getServletRequest().getCookies();
            if(cookies == null){
                return false;
            }
            for(Cookie cookie: cookies){
                if("AUTH_TOKEN".equals(cookie.getName())){
                    String token = cookie.getValue();
                    if(!jwtManager.validateToken(token))
                        return false;

                    var claims = SignedJWT.parse(token).getJWTClaimsSet();

                    if(!(claims.getStringClaim("user-agent")).equals(httpServletRequest.getHeaders().getFirst("User-Agent"))){
                        return false;
                    }
                    attributes.put("user", customUserDetailsService.loadUserByUsername(claims.getStringClaim("username")));
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                WebSocketHandler wsHandler, Exception exception) {
    }
}
