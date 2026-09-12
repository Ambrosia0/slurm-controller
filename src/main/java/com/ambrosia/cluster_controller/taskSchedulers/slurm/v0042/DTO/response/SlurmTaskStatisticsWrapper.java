package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmTaskStatisticsWrapper(
    @JsonProperty("statistics")
    SlurmTasksStatisticsResponse statistics
){}
