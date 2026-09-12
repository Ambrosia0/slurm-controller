package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record SlurmJobTres(
    @JsonInclude(value = Include.NON_NULL)
    List<SlurmTres> allocated,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmTres> requested
) {}
