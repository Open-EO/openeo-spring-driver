package org.openeo.spring.model;

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Type of the dimension, always `spatial`.
 */
public enum CollectionDimensionTypeSpatial {
  
  SPATIAL("spatial");

  private String value;

  CollectionDimensionTypeSpatial(String value) {
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
  public static CollectionDimensionTypeSpatial fromValue(String value) {
    for (CollectionDimensionTypeSpatial b : CollectionDimensionTypeSpatial.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

