package com.ambrosia.cluster_controller.model.DTO.generic;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull
    String username,
    
    @NotNull
    String password
) {}
