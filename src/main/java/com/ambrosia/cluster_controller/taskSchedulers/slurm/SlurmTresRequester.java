package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;

/**
 * Service for receiving currently supported tres on slurmdbd node
 * SlurmTresRequester
 */
public interface SlurmTresRequester {
    List<SlurmTres> getSupportedTres(Long clusterId);
}
