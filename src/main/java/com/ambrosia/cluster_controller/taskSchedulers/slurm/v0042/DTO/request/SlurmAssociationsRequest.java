package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmError;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmMeta;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWarning;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssociation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#slurmdbV0045PostAssociations
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_openapi_assocs_resp
 * SlurmAccosications
 */
@Builder
public record SlurmAssociationsRequest(
    List<SlurmAssociation> associations,

    @JsonInclude(value = Include.NON_NULL)
    SlurmMeta meta,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmError> errors,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmWarning> warnings
) {}
