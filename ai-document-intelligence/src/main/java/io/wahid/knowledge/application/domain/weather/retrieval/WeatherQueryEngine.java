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
import io.wahid.knowledge.repository.StationSummaryRepository;
import io.wahid.knowledge.util.JpaUtil;

import java.util.List;
import java.util.stream.Stream;

public class WeatherQueryEngine implements NumericQueryEngine {

    private final Neo4jWeatherRepository neoRepo;
    private final StationSummaryRepository postgresRepo;

    public WeatherQueryEngine(Neo4jGraphClient neo4jGraphClient) {
        this.neoRepo = new Neo4jWeatherRepository(neo4jGraphClient);
        this.postgresRepo = new StationSummaryRepository(JpaUtil.getEntityManagerFactory());
    }

    @Override
    public NumericResultPayload execute(NumericQuery query) {
        String metric = query.getMetric().getMetricName();
        return switch (query.getNumericType()) {
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
        return new NumericResultPayload<>(query.getNumericType(), query.getMetric(), List.of(), trend, null, 3);
    }

    private NumericResultPayload value(NumericQuery query) {
        GraphResult metricValue = neoRepo.getMetricValue(query.getStation(), query.getMetric().getMetricName());
        return new NumericResultPayload<>(query.getNumericType(), query.getMetric(), Stream.of(metricValue).map(StationSummaryMapper::map).toList(), null, metricValue, 1);
    }

    private NumericResultPayload compare(NumericQuery query) {
        List<StationSummary> summaries = postgresRepo.findByStationIn(query.getStations());
        return new NumericResultPayload<>(query.getNumericType(), query.getMetric(), summaries, null, null, summaries.size());
    }

    private NumericResultPayload wrap(NumericQuery query, List<GraphResult> graphResults) {
        List<StationSummary> summaries = graphResults.stream().map(StationSummaryMapper::map).toList();
        return new NumericResultPayload<>(query.getNumericType(), query.getMetric(), summaries, null, null, summaries.size());
    }

    private NumericResultPayload wrap(NumericQuery query, GraphResult r) {
        return wrap(query, List.of(r));
    }
}
