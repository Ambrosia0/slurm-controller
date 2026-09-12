package com.ambrosia.cluster_controller.model.DTO.filters;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder 
public record UserFilter(
    @Size(min = 3) String username,
    Long clusterId,
    String bindedCluster,
    Long groupId,
    Boolean notInCluster,
    Boolean ungrouped
) {
    public UserFilter{
        if(notInCluster == null)
            notInCluster = false;
        if(ungrouped == null)
            ungrouped = false;
    }
}
