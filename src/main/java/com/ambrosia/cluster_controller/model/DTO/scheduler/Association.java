package com.ambrosia.cluster_controller.model.DTO.scheduler;

import java.util.List;

public record Association(
    String username,
    String password,
    int maxSubmit,
    int maxTasks,
    List<String> maxTres,
    int maxTaskTtl,
    long softLimit,
    long hardLimit
) {}
