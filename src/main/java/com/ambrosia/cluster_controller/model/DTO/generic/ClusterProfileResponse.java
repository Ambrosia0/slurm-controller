package com.ambrosia.cluster_controller.model.DTO.generic;

import java.time.Instant;
import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.admin.response.UserAdminResponse;

public record ClusterProfileResponse(
    long clusterId,
    UserAdminResponse user,
    String bindedCluster,
    int profileId,
    int maxSubmit,
    int maxJobs,
    List<String> maxTres,
    Long softLimit,
    Long hardLimit,
    Integer maxTaskLiveTime,
    Instant createdAt
) {}