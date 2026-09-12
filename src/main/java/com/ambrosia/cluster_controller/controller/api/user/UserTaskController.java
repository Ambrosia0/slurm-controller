package com.ambrosia.cluster_controller.controller.api.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.config.security.CustomUserDetails;
import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.service.databaseHandlers.UserTaskService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cluster/{clusterId}")
@PreAuthorize("hasRole('USER')")
@Validated
public class UserTaskController {
    private final UserTaskService taskService;

    @GetMapping("/task")
    public List<SlurmJob> getUserTasks(
        @PathVariable Long clusterId,
        @ModelAttribute @Valid TaskFilter taskFilter,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return taskService.getTasks(
            clusterId,
            userDetails.getId(),  
            taskFilter
        );
    }

    @GetMapping("/bound/{clusterName}/task")
    public List<SlurmJobInfo> pollTasks(
        @PathVariable Long clusterId,
        @PathVariable @NotBlank String clusterName,
        @ModelAttribute TaskPollFilter taskPollFilter,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return taskService.pollTasks(
            clusterId, 
            clusterName,
            userDetails.getId(), 
            taskPollFilter
        );
    }
    
    
    @PostMapping("/bound/{clusterName}/task")
    public void createTask(
        @PathVariable Long clusterId,
        @PathVariable @Valid @NotBlank String clusterName,
        @RequestBody @Valid TaskRequest taskDTO,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskService.createTask(clusterId, clusterName, userDetails.getId(), taskDTO);
    }

    @DeleteMapping("/bound/{clusterName}/task/{taskId}")
    public void cancelTask(
        @PathVariable Long clusterId,
        @PathVariable Long taskId,
        @PathVariable @NotBlank String clusterName,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskService.cancelTask(clusterId,userDetails.getId(),clusterName, taskId);
    }
}
