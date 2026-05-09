package io.wahid.knowledge.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class YearMetricId implements Serializable {
    private String station;
    private int year;
    private String metric;

    public YearMetricId() {}

    public YearMetricId(String station, int year, String metric) {
        this.station = station;
        this.year = year;
        this.metric = metric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearMetricId that = (YearMetricId) o;
        return station.equals(that.station)
                && year == that.year
                && Objects.equals(metric, that.metric);
    }

    private YearMetricId(YearMetricId.Builder builder) {
        this.station = builder.station;
        this.year = builder.year;
        this.metric = builder.metric;
    }

    public static YearMetricId.Builder builder() {
        return new YearMetricId.Builder();
    }

    public static class Builder {
        private String station;
        private int year;
        private String metric;

        public Builder() {
        }

        public YearMetricId.Builder station(String station) {
            this.station = station;
            return this;
        }

        public YearMetricId.Builder year(int year) {
            this.year = year;
            return this;
        }

        public YearMetricId.Builder metric(String metric) {
            this.metric = metric;
            return this;
        }

        public YearMetricId build() {
            return new YearMetricId(this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, year, metric);
    }

    public String getStation() {
        return station;
    }

    public int getYear() {
        return year;
    }

    public String getMetric() {
        return metric;
    }
}
