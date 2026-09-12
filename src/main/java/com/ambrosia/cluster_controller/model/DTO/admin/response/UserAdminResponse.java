package com.ambrosia.cluster_controller.model.DTO.admin.response;

import java.time.Instant;

public record UserAdminResponse(
    long id,
    String username,
    String password,
    GroupAdminResponse group,
    Instant createdAt
) {}
