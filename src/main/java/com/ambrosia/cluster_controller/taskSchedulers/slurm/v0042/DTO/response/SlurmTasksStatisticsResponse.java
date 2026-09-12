package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmTasksStatisticsResponse(
    @JsonProperty("jobs_submitted")
    int jobsSubmitted,

    @JsonProperty("jobs_started")
    int jobsStarted,

    @JsonProperty("jobs_completed")
    int jobsCompleted,

    @JsonProperty("jobs_canceled")
    int jobsCancelled,

    @JsonProperty("jobs_failed")
    int jobsFailed,

    @JsonProperty("jobs_running")
    int jobsRunning

) {}
