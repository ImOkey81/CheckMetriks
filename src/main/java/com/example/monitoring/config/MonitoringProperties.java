package com.example.monitoring.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "monitoring")
@Validated
public class MonitoringProperties {

    @NotNull
    @Min(1000)
    private Long pollIntervalMs;

    @NotNull
    @Min(100)
    private Long requestTimeoutMs;

    @Valid
    @NotEmpty
    private List<TargetProperties> targets = new ArrayList<>();

    public Long getPollIntervalMs() {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(Long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public Long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(Long requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public List<TargetProperties> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetProperties> targets) {
        this.targets = targets;
    }

    public static class TargetProperties {

        @NotBlank
        private String id;

        @NotBlank
        private String name;

        @NotEmpty
        private List<Map<String, Object>> checks = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Map<String, Object>> getChecks() {
            return checks;
        }

        public void setChecks(List<Map<String, Object>> checks) {
            List<Map<String, Object>> normalized = new ArrayList<>();
            if (checks != null) {
                for (Map<String, Object> entry : checks) {
                    normalized.add(new LinkedHashMap<>(entry));
                }
            }
            this.checks = normalized;
        }
    }
}
