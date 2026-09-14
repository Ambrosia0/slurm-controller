package com.ambrosia.cluster_controller.util.security;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;

public class WithMockCustomUserSecurityContextFactory 
        implements WithSecurityContextFactory<WithMockCustomUser>{
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        var context = SecurityContextHolder.createEmptyContext();

        var principal = new CustomUserDetails(
            annotation.id(), 
            annotation.username(), 
            annotation.password(),
            true,
            annotation.role()
        );

        var authority = new SimpleGrantedAuthority("ROLE_"+annotation.role());
        var auth = new UsernamePasswordAuthenticationToken(
            principal,
            principal.getPassword(),
            List.of(authority)
        );

        context.setAuthentication(auth);
        return context;
    }
    
}
