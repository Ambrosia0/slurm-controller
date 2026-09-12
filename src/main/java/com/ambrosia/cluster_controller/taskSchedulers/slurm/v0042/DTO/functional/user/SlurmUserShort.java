package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_user_short
 * SlurmUserShort
 * @param adminLevel
 * @param defaultQos
 * @param defaultAccount
 * @param defaultWCkey
 */
public record SlurmUserShort(
    @JsonProperty("adminlevel")
    @JsonInclude(value = Include.NON_NULL)
    List<String> adminLevel,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("defaultqos")
    Integer defaultQos,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("defaultaccount")
    String defaultAccount,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("defaultwckey")
    String defaultWCkey
) {}
