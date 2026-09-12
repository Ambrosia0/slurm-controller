package com.ambrosia.cluster_controller.taskSchedulers.slurm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import com.ambrosia.cluster_controller.config.AppConfigurationProperties;
import com.ambrosia.cluster_controller.exception.api.CantCreateProfileException;
import com.ambrosia.cluster_controller.exception.api.CantDeleteProfileException;
import com.ambrosia.cluster_controller.exception.api.ClusterUnavailableException;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Association;
import com.ambrosia.cluster_controller.model.DTO.scheduler.Profile;
import com.ambrosia.cluster_controller.model.entity.Cluster;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmAccessManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmProfileChecker;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SlurmTokenManager;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmUint32NoVal;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssocMax;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssociation;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssocMax.MaxJobs;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssocMax.MaxPer;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssocMax.MaxTres;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssocMax.MaxJobs.MaxJobsPer;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.assoc.SlurmAssocMax.MaxPer.MaxPerAccount;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user.SlurmUser;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.user.SlurmUserDefault;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request.SlurmAssociationsRequest;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.request.SlurmUserPost;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmOpenApiResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmRemoveAssocResponse;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.response.SlurmUsersResponse;
import com.ambrosia.cluster_controller.util.SlurmRestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor 
@Component
public class SlurmAccessManagerImpl implements SlurmAccessManager, SlurmProfileChecker{

    private final RestClient restClient;

    private final SlurmTokenManager slurmTokenManager;

    private final AppConfigurationProperties appConfigurationProperties;

    // slurm doesn't supports chuncked transfer-encoding, need to explicitly set body
    private final ObjectMapper objectMapper;

    @SneakyThrows 
    @Override
    public void createProfile(Cluster cluster, List<Profile> toCreate) {
        var token = getToken(cluster);
        var uriBuilder = SlurmRestUtil.buildDbDaemonRequest();
        var uri = uriBuilder.buildAndExpand(
            cluster.getSchema(),
            cluster.getHost(),
            cluster.getDaemonPort(),
            cluster.getScheduler().getVersion(),
            "users"
        ).toUriString();

        var req = objectMapper.writeValueAsBytes(SlurmUserPost.builder()
            .users(createSlurmUsers(toCreate))
            .build()
        );

        var resp = restClient.post()
            .uri(uri)
            .header("X-SLURM-USER-NAME", cluster.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                var errors = response.getBody().readAllBytes();
                log.error(
                    "Can't create associations on slurmdbd node! {}", 
                    new String(errors)
                );
                throw new CantCreateProfileException(Map.of("errors", errors));
            })
            .toEntity(SlurmOpenApiResponse.class);

        if(resp.hasBody())
            logError(resp.getBody());
    }
    
    @SneakyThrows
    public void provideAccessToBindedCluster(Cluster cluster, String clusterName, List<Association> profile){
        var token = getToken(cluster);
        var uriBuilder = SlurmRestUtil.buildDbDaemonRequest();
        var uri = uriBuilder.buildAndExpand(
            cluster.getSchema(),
            cluster.getHost(),
            cluster.getDaemonPort(),
            cluster.getScheduler().getVersion(),
            "associations/"
        ).toUriString();

        var req = objectMapper.writeValueAsBytes(SlurmAssociationsRequest.builder()
            .associations(buildAssoc(clusterName, profile))
            .build());

        var resp = restClient.post()
            .uri(uri)
            .header("X-SLURM-USER-NAME", cluster.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(req)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                var errors = response.getBody().readAllBytes();
                log.error(
                    "Can't create associations on slurmdbd node! {}", 
                    new String(errors)
                );
                throw new CantCreateProfileException(Map.of("errors", errors));
            })
            .toEntity(SlurmOpenApiResponse.class);
        if(resp.hasBody()){
            logError(resp.getBody());
        }
    }

    public void revokeAccessToBindedCluster(Cluster cluster, String clusterName, List<String> usernames){
        var token = getToken(cluster);

        var uriBuilder = SlurmRestUtil.buildDbDaemonRequest();
        uriBuilder.queryParam("cluster", clusterName);
        usernames.forEach(val ->{
            uriBuilder.queryParam("user", val.toLowerCase());
        });
        var uri = uriBuilder.buildAndExpand(
            cluster.getSchema(),
            cluster.getHost(), 
            cluster.getDaemonPort(),
            cluster.getScheduler().getVersion(),
            "associations/"
        ).toUri();
        restClient.delete()
            .uri(uri)
            .header("X-SLURM-USER-NAME", cluster.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                log.error(
                    "Can't delete profile on cluster database! {}",
                    new String(response.getBody().readAllBytes())
                );
                throw new ClusterUnavailableException();
            })
            .toEntity(SlurmRemoveAssocResponse.class);
    }

    public void setProfileLimits(Cluster cluster, String clusterName, List<Association> profileCreate) {
        provideAccessToBindedCluster(cluster, clusterName, profileCreate);
    }

    /**
     * @see https://slurm.schedmd.com/rest_api.html#slurmdbV0045DeleteUser
     */
    public void deleteProfile(Cluster cluster, List<String> toDelete) {
        Assert.notNull(cluster, "Cluster must be not null!");
        Assert.notNull(toDelete, "toDelete must be not null!");
        if(toDelete.isEmpty())
            return;
        
        var token = getToken(cluster);
        toDelete.forEach(username -> {
            var uriBuilder = SlurmRestUtil.buildDbDaemonRequest();
            var uri = uriBuilder.buildAndExpand(
                cluster.getSchema(),
                cluster.getHost(),
                cluster.getDaemonPort(),
                cluster.getScheduler().getVersion(),
                "user/"+username
            );
            restClient.delete()
                .uri(uri.toUriString())
                .header("X-SLURM-USER-NAME", cluster.getUsername())
                .header("X-SLURM-USER-TOKEN", token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    var resp = response.getBody().readAllBytes();
                    log.error("Can't create users through post request on slurmdbd node {}! message={}", 
                        cluster.getHost(),
                        new String(resp)
                    );
                    throw new CantDeleteProfileException(Map.of("errors", resp));
                })
                .toBodilessEntity();
        });
    }


    /**
     * @see https://slurm.schedmd.com/rest_api.html#slurmdbV0045GetUser
     */
    @Override
    public boolean isProfileExist(Cluster cluster, String boundCluster, String username) {
        var token = getToken(cluster);
        var uriBuilder = SlurmRestUtil.buildDbDaemonRequest();
        uriBuilder.queryParam("with_assocs", true);
        var uri = uriBuilder.buildAndExpand(
            cluster.getSchema(),
            cluster.getHost(),
            cluster.getDaemonPort(),
            cluster.getScheduler().getVersion(),
            "user/"+username
        ).toUri();
        
        var resp = restClient
            .get()
            .uri(uri)
            .header("X-SLURM-USER-NAME", cluster.getUsername())
            .header("X-SLURM-USER-TOKEN", token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                log.error(
                    "Can't check user existance on slurmdbd node {}! message={}", 
                    cluster.getHost(),
                    new String(response.getBody().readAllBytes())
                );
                throw new ClusterUnavailableException();
            })
            .toEntity(SlurmUsersResponse.class)
            .getBody();
        // user doesnt exist
        if(resp.users() == null || resp.users().isEmpty())
            return false;

        // association doesn't exist
        if(resp.users().getFirst().associations().isEmpty())
            return false;

        var assocs = resp.users().getFirst().associations();
        return assocs.stream().anyMatch((assoc) -> assoc.cluster().equals(boundCluster));
    }

    private void logError(SlurmOpenApiResponse slurmOpenApiResponse){
        if(slurmOpenApiResponse.errors() != null && !slurmOpenApiResponse.errors().isEmpty()){
            log.error("Error on response! {}", slurmOpenApiResponse.errors());
            throw new CantCreateProfileException(Map.of("errors", slurmOpenApiResponse.errors()));
        }
    }
    private List<SlurmAssociation> buildAssoc(String clusterName, List<Association> profile){
        return profile.stream()
            .map(t -> SlurmAssociation.builder()
                .account(appConfigurationProperties.getAccount())
                .user(t.username().toLowerCase())
                .cluster(clusterName)
                .max(SlurmAssocMax.builder()
                    .tres(createMaxTres(t))
                    .jobs(createMaxJobs(t))
                    .per(createMaxWallclock(t))
                    .build()
                )
                .build()
            )
            .toList();
    }

    private MaxTres createMaxTres(Association profileCreate){
        return MaxTres.builder()
            .total(profileCreate.maxTres()
                .stream()
                .map(t -> {
                    var tresArr = t.split("=", 2);

                    if(tresArr.length != 2 || tresArr[0].isBlank())
                        throw new CantCreateProfileException(Map.of("errors", "Invalid TRES format: "+t));

                    try {
                        return SlurmTres.builder()
                            .name(tresArr[0])
                            .count(Long.parseLong(tresArr[1]))
                            .build();
                    } catch (NumberFormatException e) {
                        throw new CantCreateProfileException(Map.of("errors", "Invalid TRES number: " + t));
                    }
                })
                .toList()
            )
            .build();
    }

    private MaxJobs createMaxJobs(Association profileCreate){
        return MaxJobs.builder()
            .active(SlurmUint32NoVal.builder()
                .set(true)
                .number(profileCreate.maxTasks())
                .build()
            )
            .per(MaxJobsPer.builder()
                .submitted(SlurmUint32NoVal.builder()
                    .set(true)
                    .number(profileCreate.maxSubmit())
                    .build()
                )
                .build()
            )
            .build();
    }

    private MaxPer createMaxWallclock(Association profileCreate){
        return MaxPer.builder()
            .account(MaxPerAccount.builder()
                .wallclock(SlurmUint32NoVal.builder().number(profileCreate.maxTaskTtl()).build())
                .build()
            )
            .build();
    }

    private List<SlurmUser> createSlurmUsers(List<Profile> profiles){
        return profiles.stream().map(pf -> SlurmUser.builder()
            .name(pf.username())
            .userDefault(SlurmUserDefault.builder()
                .account(appConfigurationProperties.getAccount())
                .build()
            )
            .build()
        ).toList();
    }

    private String getToken(Cluster cluster){
        var token = slurmTokenManager.getToken(cluster);
        if (token == null) {
            throw new ClusterUnavailableException();
        }
        return token;
    }
}
