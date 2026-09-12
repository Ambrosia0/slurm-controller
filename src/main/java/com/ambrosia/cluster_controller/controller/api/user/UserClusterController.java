package com.ambrosia.cluster_controller.controller.api.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.ambrosia.cluster_controller.model.DTO.generic.ClusterProfileResponse;
import com.ambrosia.cluster_controller.model.DTO.user.response.BindedClusterResponse;
import com.ambrosia.cluster_controller.model.DTO.user.response.ClusterUserResponse;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterUserService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.SlurmTres;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cluster")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserClusterController {
    private final ClusterUserService clusterUserService;

    private final ClusterProfileManageService clusterProfileService;

    @GetMapping
    public List<ClusterUserResponse> getAvailable(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return clusterUserService.getAvailableClusters(userDetails.getId());
    }

    @GetMapping("/{clusterId}/bound")
    public List<BindedClusterResponse> getAvailableBinded(
            @PathVariable Long clusterId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return clusterUserService.getBindedClusters(clusterId, userDetails.getId());
    }

    @GetMapping("/{clusterId}/bound/{clusterName}/tres")
    public List<SlurmTres> getTres(
            @PathVariable Long clusterId) {
        return clusterUserService.getTres(clusterId);
    }

    @GetMapping("/{clusterId}/bound/{clusterName}/profile")
    public ClusterProfileResponse getProfile(
            @PathVariable Long clusterId,
            @PathVariable @NotBlank String clusterName,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return clusterProfileService.getUserProfile(clusterId, userDetails.getId(), clusterName);
    }   

    @GetMapping("/{clusterId}/bound/{clusterName}/profile/download")
    public ResponseEntity<Resource> downloadProfile(
            @PathVariable Long clusterId,
            @PathVariable String clusterName,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var resource = clusterProfileService.downloadProfiles(clusterId, clusterName, Set.of(userDetails.getId()));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers -> {
                    headers.add(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"");
                })
                .body(resource);
    }
    
}
