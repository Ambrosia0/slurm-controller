package com.ambrosia.cluster_controller.model.DTO.scheduler;

public record ProfileTask(
    Long userId,
    String username,
    String password
) {}
