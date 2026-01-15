package com.example.monitoring.service;

import com.example.monitoring.config.MonitoringProperties;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MonitoringScheduler {

    private final MonitoringProperties properties;
    private final CheckExecutor executor;

    public MonitoringScheduler(MonitoringProperties properties, CheckExecutor executor) {
        this.properties = properties;
        this.executor = executor;
    }

    @Scheduled(fixedDelayString = "${monitoring.poll-interval-ms}")
    public void pollTargets() {
        for (MonitoringProperties.TargetProperties target : properties.getTargets()) {
            for (Map<String, Object> check : target.getChecks()) {
                executor.execute(target.getId(), check);
            }
        }
    }
}
