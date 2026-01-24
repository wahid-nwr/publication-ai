package io.wahid.publication.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OllamaEmbeddingResponse {

    @JsonProperty("model")
    private String model;

    @JsonProperty("embeddings")
    private List<List<Double>> embeddings;

    public String getModel() {
        return model;
    }

    /**
     * Raw embeddings as List<List<Double>>
     * (exactly how Ollama returns them)
     */
    public List<List<Double>> getEmbeddings() {
        return embeddings;
    }

    /**
     * Convenience method: convert to float[] per vector
     * (Qdrant-friendly)
     */
    public List<float[]> embeddings() {
        return embeddings.stream()
                .map(this::toFloatArray)
                .toList();
    }

    private float[] toFloatArray(List<Double> vector) {
        float[] arr = new float[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            arr[i] = vector.get(i).floatValue();
        }
        return arr;
    }
}
