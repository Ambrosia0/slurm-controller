package com.ambrosia.cluster_controller.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class StringSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // String replaced = value.replace("\\\\", "\\");
        // gen.writeRawValue("\"" + replaced + "\"");
        String replaced = value.replace("\n", "\\n");
        gen.writeRawValue("\"" + replaced + "\"");
    }
    
}
