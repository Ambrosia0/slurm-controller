package com.ambrosia.cluster_controller.service.databaseHandlers.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.exception.api.AccessDoesntProvidedException;
import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.repository.ClusterProfileRepository;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserTaskService;
import com.ambrosia.cluster_controller.service.mappers.ProfileMapper;
import com.ambrosia.cluster_controller.taskSchedulers.SchedulerHandler;
import com.ambrosia.cluster_controller.taskSchedulers.policy.UserActor;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskUserServiceImpl implements UserTaskService{
    private final ClusterProfileRepository clusterProfileRepository;

    private final SchedulerHandler slurmUserSchedulerHandler;

    private final ProfileMapper profileMapper;


    @Override
    public JobSubmitResponse createTask(long clusterId, String bindedCluster, long userId, TaskRequest taskDTO) {
        var userProfile = clusterProfileRepository.findById(clusterId, userId, bindedCluster)
            .orElseThrow(() -> new AccessDoesntProvidedException());
        var cluster = userProfile.getId().getCluster();
        return slurmUserSchedulerHandler.createTask(
            cluster,
            bindedCluster,
            UserActor.create(
                cluster, 
                profileMapper.toTask(userProfile)
            ), 
            taskDTO
        );
    }

    @Override
    public List<SlurmJob> getTasks(long clusterId, long userId, TaskFilter taskFilter) {
        var userProfile = clusterProfileRepository.findFirstByClusterIdAndUserId(clusterId, userId)
            .orElseThrow(() -> new AccessDoesntProvidedException());
        return slurmUserSchedulerHandler.getTasks(
            userProfile.getId().getCluster(),
            taskFilter,
            UserActor.create(
                userProfile.getId().getCluster(), 
                profileMapper.toTask(userProfile)
            )
        );
    }

    @Override
    public List<SlurmJobInfo> pollTasks(long clusterId, String clusterName, long userId,
            TaskPollFilter taskPollFilter) {
        var userProfile = clusterProfileRepository.findFirstByClusterIdAndUserId(clusterId, userId)
            .orElseThrow(() -> new AccessDoesntProvidedException());
        return slurmUserSchedulerHandler.pollTasks(
            userProfile.getId().getCluster(), 
            clusterName, 
            UserActor.create(
                userProfile.getId().getCluster(),
                profileMapper.toTask(userProfile)
            ), 
            taskPollFilter
        );
    }
    
    @Override
    public void cancelTask(long clusterId, long userId, String bindedCluster, long taskId) {
        var userProfile = clusterProfileRepository.findById(clusterId, userId, bindedCluster)
            .orElseThrow(() -> new AccessDoesntProvidedException());
        slurmUserSchedulerHandler.cancelTask(
            userProfile.getId().getCluster(),
            taskId,
            bindedCluster,
            UserActor.create(
                userProfile.getId().getCluster(),
                profileMapper.toTask(userProfile)
            )
        );
    }
}
