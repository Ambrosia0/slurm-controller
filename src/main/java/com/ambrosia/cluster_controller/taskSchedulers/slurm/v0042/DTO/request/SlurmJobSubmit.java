package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint64NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobRlimits;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_job_desc_msg
 * SlurmJobSubmit
 * @param account
 * @param userName
 * @param argv
 * @param environment
 * @param script
 * @param comment
 * @param tresPerJob
 * @param maxNodes
 * @param timeLimit
 * @param jobName
 * @param numberOfTasks
 * @param tresPerTask
 * @param workingDirectory
 */
@Builder 
public record SlurmJobSubmit(
    @JsonProperty("account")
    @JsonInclude(value = Include.NON_NULL)
    String account,

    @JsonProperty("user_name")
    @JsonInclude(value = Include.NON_NULL)
    String userName,

    @JsonProperty("argv")
    @JsonInclude(value = Include.NON_NULL)
    List<String> argv,

    @JsonProperty("begin_time")
    @JsonInclude(value = Include.NON_NULL)
    SlurmUint64NoVal beginTime,

    @JsonInclude(value = Include.NON_NULL)
    Long deadline,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("end_time")
    Long endTime,

    @JsonInclude(value = Include.NON_NULL)
    List<String> flags,

    @JsonProperty("batch_features")
    @JsonInclude(value = Include.NON_NULL)
    String batchFeatures,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("environment")
    List<String> environment,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("rlimits")
    SlurmJobRlimits rLimits,

    @JsonInclude(value = Include.NON_NULL)
    String qos,

    // String Job batch script contents; only the first component in a HetJob is populated or honored
    @JsonInclude(value = Include.NON_NULL)
    // @JsonSerialize(using = StringSerializer.class)
    @JsonProperty("script")
    String script,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty
    String comment,

    // @JsonProperty("cpus_per_tres")
    // String cpuPerTres,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("tres_per_job")
    String tresPerJob,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("tres_per_node")
    String tresPerNode,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("tres_per_socket")
    String tresPerSocket,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("cpus_per_task")
    Integer cpusPerTask,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("minimum_cpus")
    Integer minimumCpus,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maximum_cpus")
    Integer maximumCpus,

    @JsonInclude(value = Include.NON_NULL)
    String nodes,

    // Boolean If true, wait to start until after all nodes have booted
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("wait_all_nodes")
    Boolean waitAllNodes,

    /**
     * cpus_per_tres (optional)
     * String Semicolon delimited list of TRES=# values values indicating
     * how many CPUs should be allocated for each specified TRES (currently only used for gres/gpu)
     */
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("cpus_per_tres")
    String cpusPerTres,

    /**
     * String Semicolon delimited list of TRES=# values indicating how much memory 
     * in megabytes should be allocated for each specified TRES (currently only used for gres/gpu)
     */
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("memory_per_tres")
    String memoryPerTres,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("memory_per_cpu")
    SlurmUint64NoVal memoryPerCpu,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("memory_per_node")
    SlurmUint64NoVal memoryPerNode,
    // @JsonProperty("maximum_cpus")
    // int maxCpus,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maximum_nodes")
    Integer maxNodes,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("minimum_nodes")
    Integer minNodes,
    //@JsonProperty("job_id")
    //int jobId,

    // Semicolon delimited list of TRES=# values indicating how much memory in megabytes 
    // should be allocated for each specified TRES (currently only used for gres/gpu)
    
    // @JsonProperty("memory_per_tres")
    // String gpuMemPerTres,

    // @JsonProperty("memory_per_node")
    // SlurmUint64NoVal memoryPerNode,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("time_limit")
    SlurmUint32NoVal timeLimit,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("time_minimum")
    SlurmUint32NoVal timeMinimum,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("name")
    String jobName,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("tasks")
    Integer numberOfTasks,
    
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("tres_per_task")
    String tresPerTask,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("current_working_directory")
    String workingDirectory,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("standard_error")
    String standardError,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("standard_input")
    String standardInput,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("standard_output")
    String standardOutput
) {}
