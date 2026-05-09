package io.wahid.knowledge.service.impl;

import io.wahid.knowledge.rag.AnswerGenerator;
import io.wahid.knowledge.rag.Retriever;
import io.wahid.knowledge.service.QueryService;
import io.wahid.knowledge.service.QuestionRouter;
import io.wahid.knowledge.vectorstore.VectorSearcher;

import java.util.List;

public class DefaultQueryService implements QueryService {

    private final Retriever retriever;
    private final AnswerGenerator answerGenerator;
    private final QuestionRouter questionRouter;

    public DefaultQueryService(
            QuestionRouter questionRouter,
            Retriever retriever,
            AnswerGenerator answerGenerator
    ) {
        this.retriever = retriever;
        this.answerGenerator = answerGenerator;
        this.questionRouter = questionRouter;
    }

    @Override
    public QueryResult query(String question, int topK) throws Exception {

        List<VectorSearcher.SearchResult> retrieved = retriever.retrieve(question, topK);
        System.out.println("---- Retrieved Context ----");
        retrieved.stream().map(VectorSearcher.SearchResult::chunkText).forEach(System.out::println);
        System.out.println("---------------------------");

//        String answer = answerGenerator.generateAnswer(question, retrieved);
        String answer = route(question);

        List<String> sources = retrieved.stream()
                .map(VectorSearcher.SearchResult::documentId)
                .distinct()
                .toList();

        return new QueryResult(answer, sources);
    }

    @Override
    public String route(String question) throws Exception {
        return questionRouter.answer(question);
    }
}

