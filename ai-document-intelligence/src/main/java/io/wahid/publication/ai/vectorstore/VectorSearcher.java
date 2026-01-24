package io.wahid.publication.ai.vectorstore;

import java.util.List;
import java.util.Map;

public interface VectorSearcher {

    List<SearchResult> search(
            float[] queryVector,
            int topK
    );

    record SearchResult(
            String documentId,
            String chunkText,
            Map<String, Object> metadata,
            double score
    ) {}
}
