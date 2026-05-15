package io.wahid.knowledge.application.domain.weather.insights.weather;

import io.wahid.knowledge.application.domain.weather.model.DailyWeatherMeasurement;

public interface WeatherAggregator {

    void accept(DailyWeatherMeasurement info);

    void finish() throws Exception;   // called once when parsing ends
}
