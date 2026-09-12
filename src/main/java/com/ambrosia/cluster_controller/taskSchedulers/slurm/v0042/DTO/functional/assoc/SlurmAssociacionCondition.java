package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0.0.45_assoc_rec_set
 * SlurmAssociacionCondition
 */
public record SlurmAssociacionCondition(
    @JsonInclude(value = Include.NON_NULL)
    String comment,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("defaultqos")
    String defaultQos,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grpjobs")
    Integer grpJobs,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grpjobsaccrue")
    Integer grpJobsAccrue,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grpsubmitjobs")
    Integer grpSubmitJobs,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grptres")
    List<SlurmTres> grpTres,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grptresmins")
    List<SlurmTres> grpTresMins,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grptresrunmins")
    List<SlurmTres> grpTresRunMins,
    
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("grpwall")
    Integer grpWall,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxjobs")
    Integer maxJobs,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxjobsaccrue")
    Integer maxJobsAccrue,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxsubmitjobs")
    Integer maxSubmitJobs,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxtresminsperjob")
    List<SlurmTres> maxTresMinsPerjob,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxtresrunmins")
    List<SlurmTres> maxTresRunMins,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxtresperjob")
    List<SlurmTres> maxTresPerJob,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxtrespernode")
    List<SlurmTres> maxTresPerNode,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("maxwalldurationperjob")
    Integer maxWallDurationPerJob,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("minpriothresh")
    Integer minPrioThresh,

    @JsonInclude(value = Include.NON_NULL)
    String parent,

    @JsonInclude(value = Include.NON_NULL)
    Integer priority,

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("qoslevel")
    List<String> qosLevel,

    @JsonInclude(value = Include.NON_NULL)
    Integer fairshare

) {}
