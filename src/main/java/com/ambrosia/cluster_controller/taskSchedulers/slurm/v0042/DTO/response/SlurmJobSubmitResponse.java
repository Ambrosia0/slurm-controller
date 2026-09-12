package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.fasterxml.jackson.annotation.JsonProperty;


public record SlurmJobSubmitResponse(
    @JsonProperty("job_id")
    Integer jobId,

    @JsonProperty("step_id")
    String stepId,

    @JsonProperty("job_submit_user_msg")
    String jobSubmitUserMessage,

    @JsonProperty("meta")
    SlurmMeta slurmMeta,

    @JsonProperty("errors")
    List<SlurmError> slurmErrors,

    @JsonProperty("warnings")
    List<SlurmWarning> SlurmWarnings
) {}
