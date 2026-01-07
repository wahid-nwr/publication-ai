package io.wahid.publication.ai.rag;

import java.util.List;

public class PromptBuilder {

    public String build(
            String question,
            String context
    ) {
        return """
                You are an expert assistant.
                Answer the question using ONLY the context below.
                If the answer is not in the context, say "I don't know".

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);
    }
}
