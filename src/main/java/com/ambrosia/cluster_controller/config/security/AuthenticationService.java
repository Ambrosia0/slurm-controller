package com.ambrosia.cluster_controller.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.security.tokenManager.JwtManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    
    private final JwtManager jwtManager;

    public String authenticate(String username, String password, String userAgent) throws Exception{
        var authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication auth = authenticationManager.authenticate(authentication);
        return jwtManager.createAccessToken(auth, userAgent);
    }
}
