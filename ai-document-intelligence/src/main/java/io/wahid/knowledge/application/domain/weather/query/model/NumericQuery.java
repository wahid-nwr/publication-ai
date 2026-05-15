package io.wahid.knowledge.application.domain.weather.query.model;

import io.wahid.knowledge.domain.query.Query;

import java.util.List;

public class NumericQuery extends Query {

    private final boolean timeBased;
    private final boolean numeric;
    private final double value;
    private final NumericQueryType nemerictype;

    private final NumericMetric metric;

    // Used by COMPARE
    private final List<String> stations;

    // Used by TREND
    private final String station;
    private final Integer fromYear;

    // Used by TOP_K / BOTTOM_K
    private final Integer k;

    private NumericQuery(Builder b) {
        this.nemerictype = b.type;
        this.metric = b.metric;
        this.stations = b.stations;
        this.station = b.station;
        this.fromYear = b.fromYear;
        this.k = b.k;
        this.timeBased = b.timeBased;
        this.numeric = b.numeric;
        this.value = b.value;
    }

    public NumericQueryType getNemericType() { return this.nemerictype; }
    public NumericMetric getMetric() { return metric; }

    public List<String> getStations() { return stations; }
    public String getStation() { return station; }
    public Integer getFromYear() { return fromYear; }
    public Integer getK() { return k; }

    public boolean isTimeBased() {
        return timeBased;
    }

    public boolean isNumeric() {
        return numeric;
    }

    public double getValue() {
        return value;
    }

    // ---------- Builder ----------
    public static class Builder {
        private NumericQueryType type;
        private NumericMetric metric;
        private List<String> stations;
        private String station;
        private Integer fromYear;
        private Integer k;
        private boolean timeBased;
        private boolean numeric;
        private double value;

        public Builder type(NumericQueryType type) {
            this.type = type;
            return this;
        }

        public Builder metric(NumericMetric metric) {
            this.metric = metric;
            return this;
        }

        public Builder stations(List<String> stations) {
            this.stations = stations;
            return this;
        }

        public Builder station(String station) {
            this.station = station;
            return this;
        }

        public Builder fromYear(Integer fromYear) {
            this.fromYear = fromYear;
            return this;
        }

        public Builder k(Integer k) {
            this.k = k;
            return this;
        }

        public Builder timeBased(boolean timeBased) {
            this.timeBased = timeBased;
            return this;
        }

        public Builder numeric(boolean numeric) {
            this.numeric = numeric;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public NumericQuery build() {
            return new NumericQuery(this);
        }
    }
}
