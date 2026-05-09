package io.wahid.knowledge.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomDateSerializer extends JsonSerializer<LocalDate> {
    private static final String DTF = "dd MMM yyyy";

    @Override
    public void serialize(final LocalDate value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
