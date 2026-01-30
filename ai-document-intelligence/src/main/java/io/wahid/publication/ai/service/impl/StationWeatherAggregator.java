package io.wahid.publication.ai.service.impl;

import io.wahid.publication.ai.dto.StationStats;
import io.wahid.publication.ai.dto.WeatherInfo;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.repository.StationSummaryRepository;
import io.wahid.publication.ai.service.EmbeddingIndexService;
import io.wahid.publication.ai.service.WeatherAggregator;
import io.wahid.publication.ai.util.JpaUtil;

import java.util.HashMap;
import java.util.Map;

public class StationWeatherAggregator implements WeatherAggregator {

    private final Map<String, StationStats> statsByStation = new HashMap<>();
    private StationSummaryRepository stationSummaryRepository;
    private EmbeddingIndexService embeddingIndexService;

    public StationWeatherAggregator(EmbeddingClient embeddingClient, QdrantClient qdrantClient) {
        this.stationSummaryRepository = new StationSummaryRepository(JpaUtil.getEntityManagerFactory());
        this.embeddingIndexService = new EmbeddingIndexService(embeddingClient, qdrantClient, stationSummaryRepository);
    }

    @Override
    public void accept(WeatherInfo info) {
        statsByStation.computeIfAbsent(info.getStation(), s -> new StationStats()).add(info);
    }

    @Override
    public void finish() throws Exception {
        for (Map.Entry<String, StationStats> entry : statsByStation.entrySet()) {
            StationSummary summary = entry.getValue().toSummary(entry.getKey());

            // 👇 persist + embed later
            stationSummaryRepository.save(summary);
            embeddingIndexService.index(summary);
        }
    }
}
