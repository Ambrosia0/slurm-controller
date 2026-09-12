package com.ambrosia.cluster_controller.taskSchedulers.slurm.v0042;

import org.springframework.stereotype.Service;

import com.ambrosia.cluster_controller.service.systemServices.SshCommandSender;
import com.ambrosia.cluster_controller.taskSchedulers.slurm.SchedulerVerificator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SchedulerVerificatorImpl implements SchedulerVerificator{
    private final SshCommandSender sender;

    @Override
    public boolean isSchedulerCorrect(String hostname, String username, String password, Integer port) {
        try {
            var result = sender.executeOnce(
                hostname, 
                username, 
                password, 
                port==null? 22: port, 
                "squeue | grep 'JOBID'"
            );
            if(result.contains("not found"))
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
