package io.wahid.knowledge.config;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum NumericMetric {
    AVG_RAINFALL("avgRainfall", "mm"),
    TOTAL_RAINFALL("totalRainfall", "mm"),
    AVG_SUNSHINE("avgSunshine","hours/day"),
    AVG_HUMIDITY("avgHumidity", "g.m-3"),
    AVG_TEMPERATURE("avgTemperature", "°C"),
    MIN_TEMPERATURE("minTemperature", "°C"),
    MAX_TEMPERATURE("maxTemperature", "°C"),
    NONE("noMetric", "");

    private static final Map<String, NumericMetric> LOOKUP =
            Arrays.stream(NumericMetric.values())
                    .collect(Collectors.toMap(
                            m -> m.getMetricName().toLowerCase(),
                            Function.identity()
                    ));
    private final String metricName;
    private final String unit;

    NumericMetric(String metricName, String unit) {
        this.metricName = metricName;
        this.unit = unit;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getUnit() {
        return unit;
    }

    public static NumericMetric getMetricByName(String metricName) {
        if (metricName == null) {
            return NONE;
        }
        return LOOKUP.getOrDefault(metricName.trim().toLowerCase(), NONE);
    }
}
