package io.wahid.knowledge.service.impl;

import io.wahid.knowledge.model.StationSummary;
import io.wahid.knowledge.service.StationSummaryFormatter;

public class DefaultStationSummaryFormatter implements StationSummaryFormatter {

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    @Override
    public String format(StationSummary s) {

        StringBuilder sb = new StringBuilder(256);

        sb.append("Weather summary for station ")
                .append(s.getStation())
                .append(" covering years ")
                .append(s.getStartYear())
                .append(" to ")
                .append(s.getEndYear())
                .append(". ");

        sb.append("Based on ")
                .append(s.getDataPoints())
                .append(" daily observations, ");

        sb.append("the average temperature was ")
                .append(round(s.getAvgTemperature()))
                .append("°C, with a minimum of ")
                .append(round(s.getMinTemperature()))
                .append("°C and a maximum of ")
                .append(round(s.getMaxTemperature()))
                .append("°C. ");

        sb.append("Average rainfall was ")
                .append(round(s.getAvgRainfall()))
                .append(" mm, totaling ")
                .append(round(s.getTotalRainfall()))
                .append(" mm over the period. ");

        if (s.getRainyMonths() != null && !s.getRainyMonths().isBlank()) {
            sb.append("Rainfall was concentrated in ")
                    .append(s.getRainyMonths())
                    .append(". ");
        }

        sb.append("Average sunshine was ")
                .append(round(s.getAvgSunshine()))
                .append(" hours per day, ");

        sb.append("with average humidity at ")
                .append(round(s.getAvgHumidity()))
                .append("%. ");

        return sb.toString();
    }
}
