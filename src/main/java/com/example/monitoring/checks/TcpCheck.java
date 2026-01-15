package com.example.monitoring.checks;

import com.example.monitoring.config.MonitoringProperties;
import com.example.monitoring.util.ConfigUtils;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class TcpCheck implements Check {

    private final MonitoringProperties properties;

    public TcpCheck(MonitoringProperties properties) {
        this.properties = properties;
    }

    @Override
    public String type() {
        return "TCP";
    }

    @Override
    public CheckOutcome run(String targetId, java.util.Map<String, Object> cfg) {
        String host = ConfigUtils.getRequiredString(cfg, "host");
        int port = ConfigUtils.getRequiredInt(cfg, "port");
        long timeoutMs = ConfigUtils.getOptionalLong(cfg, "connectTimeoutMs", properties.getRequestTimeoutMs());

        long start = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), (int) timeoutMs);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        long latencyMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
        return new CheckOutcome(true, latencyMs, "OK");
    }
}
