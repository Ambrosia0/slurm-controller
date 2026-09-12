package com.ambrosia.cluster_controller.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.config.security.AuthenticationService;
import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.ambrosia.cluster_controller.model.DTO.generic.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody @Validated LoginRequest loginDto,
        HttpServletRequest request,
        HttpServletResponse response) {
        try {
            var token = authenticationService.authenticate(
                loginDto.username(), 
                loginDto.password(),
                request.getHeader("User-Agent"));
            var cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(12000))
                .sameSite("None")
                .build();
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication error");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        HttpServletResponse response,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        var cookie = ResponseCookie.from("AUTH_TOKEN", null)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofSeconds(0))
            .sameSite("None")
            .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/info")
    public ResponseEntity<?> getRole(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(userDetails.getAuthorities().stream().findFirst().get().getAuthority());
    }
}
