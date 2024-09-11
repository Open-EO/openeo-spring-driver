package org.openeo.spring.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer for cube generic dimension's (sub-)extents.
 */
public class ExtentSerializer extends StdSerializer<List<BigDecimal>> {
    
    private static final long serialVersionUID = 2205551205458838127L;

    public ExtentSerializer() {
        this(null);
    }
    
    public ExtentSerializer(Class<List<BigDecimal>> classType) {
        super(classType);
    }

    @Override
    public void serialize(List<BigDecimal> extent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
        if (extent == null) {
            throw new IOException("Input extent is null.");
        }
        if (extent.size() > 6) {
            throw new IOException("Extent tuple is greater than 6-dimensional: " + extent.size());
        }

        jsonGenerator.writeStartArray();
        for (BigDecimal coord : extent) {
            jsonGenerator.writeNumber(coord);
        }
        jsonGenerator.writeEndArray();
    }
}
