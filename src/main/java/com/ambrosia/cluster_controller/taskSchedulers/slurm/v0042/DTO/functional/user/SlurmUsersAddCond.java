package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssociacionCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_users_add_cond
 * SlurmUserAddCond
 */
public record SlurmUsersAddCond(
    
    @JsonInclude(value = Include.NON_NULL)
    List<String> accounts,

    @JsonInclude(value = Include.NON_NULL)
    SlurmAssociacionCondition association,

    @JsonInclude(value = Include.NON_NULL)
    List<String> clusters,

    @JsonInclude(value = Include.NON_NULL)
    List<String> partitions,

    @JsonInclude(value = Include.NON_NULL)
    List<String> users,

    @JsonInclude(value = Include.NON_NULL)
    List<String> wckeys
) {}
