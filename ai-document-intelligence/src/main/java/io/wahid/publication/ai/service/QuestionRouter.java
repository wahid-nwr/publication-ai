package io.wahid.publication.ai.service;

import io.wahid.publication.ai.config.QueryIntent;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.ollama.LLMClient;
import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.repository.StationSummaryRepository;
import io.wahid.publication.ai.util.JpaUtil;
import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionRouter {

    private final StationSummaryRepository repository;
    private final VectorSearcher vectorSearcher;
    private final EmbeddingClient embeddingClient;
    private final LLMClient llmClient;

    public QuestionRouter(VectorSearcher qdrantSearcher, EmbeddingClient embeddingClient, LLMClient llmClient) {
        this.vectorSearcher = qdrantSearcher;
        this.embeddingClient = embeddingClient;
        this.llmClient = llmClient;
        this.repository = new StationSummaryRepository(JpaUtil.getEntityManagerFactory());
    }

    public String answer(String question) throws Exception {

        QueryIntent intent = IntentClassifier.classify(question);

        return switch (intent) {
            case GLOBAL_MAX -> handleGlobalMax(question);
            case GLOBAL_MIN -> handleGlobalMin(question);
            case COMPARISON -> handleComparison(question);
            case DESCRIPTIVE, UNKNOWN -> handleDescriptive(question);
        };
    }

    private String handleDescriptive(String question) throws Exception {

        float[] queryVector = embeddingClient.embed(question);

        List<VectorSearcher.SearchResult> results =
                vectorSearcher.search(queryVector, 3);

        String context = results.stream()
                .map(VectorSearcher.SearchResult::chunkText)
                .collect(Collectors.joining("\n"));

        return llmClient.generate("""
                Answer the question using ONLY the context below.
                If the answer is not present, say you don't know.

                Context:
                %s
                """.formatted(context));
    }

    private String handleGlobalMax(String question) throws Exception {

        StationSummary max = repository.findTopByOrderByTotalRainfallDesc();

        return llmClient.generate(
                "Answer in one sentence: " +
                        "The station with the highest total rainfall is " +
                        max.getStation() +
                        " with " + max.getTotalRainfall() + " mm."
        );
    }

    private String handleGlobalMin(String question) throws Exception {

        StationSummary max = repository.findTopByOrderByTotalRainfallDesc();

        return llmClient.generate(
                "Answer in one sentence: " +
                        "The station with the lowest total rainfall is " +
                        max.getStation() +
                        " with " + max.getTotalRainfall() + " mm."
        );
    }

    private String handleComparison(String question) throws Exception {

        List<String> stations = extractStations(question);

        if (stations.size() < 2) {
            return "Please specify at least two stations to compare.";
        }

        List<StationSummary> summaries = repository.findByStationIn(stations);

        if (summaries.size() < 2) {
            return "Insufficient data to perform the comparison.";
        }

        String context = summaries.stream()
                .map(this::formatFacts)
                .collect(Collectors.joining("\n"));

        return llmClient.generate("""
                Compare the following weather statistics.
                Do not infer or assume missing values.
                Use only the provided data.

                %s
                """.formatted(context));
    }

    private String formatFacts(StationSummary s) {
        return """
                Station: %s
                Total Rainfall: %.1f mm
                Average Rainfall: %.2f mm
                Average Sunshine: %.2f hours
                Average Temperature: %.2f °C
                """.formatted(
                s.getStation(),
                s.getTotalRainfall(),
                s.getAvgRainfall(),
                s.getAvgSunshine(),
                s.getAvgTemperature()
        );
    }

    private List<String> extractStations(String question) {
        String q = question.toLowerCase();

        return repository.findAllStations().stream()
                .filter(station -> q.contains(station.toLowerCase()))
                .toList();
    }
}
