package io.wahid.knowledge.dto;

public record WeatherRow(
        String station,
        int year,
        int month,
        int day,
        double rainfall,
        double sunshine,
        double humidity,
        double temperature
) {
}
