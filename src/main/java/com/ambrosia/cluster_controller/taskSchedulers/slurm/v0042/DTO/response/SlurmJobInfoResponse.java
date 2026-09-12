package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint64NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmJobInfoResponse(
    List<SlurmJobInfo> jobs,

    @JsonAlias("last_backfill")
    SlurmUint64NoVal lastBackfill,

    @JsonAlias("last_update")
    SlurmUint64NoVal lastUpdate,

    @JsonProperty("meta")
    SlurmMeta slurmMeta,

    @JsonProperty("errors")
    List<SlurmError> slurmErrors,

    @JsonProperty("warnings")
    List<SlurmWarning> SlurmWarnings
) {}
