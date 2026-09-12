package com.ambrosia.cluster_controller.controller.api.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.ambrosia.cluster_controller.model.DTO.admin.request.UserAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.UserFilter;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserManageService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminUserController {
    private final UserManageService userService;

    @PostMapping
    public void create(@RequestBody @Valid UserAdminRequest request) {
        userService.createUser(request);
    }
    
    @PatchMapping("/{userId}")
    public void update(
            @PathVariable Long userId,
            @RequestBody @Valid UserAdminRequest request){
        userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void delete(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.deleteById(userId);
    }

    @GetMapping
    public Page<UserAdminResponse> get(
            @ModelAttribute UserFilter userFilter,
            @PageableDefault(size = 20) Pageable pageable) {
        return userService.searchUsers(userFilter, pageable);
    }
    
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        userService.exportUsers(response);
    }

    @PostMapping("/import")
    public void importUsers(@RequestParam MultipartFile file) {
        userService.importUsers(file);
    }
}
