package org.openeo.spring.model;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModelProperty;

/**
 * GeoJsonGeometry
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = GeoJsonPoint.class, name = "Point"),
  @JsonSubTypes.Type(value = GeoJsonLineString.class, name = "LineString"),
  @JsonSubTypes.Type(value = GeoJsonPolygon.class, name = "Polygon"),
  @JsonSubTypes.Type(value = GeoJsonMultiPoint.class, name = "MultiPoint"),
  @JsonSubTypes.Type(value = GeoJsonMultiLineString.class, name = "MultiLineString"),
  @JsonSubTypes.Type(value = GeoJsonMultiPolygon.class, name = "MultiPolygon"),
  @JsonSubTypes.Type(value = GeoJsonGeometryCollection.class, name = "GeometryCollection"),
})

public class GeoJsonGeometry   {
  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    POINT("Point"),
    
    LINESTRING("LineString"),
    
    POLYGON("Polygon"),
    
    MULTIPOINT("MultiPoint"),
    
    MULTILINESTRING("MultiLineString"),
    
    MULTIPOLYGON("MultiPolygon"),
    
    GEOMETRYCOLLECTION("GeometryCollection");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("type")
  private TypeEnum type;

  public GeoJsonGeometry type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeoJsonGeometry geoJsonGeometry = (GeoJsonGeometry) o;
    return Objects.equals(this.type, geoJsonGeometry.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoJsonGeometry {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

