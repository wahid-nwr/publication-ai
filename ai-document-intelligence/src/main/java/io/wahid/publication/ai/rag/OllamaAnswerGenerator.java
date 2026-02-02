package io.wahid.publication.ai.rag;

import io.wahid.publication.ai.infra.ollama.LLMClient;
import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;
import java.util.stream.Collectors;

public class OllamaAnswerGenerator implements AnswerGenerator {

    private final LLMClient llmClient;

    public OllamaAnswerGenerator(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public String generateAnswer(
            String question,
            List<VectorSearcher.SearchResult> sources
    ) throws Exception {
        String context = sources.stream()
                .limit(5)
                .map(VectorSearcher.SearchResult::chunkText)
                .collect(Collectors.joining("\n\n"));

        String prompt = build(question, context);
        return llmClient.generate(prompt);
    }

    public String build(String question, String context) {
        /*
        You are an assistant answering questions strictly from the provided context.

                Rules:
                - If the answer is not explicitly stated, say: "I don't know based on the provided documents."
                - Do not infer platform purpose unless stated.
                - Be concise and factual.

         */
        return """
                Answer using only the provided context.
                If multiple stations are present, compare them and choose the maximum.
                If rainfall data is missing, say so explicitly.

                Context:
                %s

                Question:
                %s

                Answer:
                """.formatted(context, question);
    }
}
