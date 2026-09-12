package com.ambrosia.cluster_controller.config.security.tokenManager;

import java.time.Instant;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JwtManager {
    private final AppConfigurationProperties appConfigurationProperties;    
    private final JwtVerifier jwtVerifier;

    public JwtManager(
            JwtVerifier jwtVerifier,
            AppConfigurationProperties appConfigurationProperties
    ){
        this.jwtVerifier = jwtVerifier;
        this.appConfigurationProperties = appConfigurationProperties;
    }

    public String createAccessToken(Authentication authentication, String userAgent){
        var userDetails = (CustomUserDetails)authentication.getPrincipal();
        var now = Instant.now();
        
        var claims = new JWTClaimsSet.Builder()
            .subject(Long.toString(userDetails.getId()))
            .claim("username", userDetails.getUsername())
            .claim("role", userDetails.getAuthorities().stream().findFirst().get())
            .claim("user-agent", userAgent)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(appConfigurationProperties.getAccessTokenDuration())))
            .build();
        return jwtVerifier.signJwt(claims).serialize();
    }

    public String createRefreshToken(Authentication authentication, String userAgent){
        var userDetails = (CustomUserDetails)authentication.getPrincipal();
        var now = Instant.now();
        
        var claims = new JWTClaimsSet.Builder()
            .subject(Long.toString(userDetails.getId()))
            .claim("username", userDetails.getUsername())
            .claim("role", userDetails.getAuthorities().stream().findFirst().get())
            .claim("user-agent", userAgent)
            .claim("refresh", true)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(appConfigurationProperties.getRefreshTokenDuration())))
            .build();
        return jwtVerifier.signJwt(claims).serialize();
    }

    public boolean validateToken(String token){
        try{
            jwtVerifier.verify(SignedJWT.parse(token));
            return true;
        } catch(Exception e){
            return false;
        }
    }
}
