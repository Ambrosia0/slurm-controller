package com.ambrosia.cluster_controller.taskSchedulers.slurm;

public interface SchedulerVerificator {
    boolean isSchedulerCorrect(String hostname, String username, String password, Integer port);
}
