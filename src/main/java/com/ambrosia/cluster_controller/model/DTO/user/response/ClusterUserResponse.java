package com.ambrosia.cluster_controller.model.DTO.user.response;

public record ClusterUserResponse(
    long id,
    String hostname,
    String displayedName
) {}
