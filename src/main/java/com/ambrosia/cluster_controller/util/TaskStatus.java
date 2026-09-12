package com.ambrosia.cluster_controller.util;

/**
 * Enumeration with slurm task statuses
 */
public enum TaskStatus {
    BOOT_FAIL,
    CANCELLED,
    COMPLETED,
    DEADLINE,
    FAILED,
    NODE_FAIL,
    OUT_OF_MEMORY,
    PENDING,
    PREEMPTED,
    RUNNING,
    SUSPENDED,
    TIMEOUT;
}
