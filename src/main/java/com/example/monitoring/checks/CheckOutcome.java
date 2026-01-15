package com.example.monitoring.checks;

public record CheckOutcome(boolean ok, Long latencyMs, String message) {
}
