package io.wahid.publication.ai.ingestion;

import io.wahid.publication.ai.ingestion.impl.CsvIngestor;
import io.wahid.publication.ai.ingestion.impl.DocxIngestor;
import io.wahid.publication.ai.ingestion.impl.MarkdownIngestor;
import io.wahid.publication.ai.ingestion.impl.PdfIngestor;

import java.util.Map;

public class IngestorRegistry {

    private final Map<String, DocumentIngestor> ingestors = Map.of(
            "csv", new CsvIngestor(),
            "pdf", new PdfIngestor(),
            "docx", new DocxIngestor(),
            "md", new MarkdownIngestor(),
            "markdown", new MarkdownIngestor()
    );

    public DocumentIngestor get(String type) {
        DocumentIngestor ingestor = ingestors.get(type.toLowerCase());
        if (ingestor == null) {
            throw new IllegalArgumentException("Unsupported document type: " + type);
        }
        return ingestor;
    }
}
