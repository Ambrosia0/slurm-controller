package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

@Builder 
public record SlurmUint32NoVal(
    @JsonInclude(value = Include.NON_NULL)
    Boolean set,

    @JsonInclude(value = Include.NON_NULL)
    Boolean infinite,
    
    @JsonInclude(value = Include.NON_NULL)
    Integer number
){}
