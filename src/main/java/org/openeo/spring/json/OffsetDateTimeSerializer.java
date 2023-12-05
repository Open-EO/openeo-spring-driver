package org.openeo.spring.json;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom JSON serializer for timestamps.
 * <p>
 * Timestamps in STAC should follow RFC 3339, section 5.6,
 * e.g. "2019-01-01T00:00:00Z", and should serialize as {@code null}
 * when missing (e.g. open time intervals).
 * 
 * @see <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md#temporal-extent-object">STAC Collection: Temporal Extent Object</a>
 */
// TODO can we drop OpenEODateSerializer ? Beware: it does not handle null timestamps
public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
    
    /** The format of the serialized timestamp. */
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    @Override
    public void serialize(OffsetDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
        if (value == null) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeString(FORMATTER.format(value));
        }
    }
    
    @Override
    public Class<OffsetDateTime> handledType() {
        return OffsetDateTime.class;
    }
}
