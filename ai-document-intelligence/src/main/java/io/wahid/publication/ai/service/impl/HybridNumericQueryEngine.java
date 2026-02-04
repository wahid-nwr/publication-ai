package io.wahid.publication.ai.service.impl;

import io.wahid.publication.ai.config.NumericMetric;
import io.wahid.publication.ai.dto.*;
import io.wahid.publication.ai.infra.graph.Neo4jGraphClient;
import io.wahid.publication.ai.infra.ollama.LLMClient;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.repository.StationSummaryRepository;
import io.wahid.publication.ai.service.NumericQueryEngine;
import io.wahid.publication.ai.util.JpaUtil;

import java.util.List;

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

        String metric = getMetric(query);
        return switch (query.getType()) {

            case MAX -> wrap(
                    neoRepo.findTopByMetricDesc(metric)
            );

            case MIN -> wrap(
                    neoRepo.findTopByMetricAsc(metric)
            );

            case TOP_K -> wrap(
                    neoRepo.findTopKByMetricDesc(metric, query.getK())
            );

            case BOTTOM_K -> wrap(
                    neoRepo.findBottomKByMetric(metric, query.getK())
            );

            case COMPARE -> new NumericResult(
                    postgresRepo.findByStationIn(query.getStations())
            );

            case TREND -> handleTrend(query);
        };
    }

    private String getMetric(NumericQuery query) {
        try {
            return NumericMetric.valueOf(query.getMetric()).name();
        } catch (IllegalArgumentException ex) {
            return "";
        }
    }

    private NumericResult wrap(List<GraphResult> graphResults) {

        List<StationSummary> summaries = graphResults.stream()
                .map(this::toStationSummary)
                .toList();

        return new NumericResult(summaries);
    }


    private StationSummary toStationSummary(GraphResult r) {
        return StationSummary.builder()
                .station(r.station())
                .avgRainfall(r.metric().equals("avgRainfall") ? r.value() : 0)
                .totalRainfall(r.metric().equals("totalRainfall") ? r.value() : 0)
                .avgSunshine(r.metric().equals("avgSunshine") ? r.value() : 0)
                .avgHumidity(r.metric().equals("avgHumidity") ? r.value() : 0)
                .avgTemperature(r.metric().equals("avgTemperature") ? r.value() : 0)
                .minTemperature(r.metric().equals("minTemperature") ? r.value() : 0)
                .maxTemperature(r.metric().equals("maxTemperature") ? r.value() : 0)
                // optional: keep metric/value generic
                .build();
    }

    private NumericResult wrap(GraphResult r) {
        return wrap(List.of(r));
    }

    private NumericResult handleTrend(NumericQuery query) {

        List<YearValue> series =
                neoRepo.getMetricTrend(
                        query.getStation(),
                        query.getMetric(),
                        query.getFromYear()
                );

        TrendResult trend = neoRepo.computeTrend(series);

        return llm.explainTrend(trend);
    }
}
