package com.example.monitoring.checks;

import com.example.monitoring.config.MonitoringProperties;
import com.example.monitoring.util.ConfigUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class HttpCheck implements Check {

    private final HttpClient httpClient;
    private final MonitoringProperties properties;

    public HttpCheck(MonitoringProperties properties) {
        this.httpClient = HttpClient.newBuilder().build();
        this.properties = properties;
    }

    @Override
    public String type() {
        return "HTTP";
    }

    @Override
    public CheckOutcome run(String targetId, java.util.Map<String, Object> cfg) {
        String url = ConfigUtils.getRequiredString(cfg, "url");
        Integer expected = ConfigUtils.getOptionalInt(cfg, "expectStatus");
        int expectStatus = expected != null ? expected : 200;
        long maxLatencyMs = ConfigUtils.getOptionalLong(cfg, "maxLatencyMs");
        long timeoutMs = ConfigUtils.getOptionalLong(cfg, "timeoutMs", properties.getRequestTimeoutMs());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeoutMs))
                .GET()
                .build();

        long start = System.nanoTime();
        HttpResponse<Void> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        long latencyMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

        if (response.statusCode() != expectStatus) {
            return new CheckOutcome(false, latencyMs, "Unexpected status: " + response.statusCode());
        }
        if (maxLatencyMs > 0 && latencyMs > maxLatencyMs) {
            return new CheckOutcome(false, latencyMs, "Latency exceeded: " + latencyMs + "ms");
        }
        return new CheckOutcome(true, latencyMs, "OK");
    }
}
