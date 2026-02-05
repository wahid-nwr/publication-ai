package io.wahid.publication.ai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "station_year_summary")
@Access(AccessType.FIELD)
public class StationYearMetric {
    @EmbeddedId
    private final YearMetricId yearMetricId;
    private final double value;

    protected StationYearMetric() {
        this.yearMetricId = null;
        this.value = 0;
    }

    private StationYearMetric(StationYearMetric.Builder builder) {
        this.yearMetricId = builder.yearMetricId;
        this.value = builder.value;
    }

    public static StationYearMetric.Builder builder() {
        return new StationYearMetric.Builder();
    }

    public YearMetricId getYearMetricId() {
        return yearMetricId;
    }

    public double getValue() {
        return value;
    }

    public static class Builder {
        private YearMetricId yearMetricId;
        private double value;

        public Builder() {
        }

        public Builder yearMetricId(YearMetricId yearMetricId) {
            this.yearMetricId = yearMetricId;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public StationYearMetric build() {
            return new StationYearMetric(this);
        }
    }
}
