package io.wahid.knowledge.ingestion;

import io.wahid.knowledge.ingestion.impl.CsvIngestor;
import io.wahid.knowledge.ingestion.impl.DocxIngestor;
import io.wahid.knowledge.ingestion.impl.MarkdownIngestor;
import io.wahid.knowledge.ingestion.impl.PdfIngestor;

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
