package com.example.monitoring.util;

import java.util.Map;

public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static String getRequiredString(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required config: " + key);
        }
        String str = value.toString();
        if (str.isBlank()) {
            throw new IllegalArgumentException("Blank config value: " + key);
        }
        return str;
    }

    public static int getRequiredInt(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required config: " + key);
        }
        return parseInt(value, key);
    }

    public static Integer getOptionalInt(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            return null;
        }
        return parseInt(value, key);
    }

    public static long getOptionalLong(Map<String, Object> config, String key, long defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        return parseLong(value, key);
    }

    public static long getOptionalLong(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            return 0L;
        }
        return parseLong(value, key);
    }

    private static int parseInt(Object value, String key) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer for " + key + ": " + value);
        }
    }

    private static long parseLong(Object value, String key) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid long for " + key + ": " + value);
        }
    }
}
