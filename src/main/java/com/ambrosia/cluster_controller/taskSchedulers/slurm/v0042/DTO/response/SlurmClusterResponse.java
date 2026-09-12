package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @see https://slurm.schedmd.com/rest_api.html#slurmdbV0045PostClusters
 * SlurmClusterResponse
 * @param clusters
 * @param meta
 * @param errors
 * @param warnings
 */
public record SlurmClusterResponse(
    @JsonProperty("clusters")
    List<SlurmClusterRec> clusters,

    @JsonProperty("meta")
    SlurmMeta meta,

    @JsonProperty("erors")
    List<SlurmError> errors,

    @JsonProperty("warnings")
    List<SlurmWarning> warnings
) {}
