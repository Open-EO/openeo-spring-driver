package org.openeo.spring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Formats of a processing expression.
 *
 * @see <a href="https://github.com/stac-extensions/processing?tab=readme-ov-file#expression-object">Expression Objects</a>
 */
public enum ProcessingExpressionFormat {
    OPENEO("openeo"),
    GDAL_CALC("gdal-calc"),
    RIO_CALC("rio-calc");
    
    private final String label;

    private ProcessingExpressionFormat(String label) {
        this.label = label;
    }

    /** Gets the format label to be serialized on a JSON document. */
    @JsonValue
    public String getLabel() {
        return this.label;
    }
    
    @JsonCreator
    public static ProcessingExpressionFormat fromValue(String value) {
        for (ProcessingExpressionFormat f : ProcessingExpressionFormat.values()) {
            if (f.getLabel().equals(value)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }   
    
    @Override
    public String toString() {
        return this.getLabel();
    }
}