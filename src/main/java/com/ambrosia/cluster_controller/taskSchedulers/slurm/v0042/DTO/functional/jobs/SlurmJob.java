package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmAssocShort;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
public record SlurmJob(
    @JsonAlias("job_id")
    int id,

    @JsonAlias("name")
    String name,

    @JsonAlias("cluster")
    String cluster,

    @JsonAlias("time")
    SlurmJobTime time,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("required")
    SlurmJobReq required,

    @JsonAlias("state")
    SlurmJobState jobState,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("exit_code")
    SlurmProcessExitCodeVerbose exitCode,

    @JsonAlias("failed_node")
    @JsonInclude(value = Include.NON_NULL)
    String failedNode,

    @JsonAlias("user")
    String user,

    @JsonAlias("user_id")
    int userId,

    @JsonInclude(value = Include.NON_NULL)
    String nodes,

    @JsonInclude(value = Include.NON_NULL)
    SlurmAssocShort association,

    @JsonInclude(value = Include.NON_NULL)
    SlurmJobTres tres,

    @JsonAlias("used_gres")
    @JsonInclude(value = Include.NON_NULL)
    String usedGres,

    @JsonAlias("working_directory")
    String workingDirectory
) {}