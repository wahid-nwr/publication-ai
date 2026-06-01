package io.wahid.knowledge.application.core.query.handler.semantic;

import io.wahid.knowledge.application.core.query.model.SemanticResultPayload;
import io.wahid.knowledge.application.core.query.result.ResultFormatter;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

import java.util.stream.Collectors;

public class SemanticResultFormatter
        implements ResultFormatter {

    private final LLMClient llmClient;

    public SemanticResultFormatter(
            LLMClient llmClient
    ) {
        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(QueryResult result) {

        return result.getPayload()
                instanceof SemanticResultPayload;
    }

    @Override
    public String format(QueryResult result)
            throws Exception {

        SemanticResultPayload payload = (SemanticResultPayload) result.getPayload();

        // TODO check objects
        String context = payload.results()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        return llmClient.generate("""
                Answer ONLY using the provided context.

                Context:
                %s
                """.formatted(context));
    }
}