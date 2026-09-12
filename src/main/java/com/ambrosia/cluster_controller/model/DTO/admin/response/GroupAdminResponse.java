package com.ambrosia.cluster_controller.model.DTO.admin.response;

import java.time.Instant;

public record GroupAdminResponse(
    long id,
    String name,
    Instant createdAt
) {}
