package com.ambrosia.cluster_controller.model.DTO.user.response;

public record UserResponse(
    long id,
    String username,
    String group
) {}
