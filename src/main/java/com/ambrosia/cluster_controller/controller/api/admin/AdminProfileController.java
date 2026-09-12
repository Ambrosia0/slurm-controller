package com.ambrosia.cluster_controller.controller.api.admin;

import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.model.DTO.admin.request.ClusterProfileAdminRequest;
import com.ambrosia.cluster_controller.model.DTO.filters.ClusterProfileFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.ClusterProfileResponse;
import com.ambrosia.cluster_controller.service.databaseHandlers.ClusterProfileManageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/cluster")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminProfileController {

    private final ClusterProfileManageService clusterProfileService;

    @PostMapping("/{clusterId}/bound/{bindedCluster}/profile")
    public void createProfile(
            @PathVariable Long clusterId,
            @PathVariable String bindedCluster,
            @RequestBody @Valid List<ClusterProfileAdminRequest> profileRequests
    ) {
        clusterProfileService.provideAccess(clusterId, bindedCluster, profileRequests);
    }

    @PutMapping("/{clusterId}/bound/{bindedCluster}/profile")
    public void updateProfile(
            @PathVariable Long clusterId,
            @PathVariable String bindedCluster,
            @RequestBody @Valid List<ClusterProfileAdminRequest> profileRequests) {
        clusterProfileService.updateAccessResources(clusterId, bindedCluster, profileRequests);
    }

    @GetMapping("/{clusterId}/bound/{bindedCluster}/download")
    public ResponseEntity<Resource> downloadProfiles(
            @PathVariable Long clusterId,
            @PathVariable String bindedCluster,
            @RequestParam Set<Long> userIds){
        var resource = clusterProfileService.downloadProfiles(clusterId, bindedCluster, userIds);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .headers(headers->{
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            })
            .body(resource);
    }

    @DeleteMapping("/{clusterId}/bound/{bindedCluster}/profile/{userId}")
    public void deleteProfile(
            @PathVariable Long clusterId,
            @PathVariable Long userId,
            @PathVariable String bindedCluster){
        clusterProfileService.revokeAccess(clusterId, bindedCluster, Set.of(userId));
    }

    @DeleteMapping("/{clusterId}/bound/{bindedCluster}/profile")
    public void deleteProfiles(
            @PathVariable Long clusterId,
            @RequestBody Set<Long> userIds,
            @PathVariable String bindedCluster){
        clusterProfileService.revokeAccess(clusterId, bindedCluster, userIds);
    }

    @GetMapping("/profile")
    public Page<ClusterProfileResponse> getProfiles(
            @ModelAttribute @Valid ClusterProfileFilter clusterProfileFilter,
            Pageable pageable
    ) {
        return clusterProfileService.search(clusterProfileFilter, pageable);
    }
}