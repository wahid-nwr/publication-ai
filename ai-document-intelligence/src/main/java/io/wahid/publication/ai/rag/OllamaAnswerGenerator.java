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

        String prompt = build(question, context);
        return ollamaClient.generate(prompt);
    }

    public String build(String question, String context) {
        return """
                You are an assistant answering questions strictly from the provided context.

                Rules:
                - If the answer is not explicitly stated, say: "I don't know based on the provided documents."
                - Do not infer platform purpose unless stated.
                - Be concise and factual.

                Context:
                %s

                Question:
                %s

                Answer:
                """.formatted(context, question);
    }
}
