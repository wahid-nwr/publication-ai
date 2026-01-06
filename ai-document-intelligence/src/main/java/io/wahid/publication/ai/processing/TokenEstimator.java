package io.wahid.publication.ai.processing;

public final class TokenEstimator {

    private TokenEstimator() {}

    public static int estimateTokens(String text) {
        if (text == null || text.isBlank()) return 0;

        // Rough heuristic: words ~= tokens
        return text.split("\\s+").length;
    }
}
