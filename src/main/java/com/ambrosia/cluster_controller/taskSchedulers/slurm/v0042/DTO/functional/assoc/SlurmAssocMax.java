package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc;

import java.util.List;

import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * @see https://slurm.schedmd.com/rest_api.html#v0_0_45_assoc_max
 * SlurmAssocMax
 * @param jobs
 * @param tres
 * @param per
 */
@Builder 
public record SlurmAssocMax(
    @JsonInclude(value = Include.NON_NULL)
    MaxJobs jobs,

    @JsonInclude(value = Include.NON_NULL)
    MaxTres tres,

    @JsonInclude(value = Include.NON_NULL)
    MaxPer per
) {

    @Builder
    public record MaxJobs(
        @JsonInclude(value = Include.NON_NULL)
        MaxJobsPer per,

        @JsonInclude(value = Include.NON_NULL)
        SlurmUint32NoVal active,

        @JsonInclude(value = Include.NON_NULL)
        SlurmUint32NoVal accruing,

        @JsonInclude(value = Include.NON_NULL)
        SlurmUint32NoVal total
    ){
        @Builder
        public record MaxJobsPer(
            @JsonInclude(value = Include.NON_NULL)
            SlurmUint32NoVal count,

            @JsonInclude(value = Include.NON_NULL)
            SlurmUint32NoVal accruing,

            @JsonInclude(value = Include.NON_NULL)
            SlurmUint32NoVal submitted,

            @JsonInclude(value = Include.NON_NULL)
            @JsonProperty("wall_clock")
            SlurmUint32NoVal wallClock
            
        ){}
    }

    @Builder
    public record MaxTres(
        @JsonInclude(value = Include.NON_NULL)
        List<SlurmTres> total,

        @JsonInclude(value = Include.NON_NULL)
        MaxTresGroup group,

        @JsonInclude(value = Include.NON_NULL)
        MaxTresMinutes minutes,

        @JsonInclude(value = Include.NON_NULL)
        MaxTresPer per

    ){

        @Builder 
        public record MaxTresGroup(
            @JsonInclude(value = Include.NON_NULL)
            List<SlurmTres> minutes,

            @JsonInclude(value = Include.NON_NULL)
            List<SlurmTres> active
        ){}

        @Builder
        public record MaxTresMinutes(
            @JsonInclude(value = Include.NON_NULL)
            List<SlurmTres> total,

            @JsonInclude(value = Include.NON_NULL)
            MaxTresMinutesPer per

        ){
        
            @Builder
            public record MaxTresMinutesPer(
                @JsonInclude(value = Include.NON_NULL)
                List<SlurmTres> job
            ){}
        }

        @Builder
        public record MaxTresPer(
            @JsonInclude(value = Include.NON_NULL)
            List<SlurmTres> job,

            @JsonInclude(value = Include.NON_NULL)
            List<SlurmTres> node
        ){}
    }

    @Builder
    public record MaxPer(
        @JsonInclude(value = Include.NON_NULL)
        MaxPerAccount account
    ){
        @Builder
        public record MaxPerAccount(
            @JsonInclude(value = Include.NON_NULL)
            SlurmUint32NoVal wallclock
        ){}
    }
}
