package com.ambrosia.cluster_controller.taskSchedulers;

import java.util.List;

import org.springframework.core.io.Resource;

import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.admin.response.StatisticsResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.taskSchedulers.policy.AbstractPolicy;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

/**
 * Interracts with slurm clusters to perform operations over tasks and statistics
 * SchedulerHandler
 */
public interface SchedulerHandler {
    /**
     * Downloads profiles through sftp
     * @param clusterProfiles
     * @return
     */
    Resource downloadProfiles(List<ClusterProfile> clusterProfiles);

    /**
     * Creates task
     * @param cluster cluster
     * @param clusterName cluster name
     * @param abstractPolicy task policy
     * @param taskRequest task dto
     * @return
     */
    JobSubmitResponse createTask(
        Cluster cluster, 
        String clusterName, 
        AbstractPolicy abstractPolicy, 
        TaskRequest taskRequest
    );

    /**
     * Gets tasks from slurmdbd database
     * @param cluster cluster
     * @param taskFilter task filter
     * @param abstractPolicy task policy
     * @return slurm jobs
     */
    List<SlurmJob> getTasks(
        Cluster cluster,
        TaskFilter taskFilter,
        AbstractPolicy abstractPolicy
    );

    /**
     * Gets tres from slurmdbd database
     * @param cluster cluster
     * @return tres from slumdbd database
     */
    List<SlurmTres> getTres(
        Cluster cluster
    );

    /**
     * Polls tasks from slurmctld
     * @param cluster cluster
     * @param clusterName name of the cluster in slurmdbd database
     * @param abstractPolicy policy
     * @param taskPollFilter filter
     * @return polled tasks from slurmctld
     */
    List<SlurmJobInfo> pollTasks(
        Cluster cluster,
        String clusterName,
        AbstractPolicy abstractPolicy,
        TaskPollFilter taskPollFilter
    );

    /**
     * Cancels task in slurmctld
     * @param cluster cluster
     * @param taskId jobId
     * @param clusterName name of the cluster in slurmdbd database
     * @param abstractPolicy policy
     */
    void cancelTask(
        Cluster cluster,
        long taskId, 
        String clusterName,
        AbstractPolicy abstractPolicy
    );

    /**
     * Returns owner of the task
     * @param cluster cluster 
     * @param clusterName name of the cluster in slurmdbd database
     * @param taskId jobId
     * @return username of owner
     */
    String getTaskOwner(
        Cluster cluster,
        String clusterName,
        long taskId
    );

    /**
     * Polls statistics from slurmctld and slurmdbd node
     * @param cluster cluster
     * @param bindedName name of the cluster in slurmdbd database
     * @return statistics
     */
    StatisticsResponse getStatistics(Cluster cluster, String bindedName);
    
    /**
     * Requests information about bound to slurmdbd clusters
     * @param cluster cluster
     * @return information about bound clusters
     */
    List<SlurmClusterRec> getBindedClusters(Cluster cluster);
}
