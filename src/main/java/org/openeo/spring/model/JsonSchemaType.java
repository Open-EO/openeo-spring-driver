package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets json_schema_type
 */
public enum JsonSchemaType {
  
  ARRAY("array"),
  
  BOOLEAN("boolean"),
  
  INTEGER("integer"),
  
  NULL("null"),
  
  NUMBER("number"),
  
  OBJECT("object"),
  
  STRING("string");

  private String value;

  JsonSchemaType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JsonSchemaType fromValue(String value) {
    for (JsonSchemaType b : JsonSchemaType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

