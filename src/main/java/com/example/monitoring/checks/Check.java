package com.example.monitoring.checks;

import java.util.Map;

public interface Check {

    String type();

    CheckOutcome run(String targetId, Map<String, Object> cfg);
}
