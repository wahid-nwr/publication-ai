package io.wahid.knowledge.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "station_summary", uniqueConstraints = {@UniqueConstraint(columnNames = {"station"})})
@Access(AccessType.FIELD)
public class StationSummary {

    @Column(nullable = false)
    private final String station;
    private final int startYear;
    private final int endYear;
    private final double avgTemperature;
    private final double minTemperature;
    private final double maxTemperature;
    private final double avgRainfall;
    private final double totalRainfall;
    private final String rainyMonths;
    private final double avgSunshine;
    private final double avgHumidity;
    private final int dataPoints;
    @Lob
    @Column(columnDefinition = "text")
    private final String summaryText;   // 👈 embed this
    private final String embeddingId;
    private final Instant createdAt;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID summaryId;

    protected StationSummary() {
        this.station = null;
        this.startYear = 0;
        this.endYear = 0;
        this.avgTemperature = 0;
        this.minTemperature = 0;
        this.maxTemperature = 0;
        this.avgRainfall = 0;
        this.totalRainfall = 0;
        this.rainyMonths = null;
        this.avgSunshine = 0;
        this.avgHumidity = 0;
        this.dataPoints = 0;
        this.summaryText = null;
        this.embeddingId = null;
        this.createdAt = null;
    }

    private StationSummary(Builder builder) {
        this.station = builder.station;
        this.startYear = builder.startYear;
        this.endYear = builder.endYear;
        this.avgTemperature = builder.avgTemperature;
        this.minTemperature = builder.minTemperature;
        this.maxTemperature = builder.maxTemperature;
        this.avgRainfall = builder.avgRainfall;
        this.totalRainfall = builder.totalRainfall;
        this.rainyMonths = builder.rainyMonths;
        this.avgSunshine = builder.avgSunshine;
        this.avgHumidity = builder.avgHumidity;
        this.dataPoints = builder.dataPoints;
        this.summaryText = builder.summaryText;
        this.embeddingId = builder.embeddingId;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getSummaryId() {
        return summaryId;
    }

    public String getStation() {
        return station;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public double getAvgTemperature() {
        return avgTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getAvgRainfall() {
        return avgRainfall;
    }

    public double getTotalRainfall() {
        return totalRainfall;
    }

    public String getRainyMonths() {
        return rainyMonths;
    }

    public double getAvgSunshine() {
        return avgSunshine;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public int getDataPoints() {
        return dataPoints;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public String getEmbeddingId() {
        return embeddingId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private String station;
        private int startYear;
        private int endYear;

        private double avgTemperature;
        private double minTemperature;
        private double maxTemperature;

        private double avgRainfall;
        private double totalRainfall;
        private String rainyMonths;

        private double avgSunshine;
        private double avgHumidity;

        private int dataPoints;

        private String summaryText;   // 👈 embed this
        private String embeddingId;

        private Instant createdAt;

        public Builder() {
        }

        public Builder station(String station) {
            this.station = station;
            return this;
        }

        public Builder startYear(int startYear) {
            this.startYear = startYear;
            return this;
        }

        public Builder endYear(int endYear) {
            this.endYear = endYear;
            return this;
        }

        public Builder avgTemperature(double avgTemperature) {
            this.avgTemperature = avgTemperature;
            return this;
        }

        public Builder minTemperature(double minTemperature) {
            this.minTemperature = minTemperature;
            return this;
        }

        public Builder maxTemperature(double maxTemperature) {
            this.maxTemperature = maxTemperature;
            return this;
        }

        public Builder avgRainfall(double avgRainfall) {
            this.avgRainfall = avgRainfall;
            return this;
        }

        public Builder totalRainfall(double totalRainfall) {
            this.totalRainfall = totalRainfall;
            return this;
        }

        public Builder rainyMonths(String rainyMonths) {
            this.rainyMonths = rainyMonths;
            return this;
        }

        public Builder avgSunshine(double avgSunshine) {
            this.avgSunshine = avgSunshine;
            return this;
        }

        public Builder avgHumidity(double avgHumidity) {
            this.avgHumidity = avgHumidity;
            return this;
        }

        public Builder dataPoints(int dataPoints) {
            this.dataPoints = dataPoints;
            return this;
        }

        public Builder summaryText(String summaryText) {
            this.summaryText = summaryText;
            return this;
        }

        public Builder embeddingId(String embeddingId) {
            this.embeddingId = embeddingId;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StationSummary build() {
            return new StationSummary(this);
        }
    }
}
