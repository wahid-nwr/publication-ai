package io.wahid.publication.ai.ingestion.impl;


import com.opencsv.CSVReader;
import io.wahid.publication.ai.ingestion.DocumentIngestor;
import io.wahid.publication.ai.dto.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CsvIngestor implements DocumentIngestor {

    @Override
    public void ingest(
            String documentId,
            InputStream inputStream,
            TextChunkConsumer consumer
    ) throws Exception {
        System.out.println("ingesting csv file!");
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] row;
            int rowNumber = 0;

            while ((row = reader.readNext()) != null) {
                rowNumber++;
                System.out.println("ingesting row!");
                String text = String.join(" | ", row);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("row", rowNumber);
                metadata.put("type", "csv");

                consumer.accept(new TextChunk(
                        documentId,
                        text,
                        metadata
                ));
            }
        }
    }
}
