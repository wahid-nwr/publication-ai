package io.wahid.knowledge.application.domain.weather.query.routing;

import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.handler.QueryHandlerRegistry;
import io.wahid.knowledge.application.core.query.model.SemanticQuery;
import io.wahid.knowledge.application.core.query.routing.QueryRouter;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherQuestionRouter implements QueryRouter {

    private static final Logger LOGGER = Logger.getLogger(WeatherQuestionRouter.class.getName());
    private final NumericIntentParser numericIntentParser;
    private final QueryHandlerRegistry registry;
    private final QueryHandler<SemanticQuery> semanticHandler;

    public WeatherQuestionRouter(NumericIntentParser numericIntentParser,
                                 QueryHandlerRegistry registry, QueryHandler<SemanticQuery> semanticHandler) {
        super();
        this.registry = registry;
        this.semanticHandler = semanticHandler;
        this.numericIntentParser = numericIntentParser;
    }

    @Override
    public QueryResult route(Query request) throws Exception {

        Optional<NumericQuery> numericQuery = numericIntentParser.parse(request.getText());

        if (numericQuery.isPresent()) {
            LOGGER.log(Level.INFO, "Numeric query resolve, {0}", numericQuery);
            return registry.resolve(numericQuery.get()).handle(numericQuery.get());
        }
        SemanticQuery query = new SemanticQuery(request);
        LOGGER.log(Level.INFO, "Semantic handle, {0}", query);
        return semanticHandler.handle(query);
    }
}
