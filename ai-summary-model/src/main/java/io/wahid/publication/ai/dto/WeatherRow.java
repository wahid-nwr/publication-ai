package io.wahid.publication.ai.dto;

public record WeatherRow(
        String station,
        int year,
        int month,
        double rainfall,
        double sunshine,
        double humidity,
        double temperature
) {
}
