package org.openeo.spring.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.openeo.spring.model.Asset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class AssetsSerializer extends StdSerializer<Map<String, Asset> >{
	
	private static final long serialVersionUID = -5946586040848103586L;

	public AssetsSerializer() {
		this(null);
	}
	
	public AssetsSerializer(Class<Map<String, Asset> > classType) {
		super(classType);
	}

	@Override
	public void serialize(Map<String, Asset>  value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
//		gen.writeStringField("id", value.getId());
		Iterator<Asset> values = value.values().iterator();
		while(values.hasNext()) {
			Asset current = values.next();
			gen.writeObjectField(current.getTitle(), current);
		}
		gen.writeEndObject();
	}

}
