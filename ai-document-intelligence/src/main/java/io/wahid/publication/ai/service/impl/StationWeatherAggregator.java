package io.wahid.publication.ai.service.impl;

import io.wahid.publication.ai.dto.StationStats;
import io.wahid.publication.ai.dto.WeatherInfo;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.service.WeatherAggregator;

import java.util.HashMap;
import java.util.Map;

public class StationWeatherAggregator implements WeatherAggregator {

    private final Map<String, StationStats> statsByStation = new HashMap<>();

    @Override
    public void accept(WeatherInfo info) {
        statsByStation
                .computeIfAbsent(info.getStation(), s -> new StationStats())
                .add(info);
    }

    @Override
    public void finish() {
        for (Map.Entry<String, StationStats> entry : statsByStation.entrySet()) {
            StationSummary summary = entry.getValue().toSummary(entry.getKey());

            // 👇 persist + embed later
            // stationSummaryRepository.save(summary);
        }
    }
}
