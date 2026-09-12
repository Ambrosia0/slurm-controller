package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import java.util.List;

public record SlurmJobState(
    List<String> current,
    String reason // Reason for previous Pending or Failed state
){}
