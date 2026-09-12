package com.ambrosia.cluster_controller.model.DTO.generic;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder 
public record TaskRequest(
    @Pattern(regexp = "^#!/bin/bash\\\\n.+", message = "Script must start with #!/bin/bash")
    String script,

    List<@Valid JobRequest> jobs,

    @Valid
    JobRequest job
) {}
