package io.wahid.publication.ai.service;

import io.wahid.publication.ai.config.QueryIntent;

public class IntentClassifier {

    private IntentClassifier() {
    }

    public static QueryIntent classify(String question) {
        String q = question.toLowerCase();

        if (q.contains("most") || q.contains("highest") || q.contains("maximum")) {
            return QueryIntent.GLOBAL_MAX;
        }

        if (q.contains("least") || q.contains("lowest") || q.contains("minimum")) {
            return QueryIntent.GLOBAL_MIN;
        }

        if (q.contains("compare") || q.contains("vs")) {
            return QueryIntent.COMPARISON;
        }

        if (q.contains("describe") || q.contains("summary") || q.contains("explain")) {
            return QueryIntent.DESCRIPTIVE;
        }

        return QueryIntent.UNKNOWN;
    }
}
