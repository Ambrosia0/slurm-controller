package com.ambrosia.cluster_controller.controller.api.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.cluster_controller.model.DTO.filters.TaskFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskPollFilter;
import com.ambrosia.cluster_controller.model.DTO.generic.TaskRequest;
import com.ambrosia.cluster_controller.service.databaseHandlers.AdminTaskService;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJob;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042.DTO.functional.jobs.SlurmJobInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
@RequestMapping("/api/admin/cluster/{clusterId}")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminTaskController {
    private final AdminTaskService taskService;

    @GetMapping
    public List<SlurmJob> get(
            @PathVariable Long clusterId,
            @ModelAttribute @Valid TaskFilter taskFilter) {
        return taskService.getTasks(clusterId, taskFilter);
    }

    @GetMapping("/bound/{clusterName}/task")
    public List<SlurmJobInfo> pollTasks(
        @PathVariable Long clusterId,
        @PathVariable @NotBlank String clusterName,
        @ModelAttribute TaskPollFilter taskPollFilter
    ) {
        return taskService.pollTasks(
            clusterId, 
            clusterName,
            taskPollFilter
        );
    }

    @PostMapping("/bound/{clusterName}/task")
    public void create(
            @PathVariable Long clusterId,
            @PathVariable String bindedCluster,
            @RequestBody @Valid TaskRequest taskDTO) {
        taskService.createTask(clusterId, bindedCluster, taskDTO);
    }

    
    @DeleteMapping("/bound/{clusterName}/task/{taskId}")
    public void cancelTask(
            @PathVariable Long taskId,
            @PathVariable String bindedCluster,
            @PathVariable Long clusterId) {
        taskService.cancelTask(clusterId, bindedCluster, taskId);
    }
}
