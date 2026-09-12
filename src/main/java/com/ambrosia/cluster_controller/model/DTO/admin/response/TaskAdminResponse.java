package com.ambrosia.cluster_controller.model.DTO.admin.response;

import java.time.LocalDateTime;

import com.ambrosia.cluster_controller.util.TaskStatus;

public record TaskAdminResponse(
    long id,
    UserAdminResponse user,
    long appointedId,
    String workDir,
    TaskStatus status,
    LocalDateTime createdAt
) {}
