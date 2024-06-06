package org.openeo.spring.json;

import java.io.IOException;

import org.openeo.spring.model.CollectionExtent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serializer for CollectionExtent objects.
 */
public class CollectionExtentSerializer extends StdSerializer<CollectionExtent> {
    
    private static final long serialVersionUID = 1378169060344489824L;

    public CollectionExtentSerializer() {
        this(null);
    }
    
    public CollectionExtentSerializer(Class<CollectionExtent> classType) {
        super(classType);
    }
    
    @Override
    public void serialize(CollectionExtent extent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
        if (extent == null) {
            throw new IOException("Input extent is null.");
        }        

        if (null != extent.getTemporal()) {
            jsonGenerator.writeObjectField("temporal", extent.getTemporal());
        }
        
        if (null != extent.getSpatial()) {
            jsonGenerator.writeObjectField("spatial", extent.getSpatial());
        }
    }
}
