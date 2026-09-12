package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_assoc
 * SlurmAssociation
 */
@Builder 
public record SlurmAssociation(

    @JsonInclude(value = Include.NON_NULL)
    String account,

    @JsonInclude(value = Include.NON_NULL)
    String cluster,

    @JsonInclude(value = Include.NON_NULL)
    SlurmAssocMax max,

    @JsonInclude(value = Include.NON_NULL)
    Integer id,

    @JsonProperty("is_default")
    @JsonInclude(value = Include.NON_NULL)
    Boolean isDefault,

    @JsonInclude(value = Include.NON_NULL)
    SlurmAssocMin min,

    @JsonProperty("parent_account")
    @JsonInclude(value = Include.NON_NULL)
    String parentAccount,

    @JsonInclude(value = Include.NON_NULL)
    String partition,

    @JsonInclude(value = Include.NON_NULL)
    Integer priority,

    @JsonInclude(value = Include.NON_NULL)
    List<String> qos,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("shares_raw")
    Integer sharesRaw,

    String user
) {}
