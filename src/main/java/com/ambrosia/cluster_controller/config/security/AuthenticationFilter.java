package com.ambrosia.cluster_controller.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ambrosia.cluster_controller.config.security.tokenManager.JwtManager;
import com.nimbusds.jwt.SignedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Cookie authCookie = null;
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("AUTH_TOKEN")) {
                    authCookie = cookie;
                    break;
                }
            }
            if (authCookie == null) {
                filterChain.doFilter(request, response);
                return;
            }
            
            String token = authCookie.getValue();
            var jwt = SignedJWT.parse(token);

            if(!jwtManager.validateToken(token))
                throw new RuntimeException("Invalid token signature!");

            var claims = jwt.getJWTClaimsSet().getClaims();

            var username = (String)claims.get("username");
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);

                if (claims.get("user-agent").equals(request.getHeader("User-Agent")) 
                        && Long.parseLong(jwt.getJWTClaimsSet().getSubject()) == userDetails.getId()) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
