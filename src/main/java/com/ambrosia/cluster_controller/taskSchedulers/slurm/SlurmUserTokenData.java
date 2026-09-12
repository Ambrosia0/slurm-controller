package com.ambrosia.cluster_controller.taskSchedulers.slurm;

import java.time.Instant;

public record SlurmUserTokenData(String token, Instant expirationTime){}