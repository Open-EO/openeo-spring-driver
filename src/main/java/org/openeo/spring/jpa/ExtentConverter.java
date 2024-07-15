package org.openeo.spring.jpa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ExtentConverter implements AttributeConverter<List<List<BigDecimal>>, String> {
    private static final String COORDS_SPLIT_CHAR = ",";
    private static final String EXTENT_SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<List<BigDecimal>> extent) {
        if (null == extent || extent.isEmpty()) {
            return "";
        }
       
        //
        // [!] given extent can be a simple List<BigDecimal> (java generics "flaw" ?)
        //
        List<List<BigDecimal>> fixedExtent = new ArrayList<>(extent);        
        Object el = extent.get(0);
        if (el instanceof BigDecimal) {
            fixedExtent.clear();
            List<BigDecimal> simpleExtent = new ArrayList<>();
            for (Object coord : extent) {
                simpleExtent.add((BigDecimal) coord);
            }
            fixedExtent.add(simpleExtent);
        }

        List<String> outList = new ArrayList<>();
        for (List<BigDecimal> subExtent : fixedExtent) {
            String strExtent = subExtent.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(COORDS_SPLIT_CHAR));
            outList.add(strExtent);
        }
        
        return String.join(EXTENT_SPLIT_CHAR, outList);
    }

    @Override
    public List<List<BigDecimal>> convertToEntityAttribute(String string) {
        if (null == string) {
            return Collections.emptyList();
        }

        List<List<BigDecimal>> extent = new ArrayList<>();

        for (String subExtentStr : string.split(EXTENT_SPLIT_CHAR)) {
            List<BigDecimal> subExtent = Arrays.asList(
                    subExtentStr.split(COORDS_SPLIT_CHAR)).stream()
                    .map((x) -> new BigDecimal(x))
                    .collect(Collectors.toList());
            extent.add(subExtent);
        }

        return extent;
    }
}
