package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.config.SftpFileManager;
import com.ambrosia.cluster_controller.exception.api.BindedClusterDoesntExistException;
import com.ambrosia.cluster_controller.exception.api.CantCancelTaskException;
import com.ambrosia.cluster_controller.exception.api.CantCreateTaskException;
import com.ambrosia.cluster_controller.exception.api.CantPollTaskException;
import com.ambrosia.cluster_controller.exception.api.ClusterUnavailableException;
import com.ambrosia.cluster_controller.exception.api.TaskOwnerNotFoundException;
import com.ambrosia.cluster_controller.model.DTO.JobSubmitResponse;
import com.ambrosia.cluster_controller.model.DTO.admin.response.StatisticsResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.model.entity.ClusterProfile;
import com.ambrosia.cluster_controller.taskSchedulers.SchedulerHandler;
import com.ambrosia.cluster_controller.taskSchedulers.policy.AbstractPolicy;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmBindedClusterManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.mappers.SlurmJobSubmitMapper;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request.SlurmJobSubmitRequest;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmDBDJobResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmJobInfoResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmJobSubmitResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmKillTaskResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmStatisticsResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmTaskStatisticsWrapper;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmTresResponse;
import com.ambrosia.cluster_controller.util.SlurmRestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RequiredArgsConstructor 
@Service 
public class SlurmSchedulerHandlerImpl implements SchedulerHandler{
    private final SftpFileManager sftpFileManager;

    private final RestClient restClient;

    private final SlurmTokenManager slurmTokenManager;

    private final SlurmBindedClusterManager bindedClusterManager;

    private final SlurmJobSubmitMapper slurmJobSubmitMapper;

    // slurm doesn't supports chuncked transfer-encoding, need to explicitly set body
    private final ObjectMapper objectMapper;

    private final AppConfigurationProperties appConfigurationProperties;

    //  X-SLURM-USER-NAME - имя пользователя
    //  X-SLURM-USER-TOKEN - токен, получаемый через scontrol token

    @Override
    public Resource downloadProfiles(List<ClusterProfile> clusterProfiles) {
        var cluster = clusterProfiles.getFirst().getId().getCluster();
        var bindedCluster = bindedClusterManager.getBinded(cluster.getId(), clusterProfiles.getFirst().getId().getClusterName());
        try {
            var file = sftpFileManager.download(
                bindedCluster.controller().host(),
                cluster.getUsername(),
                cluster.getPassword(),
                cluster.getSshPort(),
                cluster.getDisplayedName()+"_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-MM"))+".zip",
                clusterProfiles.stream().map(profile -> 
                    (
                        appConfigurationProperties.getHomeDirectoryPath()+
                        "/"+
                        profile.getId().getUser().getUsername().toLowerCase()
                    )
                ).toArray(String[]::new)
            );
            return new UrlResource(file.toURI().toURL());
        } catch (Exception e) {
            log.error("Can't download profiles! {}", e);
            throw new ClusterUnavailableException();
        }
    }


    @Override
    public List<SlurmJob> getTasks(Cluster cluster, TaskFilter taskFilter, AbstractPolicy abstractPolicy) {
        var token = abstractPolicy.getToken(slurmTokenManager);
        var uriBuilder = SlurmRestUtil.buildSlurmDaemonRequest();

        applyFilters(uriBuilder, taskFilter, abstractPolicy);

        var uri = uriBuilder.buildAndExpand(
            cluster.getSchema(),
            cluster.getHost(),
            cluster.getDaemonPort(),
            cluster.getScheduler().getVersion(),
            "jobs/"
        ).toUriString();
        var tasks = restClient.get()
            .uri(uri)
            .header("X-SLURM-USER-NAME", abstractPolicy.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (req, resp) -> {
                log.error("Can't get tasks on {}:{}!", cluster.getHost(), cluster.getUsername());
                throw new ClusterUnavailableException();
            })
            .body(SlurmDBDJobResponse.class);

        
        if(tasks != null && tasks.jobs() != null)
            return tasks.jobs();
        else
            return List.of();
    }

    @Override
    public List<SlurmTres> getTres(Cluster cluster) {
        var token = getToken(cluster);
        var uri = SlurmRestUtil.buildDbDaemonRequest()
            .buildAndExpand(
                cluster.getSchema(),
                cluster.getHost(),
                cluster.getDaemonPort(),
                cluster.getScheduler().getVersion(),
                "tres/"
            )
            .toUriString();
        var resp = restClient.get()
            .uri(uri)
            .header("X-SLURM-USER-NAME", cluster.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                log.error("Can't get tres on {}:{}!", cluster.getHost(), cluster.getUsername());
                throw new ClusterUnavailableException();
            })
            .body(SlurmTresResponse.class);
        if(resp == null || !resp.errors().isEmpty()){
            throw new ClusterUnavailableException();
        }
        return resp.tres();
    }

    @Override
    public List<SlurmJobInfo> pollTasks(Cluster cluster, String clusterName, AbstractPolicy abstractPolicy, TaskPollFilter taskPollFilter) {
        var token = abstractPolicy.getToken(slurmTokenManager);
        var bindedCluster = bindedClusterManager.getBinded(cluster.getId(), clusterName);
        if(bindedCluster == null)
            throw new BindedClusterDoesntExistException();
        var uri = SlurmRestUtil.buildSlurmDaemonRequest();
        if(taskPollFilter.updateTime() != null)
            uri.queryParam("update_time", taskPollFilter.updateTime());
        var uriString = uri.buildAndExpand(
            cluster.getSchema().getName(),
                bindedCluster.controller().host(),
                cluster.getDaemonPort(),
                cluster.getScheduler().getVersion(),
                "jobs/"
        ).toUriString();
        var resp = restClient.get()
            .uri(uriString)
            .header("X-SLURM-USER-NAME", abstractPolicy.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(SlurmJobInfoResponse.class);
        if (resp != null && !resp.slurmErrors().isEmpty()) {
            throw new CantPollTaskException(
                Map.of("errors", resp.slurmErrors())
            );
        }else{
            return resp.jobs();
        }
    }

    @Override
    public void cancelTask(Cluster cluster, long taskId, String clusterName, AbstractPolicy abstractPolicy) {
        var token = abstractPolicy.getToken(slurmTokenManager);
        var bindedCluster = bindedClusterManager.getBinded(cluster.getId(), clusterName);
        if(bindedCluster == null)
            throw new BindedClusterDoesntExistException();
        var uri = SlurmRestUtil.buildSlurmDaemonRequest()
            .buildAndExpand(
                cluster.getSchema().getName(),
                bindedCluster.controller().host(),
                cluster.getDaemonPort(),
                cluster.getScheduler().getVersion(),
                "job/"+taskId
            )
            .toUriString();
        var resp = restClient.delete()
            .uri(uri)
            .header("X-SLURM-USER-NAME", abstractPolicy.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(SlurmKillTaskResponse.class);
        if(resp != null && !resp.errors().isEmpty()){
            log.error(
                "Error while cancelling task username={} taskId={}! {}", 
                abstractPolicy.getUsername(),
                taskId,
                resp.errors().toString()
            );
            throw new CantCancelTaskException(Map.of("errors", resp.errors()));
        }
    }

    @Override
    public String getTaskOwner(Cluster cluster, String clusterName, long taskId) {
        var token = slurmTokenManager.getToken(cluster);
        if (token == null) {
            throw new ClusterUnavailableException();
        }
        var task = restClient.get()
                .uri(SlurmRestUtil.CLUSTER_URL+"/job/{taskId}",
                        cluster.getSchema().getName(),
                        cluster.getHost(),
                        cluster.getDaemonPort(),
                        cluster.getScheduler().getVersion(),
                        taskId
                )
                .header("X-SLURM-USER-NAME", cluster.getUsername())
                .header("X-SLURM-USER-TOKEN", token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(SlurmDBDJobResponse.class);
        if(task == null)
            throw new TaskOwnerNotFoundException();
        return task.jobs().getFirst().user();
    }

    @Override
    public JobSubmitResponse createTask(Cluster cluster, String clusterName, AbstractPolicy abstractPolicy,
            TaskRequest taskRequest) {
        var token = abstractPolicy.getToken(slurmTokenManager);
        var bound = bindedClusterManager.getBinded(cluster.getId(), clusterName);
        if(bound == null)
            throw new BindedClusterDoesntExistException();
        String json = buildSlurmJobRequsts(taskRequest, abstractPolicy);
        var uriBuilder = SlurmRestUtil.buildSlurmDaemonRequest();
        var uri = uriBuilder.buildAndExpand(
            cluster.getSchema(),
            bound.controller().host(),
            cluster.getDaemonPort(),
            cluster.getScheduler().getVersion(),
            "job/submit"
        ).toUriString();
        var resp = restClient.post()
                .uri(uri)
                .header("X-SLURM-USER-NAME", abstractPolicy.getUsername())
                .header("X-SLURM-USER-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    var body = response.getBody().readAllBytes();
                    log.error(
                        "Can't access cluster {}:{}! {}", 
                        new String(body),
                        json
                    );
                    throw new CantCreateTaskException(
                        "Can't create task!",
                        Map.of("errors", body)
                    );
                })
                .body(SlurmJobSubmitResponse.class);
        if (resp != null && !resp.slurmErrors().isEmpty()) {
            log.error(
                "User {} can't create task! {}", 
                abstractPolicy.getUsername(), 
                resp.slurmErrors().toString()
            );
            throw new CantCreateTaskException(
                "Can't create task!",
                Map.of("errors", resp.slurmErrors())
            );
        }else{
            return slurmJobSubmitMapper.toResponse(resp);
        }
    }
    
    @Override
    public StatisticsResponse getStatistics(Cluster cluster, String bindedName) {
        var token = getToken(cluster);
        var bindedCluster = bindedClusterManager.getBinded(cluster.getId(), bindedName);
        var nodeUri = SlurmRestUtil.buildSlurmDaemonRequest()
            .buildAndExpand(
                cluster.getSchema(),
                cluster.getHost(),
                cluster.getDaemonPort(),
                cluster.getScheduler().getVersion(),
                "nodes/"
            )
            .toUriString();
        var clusterUri = SlurmRestUtil.buildSlurmDaemonRequest()
            .buildAndExpand(
                cluster.getSchema(),
                bindedCluster.controller().host(),
                cluster.getDaemonPort(),
                cluster.getScheduler().getVersion(),
                "diag/"
            )
            .toUriString();
        try {
            var nodeResp = restClient.get()
                .uri(nodeUri)
                .header("X-SLURM-USER-NAME", cluster.getUsername())
                .header("X-SLURM-USER-TOKEN", token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(SlurmStatisticsResponse.class);
            var clusterResp = restClient.get()
                .uri(clusterUri)
                .headers(consumer -> {
                    consumer.add("X-SLURM-USER-NAME", cluster.getUsername());
                    consumer.add("X-SLURM-USER-TOKEN", token);
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(SlurmTaskStatisticsWrapper.class);
            var statistics = new StatisticsResponse(
                clusterResp.statistics().jobsSubmitted(), 
                clusterResp.statistics().jobsStarted(), 
                clusterResp.statistics().jobsCompleted(), 
                clusterResp.statistics().jobsCancelled(), 
                clusterResp.statistics().jobsFailed(),
                clusterResp.statistics().jobsRunning(),
                LocalDateTime.now(), 
                nodeResp.nodes());
            return statistics;
        } catch (RestClientResponseException e) { // is it needs?
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<SlurmClusterRec> getBindedClusters(Cluster cluster) {
        return bindedClusterManager.getBindedClusters(cluster.getId());
    }

    private String buildSlurmJobRequsts(TaskRequest taskRequest, AbstractPolicy abstractPolicy){
        try {
            return objectMapper.writeValueAsString(
                    SlurmJobSubmitRequest.builder()
                        .script(taskRequest.script() != null? 
                            taskRequest.script(): 
                            null)
                        .job(taskRequest.job() != null? 
                            slurmJobSubmitMapper.toJobSubmit(
                                abstractPolicy.getUsername(), 
                                taskRequest.job()
                            ):
                            null
                        )
                        .jobs(taskRequest.jobs() != null?
                            taskRequest.jobs().stream()
                                .map(t -> slurmJobSubmitMapper
                                    .toJobSubmit(abstractPolicy.getUsername(), t))
                                .toList():
                            null
                        )
                        .build()
            );
        } catch (Exception e) {
            throw new CantCreateTaskException(
                "Can't create task!",
                Map.of("errors", "Logic error!")
            );
        }
    }

    private void applyFilters(UriComponentsBuilder uriBuilder, TaskFilter taskFilter, AbstractPolicy abstractPolicy){
        if (taskFilter.getTaskStatus() != null && !taskFilter.getTaskStatus().isEmpty()) {
            uriBuilder.queryParam(
                "state", 
                taskFilter.getTaskStatus().stream()
                    .map(val -> val.name()).collect(Collectors.joining(","))
            );
        }
        if (taskFilter.getStartTime() != null) {
            uriBuilder.queryParam("start_time", taskFilter.getStartTime());
        }
        if (taskFilter.getEndTime() != null) {
            uriBuilder.queryParam("end_time", taskFilter.getEndTime());
        }
        if (taskFilter.getClusterNames() != null && !taskFilter.getClusterNames().isEmpty()){
            uriBuilder.queryParam("cluster", taskFilter.getClusterNames().stream().collect(Collectors.joining(",")));
        }

        if(abstractPolicy.isGlobalViewAllowed()){
            if(taskFilter.getUsernames() != null && !taskFilter.getUsernames().isEmpty()){
                taskFilter.getUsernames().forEach(username -> uriBuilder.queryParam("users", username));
            }
        }else{
            uriBuilder.queryParam("users", abstractPolicy.getUsername());
        }
    }

    private String getToken(Cluster cluster){
        var token = slurmTokenManager.getToken(cluster);
        if(token == null){
            throw new ClusterUnavailableException();
        }
        return token;
    }
}
