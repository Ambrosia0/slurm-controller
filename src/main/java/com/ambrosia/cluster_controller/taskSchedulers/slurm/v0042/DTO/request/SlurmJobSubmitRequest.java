package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request;

import java.util.List;

import com.ambrosia.cluster_controller.util.StringSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;

@Builder 
public record SlurmJobSubmitRequest(
    // @JsonSerialize(using = StringSerializer.class)
    @JsonInclude(value = Include.NON_NULL)
    String script,

    @JsonInclude(value = Include.NON_NULL)
    List<SlurmJobSubmit> jobs,

    @JsonInclude(value = Include.NON_NULL)
    SlurmJobSubmit job
) {}
