package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0_0_45_cluster_rec_controller
 * SlurmController
 * @param host
 * @param port
 */
@Builder 
public record SlurmController(
    @JsonProperty("host")
    String host,

    @JsonProperty("port")
    int port
) {}
