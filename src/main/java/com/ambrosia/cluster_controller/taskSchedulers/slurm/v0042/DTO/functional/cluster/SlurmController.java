package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.cluster;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmController(
    @JsonProperty("host")
    String host,

    @JsonProperty("port")
    Integer port
) {}
