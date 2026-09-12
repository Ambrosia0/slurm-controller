
package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmWCKey;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmAssocShort;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmCoordinator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * https://slurm.schedmd.com/rest_api.html#v0.0.45_user
 * SlurmUser
 * @param administratorLevel
 * @param associations
 * @param coordinators
 * @param userDefault
 * @param flags
 * @param name
 * @param oldName
 * @param wcKeys
 */
@Builder
public record SlurmUser(
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("administrator_level")
    List<String> administratorLevel,


    @JsonInclude(value = Include.NON_NULL)
    List<SlurmAssocShort> associations,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmCoordinator> coordinators,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("default")
    SlurmUserDefault userDefault,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("flags")
    List<String> flags,

    String name,

    @JsonInclude(value = Include.NON_NULL)
    String oldName,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmWCKey> wckey
) {}
