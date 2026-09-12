package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user.SlurmUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

@Builder
public record SlurmUserPost(
    List<SlurmUser> users,

    @JsonInclude(value = Include.NON_NULL)
    SlurmMeta meta,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmError> errors,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmWarning> warnings
) {}
