package com.ambrosia.cluster_controller.taskSchedulers.slurm.mappers;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.generic.JobRequest;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint64NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request.SlurmJobSubmit;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmJobSubmitResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class SlurmJobSubmitMapper {
    private final AppConfigurationProperties appConfigurationProperties;

    public SlurmJobSubmit toJobSubmit(String username, JobRequest taskRequest){
        var time = Instant.now();

        return SlurmJobSubmit.builder()
            .account(appConfigurationProperties.getAccount())
            .userName(username)
            .argv(taskRequest.args())
            .environment(List.of("PATH=/bin/:/usr/bin/:/sbin/:/usr/local/bin"))
            .script(taskRequest.script() == null? null: taskRequest.script()) // script().replace("\n", "\\n")
            .beginTime(taskRequest.beginTime() == null? 
                null:
                SlurmUint64NoVal.builder()
                    .set(true)
                    .number(taskRequest.beginTime())
                    .build()
            )
            .deadline(taskRequest.deadLine())
            .endTime(taskRequest.endTime())
            .batchFeatures(taskRequest.batchFeatures())
            .flags(taskRequest.flags())
            .comment("Created with Web-GUI")
            .maxNodes(taskRequest.maxNodes())
            .timeLimit(taskRequest.maxTaskLiveTime() == null?
                null:
                SlurmUint32NoVal.builder()
                    .number(taskRequest.maxTaskLiveTime())
                    .set(true)
                    .build()
            )
            .cpusPerTask(taskRequest.cpusPerTask())
            .minimumCpus(taskRequest.minimumCpus())
            .maximumCpus(taskRequest.maximumCpus())
            .nodes(taskRequest.nodes())
            .maxNodes(taskRequest.maxNodes())
            .minNodes(taskRequest.minNodes())
            .tresPerTask(taskRequest.tresPerTask())
            .tresPerJob(taskRequest.tresPerJob())
            .tresPerNode(taskRequest.tresPerNode())
            .cpusPerTres(taskRequest.cpusPerTres())
            .memoryPerTres(taskRequest.memPerTres())
            .jobName(taskRequest.jobName() == null? username+"_"+time: taskRequest.jobName())
            .numberOfTasks(taskRequest.numberOfTasks())
            .workingDirectory(taskRequest.directory() == null? 
                appConfigurationProperties.getHomeDirectoryPath()+"/"+username:
                taskRequest.directory()
            )
            .standardError(taskRequest.standardError())
            .standardInput(taskRequest.standartInput())
            .standardOutput(taskRequest.standardOutput())
            .build();
    }

    public JobSubmitResponse toResponse(SlurmJobSubmitResponse slurmJobSubmitResponse){
        return JobSubmitResponse.builder()
            .jobId(slurmJobSubmitResponse.jobId())
            .jobSubmitUserMessage(slurmJobSubmitResponse.jobSubmitUserMessage())
            .stepId(slurmJobSubmitResponse.stepId())
            .build();
    }
}
