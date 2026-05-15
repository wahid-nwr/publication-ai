package io.wahid.knowledge.application.core.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.knowledge.application.domain.weather.query.model.NumericMetric;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NumericIntentParser {

    private static final Logger LOGGER = Logger.getLogger(NumericIntentParser.class.getName());
    private final LLMClient llm;
    private final ObjectMapper mapper = new ObjectMapper();

    public NumericIntentParser(LLMClient llm) {
        this.llm = llm;
    }

    public Optional<NumericQuery> parse(String question) throws Exception {
        LOGGER.log(Level.INFO, "numeric intent prompt -> {0}", buildPrompt(question));
        String response = llm.generate(buildPrompt(question));
        response = cleanJson(response);
        LOGGER.log(Level.INFO, "numeric intent llm response -> {0}", response);
        try {
            JsonNode root = mapper.readTree(response);
            boolean numeric = root.path("numeric").asBoolean(false);
            String type = root.path("type").asText("NONE");
            String metric = root.path("metric").asText("NONE");

            if (!numeric && !type.equals("NONE") && !metric.equals("NONE")) {
                numeric = true; // promote to numeric intent
            }

            LOGGER.log(Level.INFO, "is numeric intent -> {0}", numeric);
            if (!numeric) {
                return Optional.empty();
            }

            NumericQueryType nmQuerytype = NumericQueryType.valueOf(type.toUpperCase());

            return Optional.of(
                    new NumericQuery.Builder()
                            .numeric(root.path("numeric").asBoolean(numeric))
                            .timeBased(root.path("timeBased").asBoolean(false))
                            .value(root.path("value").asDouble(0))
                            .type(nmQuerytype)
                            .metric(NumericMetric.getMetricByName(metric))
                            .k(root.path("k").asInt(1))
                            .station(asNullable(root, "station"))
                            .fromYear(asNullableInt(root, "fromYear"))
                            .build()
            );

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String cleanJson(String response) {
        response = response.trim();

        if (response.startsWith("```")) {
            response = response
                    .replaceAll("^```json\\s*", "")
                    .replaceAll("^```\\s*", "")
                    .replaceAll("\\s*```$", "");
        }

        return response.trim();
    }

    private String buildPrompt(String question) {
        return """
        You are an intent extraction engine.
        Return ONLY valid JSON. No explanation.

        If the question mentions:
        - a metric AND
        - a station AND
        - a starting year (e.g. "since 1980", "from 1990")
        Then:
        - numeric = true
        - timeBased = true
        - type = TREND
        Even if words like "increase" or "decrease" are NOT present.
        
        If the question asks:
        “what is”
        “how much”
        “how many”
        AND includes
        a metric
        a station
        AND does NOT ask for comparison
        → classify as VALUE
        
        A query is NUMERIC if it involves:
        - comparison (min, max, least, highest, lowest)
        - aggregation (average, total, trend)
        - ranking (top, bottom, most, fewest)
        EVEN IF NO NUMBER IS PRESENT.

        Examples:
        - "least average rainfall" → numeric=true, type=MIN, metric=avgRainfall
        - "highest temperature" → numeric=true, type=MAX, metric=avgTemperature
        - "top 3 wettest stations" → numeric=true, type=TOP_K, metric=totalRainfall, k=3

        Schema:
        {
          "timeBased": boolean,
          "numeric": boolean,
          "type": "MAX | MIN | TOP_K | BOTTOM_K | TREND | VALUE | NONE",
          "metric": "totalRainfall | avgRainfall | avgSunshine | avgTemperature | NONE",
          "k": number,
          "station": string | null,
          "fromYear": number | null
        }

        Question:
        "%s"
        """.formatted(question);
    }


    private String asNullable(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText()
                : null;
    }

    private Integer asNullableInt(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asInt()
                : null;
    }
}
