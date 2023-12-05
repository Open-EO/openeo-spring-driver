package org.openeo.spring.json;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom JSON serializer for temporal intervals,
 * i.e. a list of [tmin,tmax] tuples.
 *
 * @see OffsetDateTimeSerializer
 */
public class TimeIntervalSerializer extends JsonSerializer<List<OffsetDateTime>> {
    
    @Override
    public void serialize(List<OffsetDateTime> interval, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
        if (interval == null) {
            throw new IOException("Input time interval is null.");
        }
        if (interval.size() != 2) {
            throw new IOException("Input time interval has illegal size: " + interval.size());
        }
        OffsetDateTimeSerializer odtSer = new OffsetDateTimeSerializer();
        
        jsonGenerator.writeStartArray();
        for (OffsetDateTime odt : interval) {
            odtSer.serialize(odt, jsonGenerator, serializerProvider);
        }
        jsonGenerator.writeEndArray();
    }
}