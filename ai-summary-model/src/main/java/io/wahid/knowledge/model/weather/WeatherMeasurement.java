package io.wahid.knowledge.model.weather;

import io.wahid.knowledge.domain.common.DomainRecord;
import io.wahid.knowledge.domain.common.DomainType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WeatherMeasurement implements DomainRecord {

    private String recordId;

    private String stationId;

    private LocalDate date;

    private double temperature;

    private double humidity;

    private double precipitation;

    @Override
    public String getRecordId() {
        return recordId;
    }

    @Override
    public DomainType getDomainType() {
        return DomainType.WEATHER;
    }

    @Override
    public String toSearchableText() {

        return String.format(
                "Station %s on %s had temperature %.2f, humidity %.2f and precipitation %.2f",
                stationId,
                date,
                temperature,
                humidity,
                precipitation
        );
    }

    @Override
    public Map<String, Object> getAttributes() {

        Map<String, Object> attributes = new HashMap<>();

        attributes.put("stationId", stationId);
        attributes.put("date", date);
        attributes.put("temperature", temperature);
        attributes.put("humidity", humidity);
        attributes.put("precipitation", precipitation);

        return attributes;
    }

    // getters/setters
}
