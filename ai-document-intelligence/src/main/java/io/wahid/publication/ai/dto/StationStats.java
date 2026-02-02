package io.wahid.publication.ai.dto;

import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.service.StationSummaryFormatter;
import io.wahid.publication.ai.service.impl.DefaultStationSummaryFormatter;

import java.time.Instant;

public class StationStats {

    private final StationSummaryFormatter summaryFormatter = new DefaultStationSummaryFormatter();
    private int count;
    private double tempSum;
    private double minTemp = Double.MAX_VALUE;
    private double maxTemp = Double.MIN_VALUE;
    private double rainfallSum;
    private double sunshineSum;
    private double humiditySum;
    private int startYear = Integer.MAX_VALUE;
    private int endYear = Integer.MIN_VALUE;

    public void add(WeatherInfo info) {
        count++;
        tempSum += info.getTemperature();
        rainfallSum += info.getRainfall();
        sunshineSum += info.getSunshine();
        humiditySum += info.getHumidity();

        minTemp = Math.min(minTemp, info.getTemperature());
        maxTemp = Math.max(maxTemp, info.getTemperature());

        startYear = Math.min(startYear, info.getYear());
        endYear = Math.max(endYear, info.getYear());
    }

    public StationSummary toSummary(String station) {
        StationSummary.Builder builder = StationSummary.builder()
                .station(station)
                .startYear(startYear)
                .endYear(endYear)
                .avgTemperature(tempSum / count)
                .minTemperature(minTemp)
                .maxTemperature(maxTemp)
                .avgRainfall(rainfallSum / count)
                .totalRainfall(rainfallSum)
                .avgSunshine(sunshineSum / count)
                .avgHumidity(humiditySum / count)
                .dataPoints(count)
                .createdAt(Instant.now());
        String summaryText = summaryFormatter.format(builder.build());

        return StationSummary.builder()
                .station(station)
                .startYear(startYear)
                .endYear(endYear)
                .avgTemperature(tempSum / count)
                .minTemperature(minTemp)
                .maxTemperature(maxTemp)
                .avgRainfall(rainfallSum / count)
                .totalRainfall(rainfallSum)
                .avgSunshine(sunshineSum / count)
                .avgHumidity(humiditySum / count)
                .dataPoints(count)
                .summaryText(summaryText)
                .createdAt(Instant.now())
                .build();
    }
}
