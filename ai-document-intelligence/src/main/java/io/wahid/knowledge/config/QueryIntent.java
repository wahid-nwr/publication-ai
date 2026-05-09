package io.wahid.knowledge.config;

public enum QueryIntent {
    GLOBAL_MAX,      // most / highest / max
    GLOBAL_MIN,      // least / lowest / min
    COMPARISON,      // compare A vs B
    DESCRIPTIVE,     // explain / summarize
    UNKNOWN
}
