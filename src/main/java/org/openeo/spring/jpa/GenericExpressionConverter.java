package org.openeo.spring.jpa;

import javax.persistence.AttributeConverter;

/**
 * Expressions can be JSON, or GDAL statements, etc. Keep generic String.
 */
public class GenericExpressionConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return attribute.toString();
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return dbData;
    }
}