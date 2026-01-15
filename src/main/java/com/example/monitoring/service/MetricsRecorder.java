package com.example.monitoring.service;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class MetricsRecorder {

    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicLong> latencyGauges = new ConcurrentHashMap<>();

    public MetricsRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void record(String targetId, String checkType, boolean ok, Long latencyMs) {
        meterRegistry.counter("monitor_check_total",
                "target", targetId,
                "type", checkType,
                "ok", Boolean.toString(ok))
                .increment();

        if (latencyMs != null) {
            String key = targetId + "|" + checkType;
            AtomicLong gauge = latencyGauges.computeIfAbsent(key, k -> {
                AtomicLong holder = new AtomicLong();
                meterRegistry.gauge("monitor_check_last_latency_ms",
                        Map.of("target", targetId, "type", checkType),
                        holder,
                        AtomicLong::get);
                return holder;
            });
            gauge.set(latencyMs);
        }
    }
}
