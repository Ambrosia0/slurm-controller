package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_tres
 * SlurmTres
 * @param tresType
 * @param name
 * @param id
 * @param count
 */
@Builder 
public record SlurmTres(
    String type,

    @JsonProperty("name")
    @JsonInclude(value = Include.NON_NULL)
    String name,

    @JsonProperty("id")
    @JsonInclude(value = Include.NON_NULL)
    Integer id,

    @JsonProperty("count")
    @JsonInclude(value = Include.NON_NULL)
    Long count
) {}


