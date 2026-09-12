package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

//v0.0.42_node
public record SlurmNode(
    @JsonInclude(value = Include.NON_NULL)
    String name,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("hostname")
    String hostname,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("state")
    List<String> state,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("boot_time")
    SlurmUint64NoVal bootTime,

    // Number of physical processor sockets/chips on the node format
    @JsonInclude(value = Include.NON_NULL)
    Integer sockets,

    // Total CPUs, including cores and threads
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("cpus")
    Integer cpus,

    // Number of cores in a single physical processor socket
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("cores")
    Integer cores,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("threads")
    Integer threads,

    // Total number of CPUs currently allocated for jobs
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("alloc_cpus")
    Integer allocatedCpus,

    // Total number of idle CPUs
    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("alloc_idle_cpus")
    Integer allocatedIdleCpus,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("cpu_load")
    Integer cpuLoad,
    
    // Available memory (not physical)
    @JsonAlias("free_mem")
    SlurmUint64NoVal freeMemory,

    // How many memory is using
    @JsonAlias("alloc_memory")
    long allocatedMemory,

    // Generic resources (GPU, TPU, FPGA)
    String gres,

    // Drained generic resources
    @JsonAlias("gres_drained")
    String gresDrained,

    // Generic resources currently in use
    @JsonAlias("gres_used")
    String gresUsed,

    @JsonInclude(value = Include.NON_NULL)
    @JsonAlias("tres_used")
    String tresUsed,

    @JsonInclude(value = Include.NON_NULL)
    String tres,

    @JsonAlias("last_busy")
    SlurmUint64NoVal lastBusy,

    // Describes why the node is in a "DOWN", "DRAINED", "DRAINING", "FAILING" or "FAIL" stat
    @JsonAlias("reason")
    String reason

) {}