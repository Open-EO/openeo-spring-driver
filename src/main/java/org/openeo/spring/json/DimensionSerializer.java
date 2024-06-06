package org.openeo.spring.json;

import java.io.IOException;

import org.openeo.spring.model.Dimension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JPA serializer for (cube) dimensions.
 */
public class DimensionSerializer extends StdSerializer<Dimension>{
    
    private static final long serialVersionUID = -5946586040848103586L;

    public DimensionSerializer() {
        this(null);
    }
    
    public DimensionSerializer(Class<Dimension> classType) {
        super(classType);
    }

    @Override
    public void serialize(Dimension dim, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(dim.getDescription());
    }
}
