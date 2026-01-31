package io.wahid.publication.ai.service;

import io.wahid.publication.ai.model.StationSummary;

public interface StationSummaryFormatter {
    String format(StationSummary summary);
}
