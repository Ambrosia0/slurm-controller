package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmError(
    @JsonProperty("description")
    String description,

    @JsonProperty("error_number")
    int errorNumber,

    @JsonProperty("error")
    String error, // Short form error description
    
    @JsonProperty("source")
    String source
) {}