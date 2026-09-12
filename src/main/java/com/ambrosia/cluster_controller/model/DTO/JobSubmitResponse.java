package com.ambrosia.cluster_controller.model.DTO;

import lombok.Builder;

@Builder 
public record JobSubmitResponse(
    Integer jobId,
    String stepId,
    String jobSubmitUserMessage
) {}
