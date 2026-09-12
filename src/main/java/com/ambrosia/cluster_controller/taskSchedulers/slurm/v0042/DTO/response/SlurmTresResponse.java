package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.fasterxml.jackson.annotation.JsonAlias;

public record SlurmTresResponse(
    @JsonAlias("TRES")
    List<SlurmTres> tres,

    SlurmMeta meta,
    List<SlurmError> errors,
    List<SlurmWarning> warnings
) {}
