package com.ambrosia.cluster_controller.model.DTO.admin.request;

import java.util.List;

import jakarta.validation.Valid;

public record ProfileRequestWrapper(
    List<@Valid ClusterProfileAdminRequest> requests
) {}