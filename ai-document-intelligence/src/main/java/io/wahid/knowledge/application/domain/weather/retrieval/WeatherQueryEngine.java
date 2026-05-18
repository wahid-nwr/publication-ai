package io.wahid.knowledge.application.domain.weather.retrieval;

import io.wahid.knowledge.application.core.query.dto.GraphResult;
import io.wahid.knowledge.application.core.query.dto.TrendResult;
import io.wahid.knowledge.application.core.query.mapper.StationSummaryMapper;
import io.wahid.knowledge.application.core.query.result.NumericResultPayload;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.domain.weather.model.YearValue;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.infrastructure.graph.neo4j.Neo4jGraphClient;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.impl.Neo4jWeatherRepository;
import io.wahid.knowledge.model.StationSummary;

import java.util.List;

public class WeatherQueryEngine implements NumericQueryEngine {

    private final Neo4jWeatherRepository neoRepo;

    public WeatherQueryEngine(Neo4jGraphClient neo4jGraphClient) {
        this.neoRepo = new Neo4jWeatherRepository(neo4jGraphClient);
    }

    @Override
    public NumericResultPayload execute(NumericQuery query) {
        String metric = query.getMetric().getMetricName();
        System.out.println("query.getNemericType()->" + query.getNemericType());
        return switch (query.getNemericType()) {
            case MAX -> wrap(query, neoRepo.findTopByMetricDesc(metric));
            case MIN -> wrap(query, neoRepo.findTopByMetricAsc(metric));
            case TOP_K -> wrap(query, neoRepo.findTopKByMetricDesc(metric, query.getK()));
            case BOTTOM_K -> wrap(query, neoRepo.findBottomKByMetric(metric, query.getK()));
            case COMPARE -> compare(query);
            case VALUE -> value(query);
            case TREND -> trend(query);
        };
    }

    private NumericResultPayload trend(NumericQuery query) {
        List<YearValue> series = neoRepo.getMetricTrend(query.getStation(), query.getMetric().getMetricName(), query.getFromYear());
        TrendResult trend = neoRepo.computeTrend(query.getMetric(), series, query.getStation());
        return new NumericResultPayload(query.getNemericType(), query.getMetric(), List.of(), trend, null, 3);
    }

    private NumericResultPayload value(NumericQuery query) {
        throw new UnsupportedOperationException(
                "Not implemented yet"
        );
    }

    private NumericResultPayload compare(NumericQuery query) {
        throw new UnsupportedOperationException(
                "Not implemented yet"
        );
    }

    private NumericResultPayload wrap(NumericQuery query, List<GraphResult> graphResults) {
        List<StationSummary> summaries = graphResults.stream().map(StationSummaryMapper::map).toList();
        return new NumericResultPayload(query.getNemericType(), query.getMetric(), summaries, null, null, summaries.size());
    }

    private NumericResultPayload wrap(NumericQuery query, GraphResult r) {
        return wrap(query, List.of(r));
    }

//    @Override
//    public NumericResult execute(NumericQuery query) {
//
//        String metric = query.getMetric().getMetricName();
//        return switch (query.getNemericType()) {
//
//            case MAX -> wrap(
//                    neoRepo.findTopByMetricDesc(metric)
//            );
//
//            case MIN -> wrap(
//                    neoRepo.findTopByMetricAsc(metric)
//            );
//
//            case TOP_K -> wrap(
//                    neoRepo.findTopKByMetricDesc(metric, query.getK()), List.of()
//            );
//
//            case BOTTOM_K -> wrap(
//                    neoRepo.findBottomKByMetric(metric, query.getK()), List.of()
//            );
//
//            case COMPARE -> new NumericResult(
//                    postgresRepo.findByStationIn(query.getStations()), List.of()
//            );
//
//            case VALUE -> handleValue(query);
//
//            case TREND -> handleTrend(query);
//        };
//    }

//    private StationSummary toStationSummary(GraphResult r) {
//        return StationSummary.builder()
//                .station(r.station())
//                .avgRainfall(r.metric().equals(AVG_RAINFALL.getMetricName()) ? r.value() : 0)
//                .totalRainfall(r.metric().equals(TOTAL_RAINFALL.getMetricName()) ? r.value() : 0)
//                .avgSunshine(r.metric().equals(AVG_SUNSHINE.getMetricName()) ? r.value() : 0)
//                .avgHumidity(r.metric().equals(AVG_HUMIDITY.getMetricName()) ? r.value() : 0)
//                .avgTemperature(r.metric().equals(AVG_TEMPERATURE.getMetricName()) ? r.value() : 0)
//                .minTemperature(r.metric().equals(MIN_TEMPERATURE.getMetricName()) ? r.value() : 0)
//                .maxTemperature(r.metric().equals(MAX_TEMPERATURE.getMetricName()) ? r.value() : 0)
//                .build();
//    }

//    private NumericResult handleValue(NumericQuery query) {
//        GraphResult metricValue = neoRepo.getMetricValue(query.getStation(), query.getMetric().getMetricName());
//        System.out.println("metric value -> " + metricValue);
//
//        try {
//            return llm.explainValue(query, metricValue);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private NumericResult handleTrend(NumericQuery query) {
//
//        List<YearValue> series =
//                neoRepo.getMetricTrend(
//                        query.getStation(),
//                        query.getMetric().getMetricName(),
//                        query.getFromYear()
//                );
//        TrendResult trend = neoRepo.computeTrend(query.getMetric(), series, query.getStation());
//        System.out.println("trend-> " + trend);
//        try {
//            return llm.explainTrend(trend);
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
