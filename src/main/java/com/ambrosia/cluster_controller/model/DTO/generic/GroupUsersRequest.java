package com.ambrosia.cluster_controller.model.DTO.generic;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record GroupUsersRequest(
    @NotNull
    Set<Long> userIds
) {}