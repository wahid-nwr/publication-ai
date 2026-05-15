package io.wahid.knowledge.application.domain.weather.model;

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
