package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmRemoveAssocResponse(
    @JsonProperty("removed_associations")
    List<String> removedAssociacions,

    @JsonProperty("meta")
    SlurmMeta meta,

    @JsonProperty("errors")
    List<SlurmError> errors,

    @JsonProperty("warnings")
    List<SlurmWarning> warnings
) {}