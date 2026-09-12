package com.ambrosia.cluster_controller.service.databaseHandlers;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

/**
 * Service for managing tasks on clusters for users
 */
public interface UserTaskService {
    /**
     * Creates task on cluster
     * @param clusterId Slurmdbd node id
     * @param clusterName Bound cluster name
     * @param userId Requesting user
     * @param request DTO, containing task information 
     */
    JobSubmitResponse createTask(long clusterId, String clusterName, long userId, TaskRequest request);
    
    /**
     * Gets user tasks from slurmdbd
     * @param clusterId Slurmdbd node id
     * @param userId Requesting user
     * @param taskFilter task filter
     * @return Tasks
     */
    List<SlurmJob> getTasks(long clusterId, long userId, TaskFilter taskFilter);
    
    /**
     * Polls current tasks from cluster (slurmctld)
     * @param clusterId Slurmdbd node id
     * @param clusterName Bound cluster name
     * @param userId Requesting user
     * @param taskPollFilter poll task filter
     * @return Current tasks on cluster
     */
    List<SlurmJobInfo> pollTasks(long clusterId, String clusterName, long userId, TaskPollFilter taskPollFilter);
    
    /**
     * Cancels uncompleted tasks
     * @param clusterId Slurmdbd node id
     * @param userId Requesting user
     * @param clusterName Bound cluster name
     * @param taskId Task to cancel
     */
    void cancelTask(long clusterId, long userId, String clusterName, long taskId);
}
