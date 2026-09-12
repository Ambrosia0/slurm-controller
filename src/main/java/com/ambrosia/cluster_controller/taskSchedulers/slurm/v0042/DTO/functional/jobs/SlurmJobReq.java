package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint64NoVal;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmJobReq(
    @JsonProperty("CPUs")
    Integer cpus,

    @JsonAlias("memory_per_cpu")
    SlurmUint64NoVal memoryPerCpu,

    @JsonAlias("memory_per_node")
    SlurmUint64NoVal memoryPerNode
) {}
