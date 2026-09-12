package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_cluster_rec
 * SlurmClusterRec
 * @param controller
 * @param name
 * @param nodes
 * @param associations
 * @param slurmVersion
 * @param tres
 */
@Builder 
public record SlurmClusterRec(
    @JsonProperty("controller")
    SlurmController controller,

    @JsonProperty("name")
    String name,

    @JsonProperty("nodes")
    String nodes,

    @JsonProperty("associations")
    SlurmRecAssoc associations,

    @JsonProperty("rpc_version")
    Integer slurmVersion,

    @JsonProperty("tres")
    List<SlurmTres> tres
) {}
