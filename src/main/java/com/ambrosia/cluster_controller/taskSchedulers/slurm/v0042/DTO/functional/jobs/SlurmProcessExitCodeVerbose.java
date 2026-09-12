package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record SlurmProcessExitCodeVerbose(
    @JsonInclude(value = Include.NON_NULL)
    List<String> status,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("return_code")
    SlurmUint32NoVal returnCode,
    
    @JsonInclude(value = Include.NON_NULL)
    SlurmProcessExitCodeVerboseSignal signal
) {}
