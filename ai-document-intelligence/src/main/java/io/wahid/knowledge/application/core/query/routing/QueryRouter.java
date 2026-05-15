package io.wahid.knowledge.application.core.query.routing;

import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.core.query.model.SemanticQuery;
import io.wahid.knowledge.domain.query.Query;

import java.util.Optional;

public class QueryRouter {

//    private final NumericIntentParser numericIntentParser;

//    public QueryRouter(NumericIntentParser numericIntentParser) {
//        this.numericIntentParser = numericIntentParser;
//    }

    public Query route(String question) throws Exception {

//        Optional<NumericQuery> numericQuery = numericIntentParser.parse(question);

//        if (numericQuery.isPresent()) {
//            return numericQuery.get();
//        }

        return new SemanticQuery(question);
    }
}