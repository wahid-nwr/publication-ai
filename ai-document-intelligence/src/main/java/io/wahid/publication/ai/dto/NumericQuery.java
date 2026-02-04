package io.wahid.publication.ai.dto;

import io.wahid.publication.ai.config.NumericQueryType;

import java.util.List;

public class NumericQuery {

    private final NumericQueryType type;

    private final String metric;

    // Used by COMPARE
    private final List<String> stations;

    // Used by TREND
    private final String station;
    private final Integer fromYear;

    // Used by TOP_K / BOTTOM_K
    private final Integer k;

    private NumericQuery(Builder b) {
        this.type = b.type;
        this.metric = b.metric;
        this.stations = b.stations;
        this.station = b.station;
        this.fromYear = b.fromYear;
        this.k = b.k;
    }

    public NumericQueryType getType() { return type; }
    public String getMetric() { return metric; }

    public List<String> getStations() { return stations; }
    public String getStation() { return station; }
    public Integer getFromYear() { return fromYear; }
    public Integer getK() { return k; }

    // ---------- Builder ----------
    public static class Builder {
        private NumericQueryType type;
        private String metric;
        private List<String> stations;
        private String station;
        private Integer fromYear;
        private Integer k;

        public Builder type(NumericQueryType type) {
            this.type = type;
            return this;
        }

        public Builder metric(String metric) {
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

        public NumericQuery build() {
            return new NumericQuery(this);
        }
    }
}
