package com.example.monitoring.service;

import java.util.Map;

public record CheckDefinition(String type, Map<String, Object> config) {
}
