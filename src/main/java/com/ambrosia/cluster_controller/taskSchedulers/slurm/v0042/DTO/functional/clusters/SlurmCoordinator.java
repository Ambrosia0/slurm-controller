package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters;

public record SlurmCoordinator(
    String name,
    Boolean direct
) {}
