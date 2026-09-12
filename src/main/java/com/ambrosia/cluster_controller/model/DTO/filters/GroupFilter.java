package com.ambrosia.cluster_controller.model.DTO.filters;

import lombok.Builder;

@Builder
public record GroupFilter(
    String name,
    Long clusterId,
    String bindedCluster,
    Boolean notInCluster
) {
    public GroupFilter{
        if(notInCluster == null)
            notInCluster = false;
    }
}
