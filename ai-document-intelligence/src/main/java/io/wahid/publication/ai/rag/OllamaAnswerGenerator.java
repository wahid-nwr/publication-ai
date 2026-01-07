package io.wahid.publication.ai.rag;

import io.wahid.publication.ai.embedding.OllamaEmbeddingClient;
import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;
import java.util.stream.Collectors;

public class OllamaAnswerGenerator implements AnswerGenerator {

    private final OllamaEmbeddingClient ollamaClient;

    public OllamaAnswerGenerator(OllamaEmbeddingClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    @Override
    public String generateAnswer(
            String question,
            List<VectorSearcher.SearchResult> context
    ) {
        String combinedContext = context.stream()
                .map(VectorSearcher.SearchResult::chunkText)
                .collect(Collectors.joining("\n\n"));

        return ollamaClient.answer(question, combinedContext);
    }
}
