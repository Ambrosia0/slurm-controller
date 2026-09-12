package com.ambrosia.cluster_controller.model.DTO.generic;

import java.util.List;

import com.ambrosia.cluster_controller.util.TresUtils;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder 
public record JobRequest(
    List<String> args,

    @Pattern (regexp = "^#!/bin/bash\\\\n.+", message = "Script must start with #!/bin/bash")
    String script,

    @NotNull 
    @Pattern(regexp = "^\\/([a-zA-Z0-9._-]+\\/)*[a-zA-Z0-9._-]+\\/?$", message = "Path must match with unix-system paths")
    String directory,

    String jobName,

    String batchFeatures,

    List<String> flags,

    @Positive 
    Long deadLine,

    @Positive 
    Long beginTime,

    @Positive 
    Long endTime,

    /**
     * String Semicolon delimited list of TRES=# values 
     * indicating how many CPUs should be allocated for each specified 
     * TRES (currently only used for gres/gpu)
     */
    @Pattern(regexp = TresUtils.GRES_CSV_PATTERN)
    String cpusPerTres,

    /**
     * String Semicolon delimited list of TRES=# values indicating how much memory in 
     * megabytes should be allocated for each specified TRES (currently only used for gres/gpu)
     */
    @Pattern(regexp = TresUtils.GRES_CSV_PATTERN)
    String memPerTres,

    // String Comma-separated list of TRES=# values to be allocated for every job
    @Pattern(regexp = TresUtils.TRES_CSV_PATTERN)
    String tresPerJob,

    // String Comma-separated list of TRES=# values to be allocated for every task
    @Pattern(regexp = TresUtils.TRES_CSV_PATTERN)
    String tresPerTask,
    
    // String Comma-separated list of TRES=# values to be allocated for every node
    @Pattern(regexp = TresUtils.TRES_CSV_PATTERN)
    String tresPerNode,
    
    // Integer Number of CPUs required by each task format: int32
    @Positive 
    Integer cpusPerTask,

    // Integer Minimum number of CPUs required format: int32
    @Positive 
    Integer minimumCpus,

    // Integer Maximum number of CPUs required format: int32
    @Positive 
    Integer maximumCpus,

    // String Node count range specification (e.g. 1-15:4)
    @Pattern(regexp = "^\\d+(?:-\\d+)?(?::\\d+)?$")
    String nodes, 

    @Positive
    Integer maxNodes,

    @Positive
    Integer minNodes,

    @Positive
    Integer numberOfTasks,

    @Positive
    Integer maxTaskLiveTime,

    // String Path to stderr file
    String standardError,

    // String Path to stdin file
    String standartInput,

    // String Path to stdout file
    String standardOutput
) {}
