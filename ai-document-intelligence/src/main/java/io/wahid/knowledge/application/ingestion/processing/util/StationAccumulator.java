package io.wahid.knowledge.application.ingestion.processing.util;

import io.wahid.knowledge.application.ingestion.processing.dto.WeatherRow;

final class StationAccumulator {

    String station;

    int startYear = Integer.MAX_VALUE;
    int endYear = Integer.MIN_VALUE;

    double tempSum = 0;
    double minTemp = Double.MAX_VALUE;
    double maxTemp = Double.MIN_VALUE;

    double rainfallSum = 0;
    double sunshineSum = 0;
    double humiditySum = 0;

    int count = 0;

    // rainfall per month (1–12)
    double[] monthlyRainfall = new double[12];

    void accept(WeatherRow row) {
        station = row.station();

        startYear = Math.min(startYear, row.year());
        endYear = Math.max(endYear, row.year());

        tempSum += row.temperature();
        minTemp = Math.min(minTemp, row.temperature());
        maxTemp = Math.max(maxTemp, row.temperature());

        rainfallSum += row.rainfall();
        sunshineSum += row.sunshine();
        humiditySum += row.humidity();

        monthlyRainfall[row.month() - 1] += row.rainfall();

        count++;
    }
}
