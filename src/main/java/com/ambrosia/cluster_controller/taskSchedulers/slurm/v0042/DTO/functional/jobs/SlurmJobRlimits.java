package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint64NoVal;

import lombok.Builder;

@Builder 
public record SlurmJobRlimits(
    SlurmUint64NoVal cpu,
    SlurmUint64NoVal fsize,
    SlurmUint64NoVal data,
    SlurmUint64NoVal stack,
    SlurmUint64NoVal core,
    SlurmUint64NoVal rss,
    SlurmUint64NoVal nproc,
    SlurmUint64NoVal nofile,
    SlurmUint64NoVal memlock,
    SlurmUint64NoVal as
) {}
