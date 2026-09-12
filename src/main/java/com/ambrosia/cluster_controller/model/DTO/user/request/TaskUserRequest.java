package com.ambrosia.cluster_controller.model.DTO.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record TaskUserRequest(
    @Null
    long id,

    @NotNull
    long clusterId
) {}
