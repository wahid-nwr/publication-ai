package io.wahid.publication.ai;

import io.wahid.publication.ai.embedding.DefaultRetriever;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.embedding.OllamaEmbeddingClient;
import io.wahid.publication.ai.ingestion.DefaultIngestionService;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;
import io.wahid.publication.ai.processing.SlidingWindowChunker;
import io.wahid.publication.ai.processing.impl.DefaultMetadataExtractor;
import io.wahid.publication.ai.processing.impl.DefaultTextNormalizer;
import io.wahid.publication.ai.rag.AnswerGenerator;
import io.wahid.publication.ai.rag.OllamaAnswerGenerator;
import io.wahid.publication.ai.rag.Retriever;
import io.wahid.publication.ai.service.IngestionService;
import io.wahid.publication.ai.service.QueryService;
import io.wahid.publication.ai.service.impl.DefaultQueryService;
import io.wahid.publication.ai.vectorstore.*;

public class ApplicationContext {

    public IngestionService ingestionService() {

        // Final consumer: embedding + vector storage
        TextChunkConsumer embeddingConsumer = createEmbeddingPipeline();

        // Chunker
        SlidingWindowChunker chunker = new SlidingWindowChunker(500, 50);
        chunker.setDownstream(embeddingConsumer);

        // Metadata extractor
        DefaultMetadataExtractor metadataExtractor = new DefaultMetadataExtractor();
        metadataExtractor.setDownstream(chunker);

        // Text normalizer
        DefaultTextNormalizer textNormalizer = new DefaultTextNormalizer();
        textNormalizer.setDownstream(metadataExtractor);

        return new DefaultIngestionService(textNormalizer);
    }

    public QueryService queryService() {
        VectorSearcher vectorSearcher =
                new QdrantVectorSearcher(
                        "http://localhost:6333",
                        "documents"
                );

        OllamaEmbeddingClient ollamaClient =
                new OllamaEmbeddingClient(
                        "http://localhost:11434",
                        "llama3"
                );

        Retriever retriever = new DefaultRetriever(ollamaClient, vectorSearcher);

        AnswerGenerator answerGenerator = new OllamaAnswerGenerator(ollamaClient);

        return new DefaultQueryService(
                retriever,
                answerGenerator
        );
    }

    private TextChunkConsumer createEmbeddingPipeline() {

        EmbeddingClient embeddingClient =
                new OllamaEmbeddingClient(
                        "http://localhost:11434",
                        "all-minilm"
                );

        VectorWriter vectorWriter =
                new QdrantVectorWriter(
                        "http://localhost:6333",
                        "documents"
                );

        return new EmbeddingVectorConsumer(
                embeddingClient,
                vectorWriter
        );
    }
}
