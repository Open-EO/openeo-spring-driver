package org.openeo.spring.json;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class OpenEODateSerializer extends StdSerializer<OffsetDateTime> {
	
	private static final long serialVersionUID = -6557516394955871426L;
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));

	public OpenEODateSerializer() {
		this(null);
	}
	
	public OpenEODateSerializer(Class<OffsetDateTime> t) {
		super(t);
	}

	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(formatter.format(value.atZoneSameInstant(ZoneOffset.UTC)));

	}

}
