package io.wahid.publication.ai.rag;

import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;
import java.util.stream.Collectors;

public class OllamaAnswerGenerator implements AnswerGenerator {

    private final OllamaLLMClient ollamaClient;

    public OllamaAnswerGenerator(OllamaLLMClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    @Override
    public String generateAnswer(
            String question,
            List<VectorSearcher.SearchResult> sources
    ) {
        String context = sources.stream()
                .limit(5)
                .map(VectorSearcher.SearchResult::chunkText)
                .collect(Collectors.joining("\n\n"));

        String prompt = """
                Answer the question using ONLY the context below.

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

        return ollamaClient.generate(prompt);
    }
}
