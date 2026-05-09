package io.wahid.knowledge.service;

import io.wahid.knowledge.dto.WeatherInfo;

public interface WeatherAggregator {

    void accept(WeatherInfo info);

    void finish() throws Exception;   // called once when parsing ends
}
