package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SlurmMeta(
    Plugin plugin,
    Client client,
    List<String> command,
    Slurm slurm
) {}

record Plugin(
    @JsonProperty("type")
    String type,

    @JsonProperty("name")
    String name,

    @JsonProperty("data_parser")
    String dataParser,

    @JsonProperty("accounting_storage")
    String accountingStorage
){}

record Client(
    String source,
    String user,
    String group
){}

record Slurm(
    Version version,
    String release,

    @JsonProperty("cluster")
    String clusterName
){}

record Version(
    String major,
    String micro,
    String minor
){}