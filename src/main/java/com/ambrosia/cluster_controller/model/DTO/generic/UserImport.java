package com.ambrosia.cluster_controller.model.DTO.generic;

import com.ambrosia.cluster_controller.model.entity.Group;

import jakarta.validation.constraints.Pattern;

public record UserImport(

    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,32}$")
    String username,

    // @Pattern(regexp = "^(?=(.*[A-Z]){2})(?=.*[!@#$&*])(?=(.*[0-9]){2})(?=(.*[a-z]){3})[A-Za-z0-9!@#$&*]{8,255}$")
    @Pattern(regexp = "^[A-Za-z0-9!@#$&*]{12,255}$")
    String password,

    Group group
) {}