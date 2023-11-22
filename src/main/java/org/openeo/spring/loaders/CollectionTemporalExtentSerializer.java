package org.openeo.spring.loaders;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import org.openeo.spring.model.CollectionTemporalExtent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom JSON serializer for temporal extents.  
 */
public class CollectionTemporalExtentSerializer extends JsonSerializer<CollectionTemporalExtent> {
    
    @Override
    public void serialize(CollectionTemporalExtent value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
        if (value == null) {
            throw new IOException("CollectionTemporalExtent argument is null.");
        }
        OffsetDateTimeSerializer odtSer = new OffsetDateTimeSerializer();
        
        jsonGenerator.writeStartArray();
        for (List<OffsetDateTime> l0 : value.getInterval()) {
            jsonGenerator.writeStartArray();
            for (OffsetDateTime odt : l0) {                
                odtSer.serialize(odt, jsonGenerator, serializerProvider);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
    }
}