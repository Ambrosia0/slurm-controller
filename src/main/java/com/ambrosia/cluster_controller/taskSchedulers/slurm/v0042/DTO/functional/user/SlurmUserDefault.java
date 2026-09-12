package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

@Builder
public record SlurmUserDefault(
    @JsonInclude(value = Include.NON_NULL)
    Integer qos,

    @JsonInclude(value = Include.NON_NULL)
    String account,

    @JsonInclude(value = Include.NON_NULL)
    String wcKey
){}