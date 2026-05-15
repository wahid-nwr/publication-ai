package io.wahid.knowledge.application.domain.weather.insights.impl;

import io.wahid.knowledge.application.domain.weather.insights.weather.WeatherAggregator;
import io.wahid.knowledge.infrastructure.config.AppConfig;
import io.wahid.knowledge.application.domain.weather.model.StationStats;
import io.wahid.knowledge.application.domain.weather.model.StationYearStats;
import io.wahid.knowledge.application.domain.weather.model.DailyWeatherMeasurement;
import io.wahid.knowledge.infrastructure.embedding.EmbeddingIndexService;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.impl.Neo4jSyncService;
import io.wahid.knowledge.application.domain.weather.model.YearlyAccumulator;
import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantClient;
import io.wahid.knowledge.model.StationSummary;
import io.wahid.knowledge.repository.StationSummaryRepository;
import io.wahid.knowledge.repository.StationYearMetricRepository;
import io.wahid.knowledge.util.JpaUtil;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.wahid.knowledge.application.core.query.handler.numeric.NumericMetric.*;

public class StationWeatherAggregator implements WeatherAggregator {

    private final Map<String, Map<Integer, YearlyAccumulator>> yearlyData = new HashMap<>();
    private final Map<String, StationStats> statsByStation = new HashMap<>();
    private final StationSummaryRepository stationSummaryRepository;
    private final StationYearMetricRepository stationYearMetricRepository;
    private final EmbeddingIndexService embeddingIndexService;
    private final Neo4jSyncService neo4jSyncService;
    private final QdrantClient qdrantClient;

    public StationWeatherAggregator(EmbeddingClient embeddingClient, QdrantClient qdrantClient, Neo4jSyncService neo4jSyncService) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        this.stationSummaryRepository = new StationSummaryRepository(emf);
        this.stationYearMetricRepository = new StationYearMetricRepository(emf);
        this.qdrantClient = qdrantClient;
        this.embeddingIndexService = new EmbeddingIndexService(embeddingClient, qdrantClient, stationSummaryRepository);
        this.neo4jSyncService = neo4jSyncService;
    }

    @Override
    public void accept(DailyWeatherMeasurement info) {
        statsByStation.computeIfAbsent(info.getStation(), s -> new StationStats()).add(info);
        yearlyData
                .computeIfAbsent(info.getStation(), s -> new HashMap<>())
                .computeIfAbsent(info.getYear(), y -> new YearlyAccumulator())
                .accept(info.getWeatherRow());
    }

    @Override
    public void finish() throws Exception {
        // sanitize
        stationSummaryRepository.removeAll();
        stationYearMetricRepository.removeAll();
        neo4jSyncService.removeAll();
        qdrantClient.deleteAllPoints(AppConfig.collectionName());

        for (Map.Entry<String, StationStats> entry : statsByStation.entrySet()) {
            StationSummary summary = entry.getValue().toSummary(entry.getKey());

            // 👇 persist + embed later
            summary = stationSummaryRepository.save(summary);
            embeddingIndexService.index(summary);
        }
        List<StationYearStats> yearMetrics = new ArrayList<>();
        for (var stationEntry : yearlyData.entrySet()) {
            String station = stationEntry.getKey();

            for (var yearEntry : stationEntry.getValue().entrySet()) {
                int year = yearEntry.getKey();
                YearlyAccumulator acc = yearEntry.getValue();

                yearMetrics.add(new StationYearStats(station, year, AVG_RAINFALL.getMetricName(), acc.getRainfallSum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, TOTAL_RAINFALL.getMetricName(), acc.getRainfallSum()));
                yearMetrics.add(new StationYearStats(station, year, AVG_TEMPERATURE.getMetricName(), acc.getTemperatureSum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, AVG_SUNSHINE.getMetricName(), acc.getSunshineSum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, AVG_HUMIDITY.getMetricName(), acc.getHumiditySum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, MIN_TEMPERATURE.getMetricName(), acc.getMinTemp()));
                yearMetrics.add(new StationYearStats(station, year, MAX_TEMPERATURE.getMetricName(), acc.getMaxTemp()));
            }
        }
        yearMetrics.forEach(stats -> stationYearMetricRepository.save(stats.toYearSummary(stats.getStation(), stats.getYear())));
        neo4jSyncService.sync();
    }
}
