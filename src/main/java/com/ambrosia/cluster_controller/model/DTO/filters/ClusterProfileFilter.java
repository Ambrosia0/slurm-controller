package com.ambrosia.cluster_controller.model.DTO.filters;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ClusterProfileFilter(
    Long groupId,
    Long clusterId,
    String bindedCluster,
    @Size(min = 3)
    String username
) {}
