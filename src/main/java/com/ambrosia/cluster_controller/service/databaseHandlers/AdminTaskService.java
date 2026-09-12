package com.ambrosia.cluster_controller.service.databaseHandlers;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

/**
 * Service for managing tasks for admin
 * TaskAdminService
 */
public interface AdminTaskService {
    /**
     * Creates task
     * @param clusterId Slurmdbd node id
     * @param bindedClusterName Bound cluster id
     * @param request Task information
     * @return information about created task
     */
    JobSubmitResponse createTask(long clusterId, String bindedClusterName, TaskRequest request);

    /**
     * Cancels any task
     * @param clusterId Slurmdbd node id
     * @param bindedClusterName Bound cluster id
     * @param taskId Task id
     */
    void cancelTask(long clusterId, String bindedClusterName, long taskId);

    /**
     * Polls current tasks from cluster (slurmctld)
     * @param clusterId Slurmdbd node id
     * @param clusterName Bound cluster name
     * @param taskPollFilter poll task filter
     * @return Current tasks on cluster
     */
    List<SlurmJobInfo> pollTasks(long clusterId, String clusterName, TaskPollFilter taskPollFilter);

    /**
     * Get tasks from bound cluster
     * @param clusterId Slurmdbd node id
     * @param bindedCluster Bound cluster id
     * @param taskFilter Task filter
     * @return Tasks
     */
    List<SlurmJob> getTasks(long clusterId, TaskFilter taskFilter);
}
