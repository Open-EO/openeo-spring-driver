package org.openeo.spring.jpa;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.openeo.spring.json.OffsetDateTimeSerializer;

@Converter
public class TimeExtentConverter implements AttributeConverter<List<List<OffsetDateTime>>, String> {
    
    /** The format of the serialized timestamp. */
    private static final DateTimeFormatter FORMATTER = OffsetDateTimeSerializer.FORMATTER;

    private static final String COORDS_SPLIT_CHAR = ",";
    private static final String EXTENT_SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<List<OffsetDateTime>> timeExtent) {
        if (null == timeExtent || timeExtent.isEmpty()) {
            return "";
        }

        //
        // [!] given extent can be a simple List<OffsetDateTime> (java generics "flaw" ?)
        //
        List<List<OffsetDateTime>> fixedExtent = new ArrayList<>(timeExtent);        
        Object el = timeExtent.get(0);
        if (el instanceof OffsetDateTime) {
            fixedExtent.clear();
            List<OffsetDateTime> simpleExtent = new ArrayList<>();
            for (Object coord : timeExtent) {
                simpleExtent.add((OffsetDateTime) coord);
            }
            fixedExtent.add(simpleExtent);
        }

        List<String> outList = new ArrayList<>();
        for (List<OffsetDateTime> subExtent : fixedExtent) {
            String strExtent = subExtent.stream()
                    .map((x) -> FORMATTER.format(x))
                    .collect(Collectors.joining(COORDS_SPLIT_CHAR));
            outList.add(strExtent);
        }
        return String.join(EXTENT_SPLIT_CHAR, outList);
    }

    @Override
    public List<List<OffsetDateTime>> convertToEntityAttribute(String string) {
        if (null == string) {
            return Collections.emptyList();
        }

        List<List<OffsetDateTime>> extent = new ArrayList<>();

        for (String subExtentStr : string.split(EXTENT_SPLIT_CHAR)) {
            List<OffsetDateTime> subExtent = Arrays.asList(
                    subExtentStr.split(COORDS_SPLIT_CHAR)).stream()
                    .map((x) -> OffsetDateTime.parse(x, FORMATTER))
                    .collect(Collectors.toList());
            extent.add(subExtent);
        }

        return extent;
    }
}
