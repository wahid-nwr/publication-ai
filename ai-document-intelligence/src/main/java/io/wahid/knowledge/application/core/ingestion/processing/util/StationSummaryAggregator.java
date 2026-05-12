package io.wahid.knowledge.application.core.ingestion.processing.util;

import io.wahid.knowledge.application.core.ingestion.processing.dto.WeatherRow;
import io.wahid.knowledge.model.StationSummary;

import java.time.Instant;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class StationSummaryAggregator {

    public StationSummary aggregate(String station, Stream<WeatherRow> rows) {

        StationAccumulator acc = new StationAccumulator();

        rows.forEach(acc::accept);

        if (acc.count == 0) {
            throw new IllegalStateException("No data for station " + station);
        }

        String rainyMonths = detectRainyMonths(acc.monthlyRainfall);

        return StationSummary.builder()
                .station(station)
                .startYear(acc.startYear)
                .endYear(acc.endYear)

                .avgTemperature(acc.tempSum / acc.count)
                .minTemperature(acc.minTemp)
                .maxTemperature(acc.maxTemp)

                .avgRainfall(acc.rainfallSum / acc.count)
                .totalRainfall(acc.rainfallSum)
                .rainyMonths(rainyMonths)

                .avgSunshine(acc.sunshineSum / acc.count)
                .avgHumidity(acc.humiditySum / acc.count)

                .dataPoints(acc.count)

                .createdAt(Instant.now())
                .build();
    }

    private String detectRainyMonths(double[] monthlyRainfall) {

        double total = Arrays.stream(monthlyRainfall).sum();
        if (total == 0) return "None";

        double threshold = total * 0.6; // 60% of rain
        double running = 0;

        List<Integer> months = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            running += monthlyRainfall[i];
            months.add(i + 1);
            if (running >= threshold) break;
        }

        return formatMonthRange(months);
    }

    private String formatMonthRange(List<Integer> months) {
        if (months.isEmpty()) return "None";
        if (months.size() == 1) return monthName(months.get(0));
        return monthName(months.get(0)) + "–" + monthName(months.get(months.size() - 1));
    }

    private String monthName(int m) {
        return Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
