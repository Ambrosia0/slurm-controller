package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssociacionCondition;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user.SlurmUserShort;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @see https://slurm.schedmd.com/rest_api.html#slurmdbV0045PostUsersAssociation
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_openapi_users_add_cond_resp
 * SlurmUsersAccosications
 */
public record SlurmUsersAccosications(
    @JsonProperty("association_condition")
    SlurmAssociacionCondition associacionCondition,

    SlurmUserShort user,

    @JsonInclude(value = Include.NON_NULL)
    SlurmMeta meta,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmError> errors,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmWarning> warnings
) {}
