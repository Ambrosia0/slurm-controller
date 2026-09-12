package com.ambrosia.cluster_controller.model.DTO.user.response;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;

public record BindedClusterResponse(
    String name,
    List<SlurmTres> tres,
    String nodes
) {
}
