package com.example.monitoring.service;

import com.example.monitoring.checks.Check;
import com.example.monitoring.checks.CheckOutcome;
import com.example.monitoring.model.CheckResult;
import com.example.monitoring.repository.CheckResultRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CheckExecutor {

    private final Map<String, Check> checksByType;
    private final CheckResultRepository repository;
    private final MetricsRecorder metricsRecorder;

    public CheckExecutor(List<Check> checks,
                         CheckResultRepository repository,
                         MetricsRecorder metricsRecorder) {
        this.checksByType = checks.stream()
                .collect(Collectors.toMap(
                        check -> check.type().toUpperCase(Locale.ROOT),
                        check -> check));
        this.repository = repository;
        this.metricsRecorder = metricsRecorder;
    }

    public void execute(String targetId, Map<String, Object> rawConfig) {
        CheckDefinition definition = toDefinition(rawConfig);
        if (definition.type() == null || definition.type().isBlank()) {
            persistResult(targetId, "UNKNOWN", false, null, "Missing check type in configuration");
            return;
        }
        String normalizedType = definition.type().toUpperCase(Locale.ROOT);
        Check check = checksByType.get(normalizedType);
        if (check == null) {
            persistResult(targetId, normalizedType, false, null,
                    "No check implementation for type " + normalizedType);
            return;
        }

        CheckOutcome outcome;
        try {
            outcome = check.run(targetId, definition.config());
        } catch (Exception ex) {
            String message = ex.getClass().getSimpleName();
            String detail = Optional.ofNullable(ex.getMessage()).orElse("");
            if (!detail.isBlank()) {
                message = message + ": " + detail;
            }
            persistResult(targetId, normalizedType, false, null, message);
            return;
        }

        persistResult(targetId, normalizedType, outcome.ok(), outcome.latencyMs(), outcome.message());
    }

    private CheckDefinition toDefinition(Map<String, Object> raw) {
        Map<String, Object> config = new LinkedHashMap<>(raw);
        Object type = config.remove("type");
        String typeValue = type != null ? type.toString() : null;
        return new CheckDefinition(typeValue, config);
    }

    private void persistResult(String targetId,
                               String checkType,
                               boolean ok,
                               Long latencyMs,
                               String message) {
        CheckResult result = new CheckResult();
        result.setTargetId(targetId);
        result.setCheckType(checkType);
        result.setOk(ok);
        result.setLatencyMs(latencyMs);
        result.setMessage(message);
        repository.save(result);
        metricsRecorder.record(targetId, checkType, ok, latencyMs);
    }
}
