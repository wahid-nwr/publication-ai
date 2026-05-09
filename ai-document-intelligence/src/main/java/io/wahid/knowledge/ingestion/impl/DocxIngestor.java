package io.wahid.knowledge.ingestion.impl;

import io.wahid.knowledge.ingestion.DocumentIngestor;
import io.wahid.knowledge.dto.TextChunk;
import io.wahid.knowledge.ingestion.TextChunkConsumer;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DocxIngestor implements DocumentIngestor {

    @Override
    public void ingest(
            String documentId,
            InputStream inputStream,
            TextChunkConsumer consumer
    ) throws Exception {

        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();

        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(inputStream, handler, metadata);

        String content = handler.toString();
        String[] paragraphs = content.split("\\n\\n+");

        int index = 0;
        for (String paragraph : paragraphs) {
            if (paragraph.isBlank()) continue;

            Map<String, Object> meta = new HashMap<>();
            meta.put("index", ++index);
            meta.put("type", "docx");

            consumer.accept(new TextChunk(
                    documentId,
                    paragraph.trim(),
                    meta
            ));
        }
    }
}
