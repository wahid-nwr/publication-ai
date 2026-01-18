package io.wahid.publication.ai;

import io.wahid.publication.ai.embedding.DefaultRetriever;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.embedding.OllamaEmbeddingClient;
import io.wahid.publication.ai.ingestion.DefaultIngestionService;
import io.wahid.publication.ai.ingestion.PipelineStage;
import io.wahid.publication.ai.processing.SlidingWindowChunker;
import io.wahid.publication.ai.processing.impl.DefaultMetadataExtractor;
import io.wahid.publication.ai.processing.impl.DefaultTextNormalizer;
import io.wahid.publication.ai.rag.AnswerGenerator;
import io.wahid.publication.ai.rag.OllamaAnswerGenerator;
import io.wahid.publication.ai.rag.OllamaLLMClient;
import io.wahid.publication.ai.rag.Retriever;
import io.wahid.publication.ai.service.IngestionService;
import io.wahid.publication.ai.service.QueryService;
import io.wahid.publication.ai.service.impl.DefaultQueryService;
import io.wahid.publication.ai.vectorstore.*;

import java.io.IOException;

public class ApplicationContext {

    public IngestionService ingestionService() throws IOException, InterruptedException {
        ensureQdrantCollection();

        // Final consumer: embedding + vector storage
        PipelineStage embeddingStage = createEmbeddingPipeline();

        // Chunker
        SlidingWindowChunker chunker = new SlidingWindowChunker(500, 50);
        chunker.setDownstream(embeddingStage);

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
                        "http://qdrant:6333",
                        "documents"
                );

        OllamaEmbeddingClient embeddingClient =
                new OllamaEmbeddingClient(
                        "http://ollama:11434",
                        "nomic-embed-text"
                );

        OllamaLLMClient llmClient =
                new OllamaLLMClient(
                        "http://ollama:11434",
                        "llama3"   // 🔥 GENERATION MODEL
                );

        Retriever retriever = new DefaultRetriever(embeddingClient, vectorSearcher);

        AnswerGenerator answerGenerator = new OllamaAnswerGenerator(llmClient);

        return new DefaultQueryService(
                retriever,
                answerGenerator
        );
    }

    private PipelineStage createEmbeddingPipeline() throws IOException, InterruptedException {

        EmbeddingClient embeddingClient =
                new OllamaEmbeddingClient(
                        "http://ollama:11434",
                        "nomic-embed-text"
                );

        VectorWriter vectorWriter =
                new QdrantVectorWriter(
                        "http://qdrant:6333",
                        "documents"
                );

        validateEmbeddingDimension(embeddingClient, vectorWriter);
        return new EmbeddingVectorConsumer(
                embeddingClient,
                vectorWriter
        );
    }

    private void validateEmbeddingDimension(EmbeddingClient embeddingClient, VectorWriter vectorWriter) throws IOException, InterruptedException {
        int modelDim = embeddingClient.dimension();
        int collectionDim = vectorWriter.requiredVectorSize();

        if (modelDim != collectionDim) {
            throw new IllegalStateException(
                    "Embedding dimension mismatch: model=" + modelDim +
                            ", Qdrant collection=" + collectionDim
            );
        }
    }

    private void ensureQdrantCollection() {
        QdrantAdminClient admin = new QdrantAdminClient("http://qdrant:6333");

        admin.ensureCollection(
                "documents",
                768,          // embedding dimension
                "Cosine"
        );
    }
}
