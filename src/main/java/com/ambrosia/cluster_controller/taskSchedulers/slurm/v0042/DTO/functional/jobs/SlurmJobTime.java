package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record SlurmJobTime(
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("elapsed")
    Integer elapsed,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("end")
    Long endTime,

    @JsonInclude(value = Include.NON_NULL)
    Long submission,

    @JsonInclude(value = Include.NON_NULL)
    Long eligible,

    @JsonInclude(value = Include.NON_NULL)
    Long suspended,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("start")
    Long start
) {}
