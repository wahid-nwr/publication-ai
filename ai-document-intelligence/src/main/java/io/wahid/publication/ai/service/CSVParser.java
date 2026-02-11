package io.wahid.publication.ai.service;

import com.opencsv.bean.CsvToBeanBuilder;
import io.wahid.publication.ai.dto.WeatherInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class CSVParser {
    private static final Logger LOGGER = Logger.getLogger(CSVParser.class.getName());
    private static final char COLUMN_SEPARATOR = ',';
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int BATCH_SIZE = 500;
    private final WeatherAggregator aggregator;

    public CSVParser(WeatherAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public void parse(InputStream in, String objectKey) throws Exception {
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.ISO_8859_1);
        int total = 0;

        try (BufferedReader reader = new BufferedReader(isr)) {
            LOGGER.info("downloaded file from r2");
            if (Objects.isNull(reader.readLine())) {
                throw new IOException(String.format("File does not contain header %s", objectKey));
            }

            for (List<String> chunk : chunkBufferedReader(reader)) {
                try (BufferedReader chunkReader = new BufferedReader(new StringReader(String.join(LINE_SEPARATOR, chunk)))) {
                    CsvToBeanBuilder<WeatherInfo> csvToBean = getCsvToBeanBuilder(WeatherInfo.class, chunkReader);
                    csvToBean.build().stream().forEach(aggregator::accept);
                }
                total += chunk.size();
            }
            System.out.println("total from csv " + objectKey + ":" + total);
            /*new CsvToBeanBuilder<WeatherInfo>(reader)
                    .withType(WeatherInfo.class)
                    .withSeparator(COLUMN_SEPARATOR)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withVerifyReader(true)
                    .withThrowExceptions(true)
                    .withSkipLines(1)
                    .build()
                    .stream()
                    .forEach(aggregator::accept);*/

            aggregator.finish();
        }
    }

    private static <P> CsvToBeanBuilder<P> getCsvToBeanBuilder(Class<P> type, BufferedReader reader) {
        return new CsvToBeanBuilder<P>(reader)
                .withType(type)
                .withSeparator(COLUMN_SEPARATOR)
                .withIgnoreLeadingWhiteSpace(true)
                .withVerifyReader(true)
                .withThrowExceptions(true);
    }

    /**
     * @param reader The original reader of the file
     * @return Iterable list of Strings
     */
    private static Iterable<List<String>> chunkBufferedReader(BufferedReader reader) {
        return () -> new Iterator<>() {
            List<String> nextChunk = null;

            @Override
            public boolean hasNext() {
                try {
                    if (nextChunk == null) {
                        nextChunk = readChunk();
                    }
                    return !nextChunk.isEmpty();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public List<String> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more chunks available");
                }
                List<String> result = nextChunk;
                nextChunk = null;
                return result;
            }

            private List<String> readChunk() throws IOException {
                List<String> chunk = new ArrayList<>(BATCH_SIZE);
                String line;
                while (chunk.size() < BATCH_SIZE && (line = reader.readLine()) != null) {
                    chunk.add(line);
                }
                return chunk;
            }
        };
    }
}
