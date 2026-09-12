package com.ambrosia.cluster_controller.model.DTO.admin.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmNode;

public record StatisticsResponse(
    int jobsSubmitted,
    int jobsStarted,
    int jobsCompleted,
    int jobsCancelled,
    int jobsFailed,
    int jobsRunning,
    LocalDateTime timestamp,
    List<SlurmNode> nodes
) {}