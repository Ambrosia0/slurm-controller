package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint64NoVal;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record SlurmJobInfo(
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("job_id")
    Integer jobId,

    @JsonAlias("job_state")
    @JsonInclude(value = Include.NON_NULL)
    List<String> jobState,

    @JsonInclude(value = Include.NON_NULL)
    String account,

    @JsonInclude(value = Include.NON_NULL)
    String cluster,

    @JsonInclude(value = Include.NON_NULL)
    String comment,
    
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("failed_node")
    String failedNode,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("gres_detail")
    List<String> gresDetail,

    @JsonInclude(value = Include.NON_NULL)
    SlurmUint32NoVal cpus,

    @JsonInclude(value = Include.NON_NULL)
    SlurmUint32NoVal tasks,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("node_count")
    SlurmUint32NoVal nodeCount,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("user_name")
    String username,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("current_working_directory")
    String currentWorkingDirectory,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("end_time")
    SlurmUint64NoVal endTime,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("eligible_time")
    SlurmUint64NoVal eligibleTime,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("submit_time")
    SlurmUint64NoVal submitTime,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("suspend_time")
    SlurmUint64NoVal suspendTime,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("time_limit")
    SlurmUint32NoVal timeLimit,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("tres_per_job")
    String tresPerJob,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("tres_per_node")
    String tresPerNode,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("tres_per_task")
    String tresPerTask,

    // String TRES requested by the job
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("tres_req_str")
    String tresReqStr,

    // String TRES used by the job
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("tres_alloc_str")
    String tresAllocStr,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("state_reason")
    String stateReason
) {}
