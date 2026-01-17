package com.example.monitoring.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TargetRequest {

    @NotBlank
    private String name;

    @NotEmpty
    private List<Map<String, Object>> checks = new ArrayList<>();

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
