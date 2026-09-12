package com.ambrosia.cluster_controller.model.DTO.admin.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GroupAdminRequest(

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,32}$")
    String name
) {}

