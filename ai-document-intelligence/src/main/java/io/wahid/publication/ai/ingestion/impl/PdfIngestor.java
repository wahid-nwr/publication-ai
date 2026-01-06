package io.wahid.publication.ai.ingestion.impl;

import io.wahid.publication.ai.ingestion.DocumentIngestor;
import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PdfIngestor implements DocumentIngestor {

    @Override
    public void ingest(
            String documentId,
            InputStream inputStream,
            TextChunkConsumer consumer
    ) throws Exception {

        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        ContentHandler handler = new BodyContentHandler(-1); // no char limit
        PDFParser parser = new PDFParser();

        parser.parse(inputStream, handler, metadata, context);

        String content = handler.toString();

        // Simple paragraph-based splitting (safe starting point)
        String[] paragraphs = content.split("\\n\\n+");

        int index = 0;
        for (String paragraph : paragraphs) {
            if (paragraph.isBlank()) continue;

            Map<String, Object> meta = new HashMap<>();
            meta.put("index", ++index);
            meta.put("type", "pdf");

            consumer.accept(new TextChunk(
                    documentId,
                    paragraph.trim(),
                    meta
            ));
        }
    }
}
