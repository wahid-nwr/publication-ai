package io.wahid.knowledge.application.retrieval.impl;

import io.wahid.knowledge.application.query.dto.GraphResult;
import io.wahid.knowledge.application.query.dto.NumericQuery;
import io.wahid.knowledge.application.query.dto.NumericResult;
import io.wahid.knowledge.application.query.dto.TrendResult;
import io.wahid.knowledge.application.ingestion.processing.dto.YearValue;
import io.wahid.knowledge.infrastructure.graph.neo4j.Neo4jGraphClient;
import io.wahid.knowledge.infrastructure.llms.LLMClient;
import io.wahid.knowledge.model.StationSummary;
import io.wahid.knowledge.model.StationYearMetric;
import io.wahid.knowledge.repository.StationSummaryRepository;
import io.wahid.knowledge.application.retrieval.NumericQueryEngine;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.impl.Neo4jWeatherRepository;
import io.wahid.knowledge.util.JpaUtil;

import java.util.List;

import static io.wahid.knowledge.application.config.NumericMetric.*;

public class HybridNumericQueryEngine implements NumericQueryEngine {

    private final StationSummaryRepository postgresRepo;
    private final Neo4jWeatherRepository neoRepo;
    private final LLMClient llm;

    public HybridNumericQueryEngine(
            Neo4jGraphClient neo4jGraphClient,
            LLMClient llm
    ) {
        this.postgresRepo = new StationSummaryRepository(JpaUtil.getEntityManagerFactory());
        this.neoRepo = new Neo4jWeatherRepository(neo4jGraphClient);
        this.llm = llm;
    }

    @Override
    public NumericResult execute(NumericQuery query) {

        String metric = query.getMetric().getMetricName();
        return switch (query.getType()) {

            case MAX -> wrap(
                    neoRepo.findTopByMetricDesc(metric)
            );

            case MIN -> wrap(
                    neoRepo.findTopByMetricAsc(metric)
            );

            case TOP_K -> wrap(
                    neoRepo.findTopKByMetricDesc(metric, query.getK()), List.of()
            );

            case BOTTOM_K -> wrap(
                    neoRepo.findBottomKByMetric(metric, query.getK()), List.of()
            );

            case COMPARE -> new NumericResult(
                    postgresRepo.findByStationIn(query.getStations()), List.of()
            );

            case VALUE -> handleValue(query);

            case TREND -> handleTrend(query);
        };
    }

    private NumericResult wrap(List<GraphResult> graphResults, List<TrendResult> trendResults) {

        List<StationSummary> summaries = graphResults.stream()
                .map(this::toStationSummary)
                .toList();
        List<StationYearMetric> yearMetrics = List.of();
        return new NumericResult(summaries, yearMetrics);
    }


    private StationSummary toStationSummary(GraphResult r) {
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

    private NumericResult wrap(GraphResult r) {
        return wrap(List.of(r), List.of());
    }

    private NumericResult handleValue(NumericQuery query) {
        GraphResult metricValue = neoRepo.getMetricValue(query.getStation(), query.getMetric().getMetricName());
        System.out.println("metric value -> " + metricValue);

        try {
            return llm.explainValue(query, metricValue);
        } catch (Exception e) {
            return null;
        }
    }

    private NumericResult handleTrend(NumericQuery query) {

        List<YearValue> series =
                neoRepo.getMetricTrend(
                        query.getStation(),
                        query.getMetric().getMetricName(),
                        query.getFromYear()
                );
        TrendResult trend = neoRepo.computeTrend(query.getMetric(), series, query.getStation());
        System.out.println("trend-> " + trend);
        try {
            return llm.explainTrend(trend);
        } catch (Exception e) {
            return null;
        }
    }
}
