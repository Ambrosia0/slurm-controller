package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmAssocShort(
    @JsonProperty("account")
    String account,

    @JsonProperty("cluster")
    String cluster,

    @JsonProperty("partition")
    String partition,

    @JsonProperty("user")
    String user,

    @JsonProperty("id")
    int id
) {}
