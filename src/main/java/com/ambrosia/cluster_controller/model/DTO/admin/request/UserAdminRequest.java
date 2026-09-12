package com.ambrosia.cluster_controller.model.DTO.admin.request;

import jakarta.validation.constraints.Pattern;

public record UserAdminRequest(
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,32}$")
    String username,

    @Pattern(regexp = "^[A-Za-z0-9!@#$&*]{12,255}$")
    String password,

    Long groupId
) {}
