package com.ambrosia.cluster_controller.controller.api.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.admin.response.ClusterAdminResponse;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterFilter;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterManageService;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.clusters.SlurmClusterRec;
import com.ambrosia.cluster_controller.util.SupportedTaskSchedulers;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/cluster")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminClusterController {
    private final ClusterManageService clusterService;

    private final ClusterProfileManageService clusterProfileService;

    @GetMapping
    public Page<ClusterAdminResponse> getClusters(
            @ModelAttribute ClusterFilter clusterFilter,
            @PageableDefault(size = 20) Pageable pageable) {
        return clusterService.getClusters(clusterFilter, pageable);
    }
    
    @PostMapping
    public ClusterAdminResponse addCluster(
            @RequestBody ClusterAdminRequest clusterRequest) {
        return clusterService.addCluster(clusterRequest);
    }
    
    @DeleteMapping("/{clusterId}")
    public void deleteCluster(
            @PathVariable Long clusterId){
        clusterService.deleteCluster(clusterId);
    }

    @GetMapping("/versions")
    public List<String> getSupportedSchedulers() {
        return SupportedTaskSchedulers.getSchedulers();
    }

    @GetMapping("/{clusterId}/bound")
    public List<SlurmClusterRec> getClusters(
            @PathVariable Long clusterId){
        return clusterService.getBindedClusters(clusterId);
    }

    @DeleteMapping("/{clusterId}/bound/{bindedCluster}/groups/{groupId}")
    public void deleteGroupProfiles(
            @PathVariable Long clusterId,
            @PathVariable Long groupId,
            @PathVariable String bindedCluster){
        clusterProfileService.revokeAccessFromGroup(clusterId, groupId, bindedCluster);
    }
}
