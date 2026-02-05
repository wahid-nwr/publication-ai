package io.wahid.publication.ai.dto;

import io.wahid.publication.ai.config.NumericMetric;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.model.StationYearMetric;
import io.wahid.publication.ai.model.YearMetricId;
import io.wahid.publication.ai.service.StationSummaryFormatter;
import io.wahid.publication.ai.service.impl.DefaultStationSummaryFormatter;

import java.time.Instant;

public class StationYearStats {

    private int count;
    private String metric;
    private double value;
    private String station;
    private int year;

    public StationYearStats(String station, int year, String metric, double value) {
        this.station = station;
        this.year = year;
        this.metric = metric;
        this.value = value;
    }

    public void add(WeatherInfo info, NumericMetric metric) {
        count++;
        switch (metric) {
            case avgTemperature: value += info.getTemperature();
            case totalRainfall: value += info.getRainfall();
            case avgSunshine: value += info.getSunshine();
            case avgHumidity: value += info.getHumidity();
            default:
        }
    }

    public StationYearMetric toYearSummary(String station, int year) {
        YearMetricId yearMetricId = YearMetricId.builder()
                .station(station)
                .metric(metric)
                .year(year).build();
        StationYearMetric.Builder builder = StationYearMetric.builder()
                .yearMetricId(yearMetricId)
                .value(value);
        return builder.build();
    }

    public String getStation() {
        return station;
    }

    public int getYear() {
        return year;
    }
}
