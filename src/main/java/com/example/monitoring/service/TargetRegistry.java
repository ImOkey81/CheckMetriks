package com.example.monitoring.service;

import com.example.monitoring.config.MonitoringProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class TargetRegistry {

    private final List<MonitoringTarget> targets = new CopyOnWriteArrayList<>();

    public TargetRegistry(MonitoringProperties properties) {
        if (properties.getTargets() != null) {
            for (MonitoringProperties.TargetProperties target : properties.getTargets()) {
                targets.add(new MonitoringTarget(target.getId(), target.getName(), target.getChecks()));
            }
        }
    }

    public List<MonitoringTarget> getTargets() {
        return new ArrayList<>(targets);
    }

    public MonitoringTarget addTarget(MonitoringTarget target) {
        targets.removeIf(existing -> existing.id().equalsIgnoreCase(target.id()));
        targets.add(target);
        return target;
    }

    public record MonitoringTarget(String id, String name, List<Map<String, Object>> checks) {
    }
}
