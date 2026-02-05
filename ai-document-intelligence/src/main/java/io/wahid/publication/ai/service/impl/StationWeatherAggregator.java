package io.wahid.publication.ai.service.impl;

import io.wahid.publication.ai.dto.StationStats;
import io.wahid.publication.ai.dto.StationYearStats;
import io.wahid.publication.ai.dto.WeatherInfo;
import io.wahid.publication.ai.dto.YearlyAccumulator;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.repository.StationSummaryRepository;
import io.wahid.publication.ai.repository.StationYearMetricRepository;
import io.wahid.publication.ai.service.EmbeddingIndexService;
import io.wahid.publication.ai.service.Neo4jSyncService;
import io.wahid.publication.ai.service.WeatherAggregator;
import io.wahid.publication.ai.util.JpaUtil;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationWeatherAggregator implements WeatherAggregator {

    private final Map<String, Map<Integer, YearlyAccumulator>> yearlyData = new HashMap<>();
    private final Map<String, StationStats> statsByStation = new HashMap<>();
    private final StationSummaryRepository stationSummaryRepository;
    private final StationYearMetricRepository stationYearMetricRepository;
    private final EmbeddingIndexService embeddingIndexService;
    private final Neo4jSyncService neo4jSyncService;

    public StationWeatherAggregator(EmbeddingClient embeddingClient, QdrantClient qdrantClient, Neo4jSyncService neo4jSyncService) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        this.stationSummaryRepository = new StationSummaryRepository(emf);
        this.stationYearMetricRepository = new StationYearMetricRepository(emf);
        this.embeddingIndexService = new EmbeddingIndexService(embeddingClient, qdrantClient, stationSummaryRepository);
        this.neo4jSyncService = neo4jSyncService;
    }

    @Override
    public void accept(WeatherInfo info) {
        statsByStation.computeIfAbsent(info.getStation(), s -> new StationStats()).add(info);
        yearlyData
                .computeIfAbsent(info.getStation(), s -> new HashMap<>())
                .computeIfAbsent(info.getYear(), y -> new YearlyAccumulator())
                .accept(info.getWeatherRow());
    }

    @Override
    public void finish() throws Exception {
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

                yearMetrics.add(new StationYearStats(station, year, "avgRainfall", acc.getRainfallSum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, "totalRainfall", acc.getRainfallSum()));
                yearMetrics.add(new StationYearStats(station, year, "avgTemperature", acc.getTemperatureSum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, "avgSunshine", acc.getSunshineSum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, "avgHumidity", acc.getHumiditySum() / acc.getCount()));
                yearMetrics.add(new StationYearStats(station, year, "minTemperature", acc.getMinTemp()));
                yearMetrics.add(new StationYearStats(station, year, "maxTemperature", acc.getMaxTemp()));
            }
        }
        yearMetrics.forEach(stats -> stationYearMetricRepository.save(stats.toYearSummary(stats.getStation(), stats.getYear())));
        neo4jSyncService.sync();
    }
}
