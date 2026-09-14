package com.ambrosia.cluster_controller.controller.api.admin;

import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
@Validated
public class AdminProfileController {

    private final ClusterProfileManageService clusterProfileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{clusterId}/bound/{clusterName}/profile")
    public void createProfile(
            @PathVariable Long clusterId,
            @PathVariable String clusterName,
            @RequestBody @Valid List<ClusterProfileAdminRequest> profileRequests
    ) {
        clusterProfileService.provideAccess(clusterId, clusterName, profileRequests);
    }

    @PutMapping("/{clusterId}/bound/{clusterName}/profile")
    public void updateProfile(
            @PathVariable Long clusterId,
            @PathVariable String clusterName,
            @RequestBody @Valid List<ClusterProfileAdminRequest> profileRequests) {
        clusterProfileService.updateAccessResources(clusterId, clusterName, profileRequests);
    }

    @GetMapping("/{clusterId}/bound/{clusterName}/download")
    public ResponseEntity<Resource> downloadProfiles(
            @PathVariable Long clusterId,
            @PathVariable String clusterName,
            @RequestParam Set<Long> userIds){
        var resource = clusterProfileService.downloadProfiles(clusterId, clusterName, userIds);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .headers(headers->{
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            })
            .body(resource);
    }

    @DeleteMapping("/{clusterId}/bound/{clusterName}/profile/{userId}")
    public void deleteProfile(
            @PathVariable Long clusterId,
            @PathVariable Long userId,
            @PathVariable String clusterName){
        clusterProfileService.revokeAccess(clusterId, clusterName, Set.of(userId));
    }

    @DeleteMapping("/{clusterId}/bound/{clusterName}/profile")
    public void deleteProfiles(
            @PathVariable Long clusterId,
            @RequestParam Set<Long> userIds,
            @PathVariable String clusterName){
        clusterProfileService.revokeAccess(clusterId, clusterName, userIds);
    }

    @GetMapping("/profile")
    public Page<ClusterProfileResponse> getProfiles(
            @ModelAttribute @Valid ClusterProfileFilter clusterProfileFilter,
            Pageable pageable
    ) {
        return clusterProfileService.search(clusterProfileFilter, pageable);
    }
}