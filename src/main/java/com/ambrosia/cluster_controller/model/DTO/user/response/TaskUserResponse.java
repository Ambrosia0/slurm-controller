package com.ambrosia.cluster_controller.model.DTO.user.response;

import java.time.LocalDateTime;

import com.ambrosia.cluster_controller.util.TaskStatus;

public record TaskUserResponse(
    long id,
    String user,
    long appointedId,
    String workDir,
    TaskStatus status,
    LocalDateTime createdAt
) {}
