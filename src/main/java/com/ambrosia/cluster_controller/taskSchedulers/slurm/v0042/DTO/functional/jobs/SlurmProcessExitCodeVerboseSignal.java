package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint16NoVal;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record SlurmProcessExitCodeVerboseSignal(
    @JsonInclude(value = Include.NON_NULL)
    SlurmUint16NoVal id,

    @JsonInclude(value = Include.NON_NULL)
    String name
) {}
