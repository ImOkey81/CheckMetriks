package com.example.monitoring.service;

import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MonitoringScheduler {

    private final CheckExecutor executor;
    private final TargetRegistry targetRegistry;

    public MonitoringScheduler(CheckExecutor executor, TargetRegistry targetRegistry) {
        this.executor = executor;
        this.targetRegistry = targetRegistry;
    }

    @Scheduled(fixedDelayString = "${monitoring.poll-interval-ms}")
    public void pollTargets() {
        for (TargetRegistry.MonitoringTarget target : targetRegistry.getTargets()) {
            for (Map<String, Object> check : target.checks()) {
                executor.execute(target.id(), check);
            }
        }
    }
}
