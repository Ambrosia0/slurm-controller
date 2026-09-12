package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmRecAssoc(
    @JsonProperty("root")
    SlurmAssocShort root
) {
}