package io.wahid.knowledge.application.core.query.mapper;

import io.wahid.knowledge.application.core.query.dto.GraphResult;
import io.wahid.knowledge.model.StationSummary;

import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.AVG_HUMIDITY;
import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.AVG_RAINFALL;
import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.AVG_SUNSHINE;
import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.AVG_TEMPERATURE;
import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.MAX_TEMPERATURE;
import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.MIN_TEMPERATURE;
import static io.wahid.knowledge.application.domain.weather.query.model.NumericMetric.TOTAL_RAINFALL;

public class StationSummaryMapper {
    private StationSummaryMapper() {}
    public static StationSummary map(GraphResult r) {
        return StationSummary.builder()
                .station(r.station())
                .avgRainfall(r.metric().equals(AVG_RAINFALL.getMetricName()) ? r.value() : 0)
                .totalRainfall(r.metric().equals(TOTAL_RAINFALL.getMetricName()) ? r.value() : 0)
                .avgSunshine(r.metric().equals(AVG_SUNSHINE.getMetricName()) ? r.value() : 0)
                .avgHumidity(r.metric().equals(AVG_HUMIDITY.getMetricName()) ? r.value() : 0)
                .avgTemperature(r.metric().equals(AVG_TEMPERATURE.getMetricName()) ? r.value() : 0)
                .minTemperature(r.metric().equals(MIN_TEMPERATURE.getMetricName()) ? r.value() : 0)
                .maxTemperature(r.metric().equals(MAX_TEMPERATURE.getMetricName()) ? r.value() : 0)
                .build();
    }
}