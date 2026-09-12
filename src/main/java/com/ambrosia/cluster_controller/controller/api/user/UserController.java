package com.ambrosia.cluster_controller.controller.api.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {
    @GetMapping("/info")
    public List<String> getUserInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return List.of(userDetails.getUsername(), userDetails.getAuthorities().stream().findFirst().get().getAuthority());
    }
    
}
