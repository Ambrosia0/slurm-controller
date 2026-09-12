package com.ambrosia.cluster_controller.model.DTO.admin.request;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.FieldEqualsOrMore;
import com.ambrosia.cluster_controller.util.TresUtils;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@FieldEqualsOrMore(first = "hardLimit", second = "softLimit", message = "Hard limit must be more or equals to soft limit")
public record ClusterProfileAdminRequest(
    @NotNull
    Long userId,
    
    Integer maxSubmit,

    Integer maxTasks,

    // memory tres in MB
    List<@Pattern(regexp = TresUtils.TRES_PATTERN, message = "Invalid TRES specification!") String> maxTres,
    
    Long softLimit,

    Long hardLimit,

    Integer maxTaskLiveTime
) {}
