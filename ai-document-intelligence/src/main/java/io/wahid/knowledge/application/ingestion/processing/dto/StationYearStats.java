package io.wahid.knowledge.application.ingestion.processing.dto;

import io.wahid.knowledge.application.query.NumericMetric;
import io.wahid.knowledge.model.StationYearMetric;
import io.wahid.knowledge.model.YearMetricId;

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
            case AVG_TEMPERATURE: value += info.getTemperature();
            case TOTAL_RAINFALL: value += info.getRainfall();
            case AVG_SUNSHINE: value += info.getSunshine();
            case AVG_HUMIDITY: value += info.getHumidity();
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
