package io.wahid.publication.ai.service;

import io.wahid.publication.ai.config.AppConfig;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.repository.StationSummaryRepository;

import java.util.Map;

public class EmbeddingIndexService {

    private final EmbeddingClient embeddingClient;
    private final QdrantClient qdrantClient;
    private final StationSummaryRepository repository;

    public EmbeddingIndexService(
            EmbeddingClient embeddingClient,
            QdrantClient qdrantClient,
            StationSummaryRepository repository
    ) {
        this.embeddingClient = embeddingClient;
        this.qdrantClient = qdrantClient;
        this.repository = repository;
    }

    public void index(StationSummary summary) throws Exception {
        if (summary.getEmbeddingId() != null) {
            return; // already indexed
        }

        float[] vector = embeddingClient.embed(summary.getSummaryText());

        String pointId = summary.getSummaryId().toString();
        qdrantClient.upsert(
                AppConfig.collectionName(),
                pointId,
                vector,
                summaryMetadata(summary)
        );

        repository.updateEmbeddingId(
                summary.getSummaryId(),
                pointId
        );
    }

    private Map<String, Object> summaryMetadata(StationSummary s) {
        return Map.of(
                "station", s.getStation(),
                "startYear", s.getStartYear(),
                "endYear", s.getEndYear(),
                "dataPoints", s.getDataPoints()
        );
    }
}
