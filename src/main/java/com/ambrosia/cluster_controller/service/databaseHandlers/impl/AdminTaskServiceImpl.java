package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.exception.api.ClusterDoesntExistException;
import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.repository.ClusterRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.AdminTaskService;
import com.ambrosia.cluster_controller.taskSchedulers.SchedulerHandler;
import com.ambrosia.cluster_controller.taskSchedulers.policy.AdminActor;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminTaskServiceImpl implements AdminTaskService{
    private final ClusterRepository clusterRepository;

    private final SchedulerHandler slurmAdminSchedulerHandler;

    private final ClusterProfileRepository clusterProfileRepository;

    @Override
    public JobSubmitResponse createTask(long clusterId, String clusterName, TaskRequest taskDTO) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());
        return slurmAdminSchedulerHandler.createTask(
            cluster, 
            clusterName,
            AdminActor.create(cluster),
            taskDTO
        );
    }

    @Override
    public List<SlurmJobInfo> pollTasks(long clusterId, String clusterName, TaskPollFilter taskPollFilter) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());
        return slurmAdminSchedulerHandler.pollTasks(
            cluster, 
            clusterName,
            AdminActor.create(cluster), 
            taskPollFilter
        );
    }

    @Override
    public void cancelTask(long clusterId, String clusterName, long taskId) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());
        slurmAdminSchedulerHandler.cancelTask(
            cluster, 
            taskId, 
            clusterName,
            AdminActor.create(cluster)
        );
    }

    @Override
    public List<SlurmJob> getTasks(long clusterId, TaskFilter taskFilter) {
        var cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new ClusterDoesntExistException());
        if(taskFilter.getGroupId() != null){
            taskFilter.setUsernames(
                clusterProfileRepository.findUsernamesByClusterIdAndGroupId(
                    clusterId, 
                    taskFilter.getGroupId()
                )
            );
        }
        var tasks = slurmAdminSchedulerHandler.getTasks(
            cluster, 
            taskFilter,
            AdminActor.create(cluster)
        );
        return tasks;
    }
}
