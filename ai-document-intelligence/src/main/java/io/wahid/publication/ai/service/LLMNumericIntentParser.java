package io.wahid.publication.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.publication.ai.config.NumericQueryType;
import io.wahid.publication.ai.dto.NumericQuery;
import io.wahid.publication.ai.infra.ollama.LLMClient;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LLMNumericIntentParser {

    private static final Logger LOGGER = Logger.getLogger(LLMNumericIntentParser.class.getName());
    private final LLMClient llm;
    private final ObjectMapper mapper = new ObjectMapper();

    public LLMNumericIntentParser(LLMClient llm) {
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
                            .type(nmQuerytype)
                            .metric(metric)
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

    /*private String buildPrompt(String question) {
        return """
                You are an intent extraction engine.
                Return ONLY valid JSON.

                Schema:
                {
                  "numeric": boolean,
                  "type": "MAX | MIN | TOP_K | BOTTOM_K | TREND | NONE",
                  "metric": "totalRainfall | avgRainfall | avgSunshine | avgTemperature | NONE",
                  "k": number,
                  "station": string | null,
                  "fromYear": number | null
                }

                Question:
                "%s"
                """.formatted(question);
    }*/
    private String buildPrompt(String question) {
        return """
        You are an intent extraction engine.
        Return ONLY valid JSON. No explanation.

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
          "numeric": boolean,
          "type": "MAX | MIN | TOP_K | BOTTOM_K | TREND | NONE",
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
