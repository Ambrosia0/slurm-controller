package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.fasterxml.jackson.annotation.JsonProperty;


public record SlurmKillTaskResponse(
    @JsonProperty("status")
    List<Status> response,
    SlurmMeta meta,
    List<SlurmError> errors,
    List<SlurmWarning> warnings
) {}


record Status(
    KillRespError error,
    
    @JsonProperty("step_id")
    String step,

    @JsonProperty("job_id")
    SlurmUint32NoVal jobId,

    @JsonProperty("federation")
    String federationSibling // Name of federation sibling (may be empty for non-federation)
){}

record KillRespError(
    String string,
    int code,
    String message
){}