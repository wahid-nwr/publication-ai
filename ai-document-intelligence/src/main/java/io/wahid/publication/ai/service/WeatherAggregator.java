package io.wahid.publication.ai.service;

import io.wahid.publication.ai.dto.WeatherInfo;

public interface WeatherAggregator {

    void accept(WeatherInfo info);

    void finish() throws Exception;   // called once when parsing ends
}
