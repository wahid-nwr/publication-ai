package io.wahid.publication.ai.service;

import com.opencsv.bean.CsvToBeanBuilder;
import io.wahid.publication.ai.dto.WeatherInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class CSVParser {
    private static final Logger LOGGER = Logger.getLogger(CSVParser.class.getName());
    private static final char COLUMN_SEPARATOR = ',';
    private final WeatherAggregator aggregator;

    public CSVParser(WeatherAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public void parse(InputStream in) throws Exception {
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.ISO_8859_1);
        try (BufferedReader reader = new BufferedReader(isr)) {
            LOGGER.info("downloaded file from r2");
            new CsvToBeanBuilder<WeatherInfo>(reader)
                    .withType(WeatherInfo.class)
                    .withSeparator(COLUMN_SEPARATOR)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withVerifyReader(true)
                    .withThrowExceptions(true)
                    .withSkipLines(1)
                    .build()
                    .stream()
                    .forEach(aggregator::accept);

            aggregator.finish();
        }
    }
}
