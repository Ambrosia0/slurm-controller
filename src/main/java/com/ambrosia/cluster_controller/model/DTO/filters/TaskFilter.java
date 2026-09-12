package com.ambrosia.cluster_controller.model.DTO.filters;

import java.util.List;
import java.util.Set;

import com.ambrosia.cluster_controller.util.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilter{
    private Set<TaskStatus> taskStatus;
    private Long startTime;
    private Long endTime;
    private List<String> usernames;
    private Long groupId;
    private @Size(max = 5) Set<@NotBlank String> clusterNames;
}