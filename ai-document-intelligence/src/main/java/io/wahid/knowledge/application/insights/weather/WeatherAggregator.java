package io.wahid.knowledge.application.insights.weather;

import io.wahid.knowledge.application.ingestion.processing.dto.WeatherInfo;

public interface WeatherAggregator {

    void accept(WeatherInfo info);

    void finish() throws Exception;   // called once when parsing ends
}
