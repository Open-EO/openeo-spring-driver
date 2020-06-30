package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.GeoJsonGeometryCollection;
import org.openeo.spring.model.GeoJsonLineString;
import org.openeo.spring.model.GeoJsonMultiLineString;
import org.openeo.spring.model.GeoJsonMultiPoint;
import org.openeo.spring.model.GeoJsonMultiPolygon;
import org.openeo.spring.model.GeoJsonPoint;
import org.openeo.spring.model.GeoJsonPolygon;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * GeoJsonGeometry
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
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

  @JsonProperty("coordinates")
  @Valid
  private List<List<List<List<BigDecimal>>>> coordinates = new ArrayList<>();

  @JsonProperty("geometries")
  @Valid
  private List<GeoJsonGeometry> geometries = new ArrayList<>();

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

  public GeoJsonGeometry coordinates(List<List<List<List<BigDecimal>>>> coordinates) {
    this.coordinates = coordinates;
    return this;
  }

  public GeoJsonGeometry addCoordinatesItem(List<List<List<BigDecimal>>> coordinatesItem) {
    this.coordinates.add(coordinatesItem);
    return this;
  }

  /**
   * Get coordinates
   * @return coordinates
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<List<List<List<BigDecimal>>>> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<List<List<List<BigDecimal>>>> coordinates) {
    this.coordinates = coordinates;
  }

  public GeoJsonGeometry geometries(List<GeoJsonGeometry> geometries) {
    this.geometries = geometries;
    return this;
  }

  public GeoJsonGeometry addGeometriesItem(GeoJsonGeometry geometriesItem) {
    this.geometries.add(geometriesItem);
    return this;
  }

  /**
   * Get geometries
   * @return geometries
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<GeoJsonGeometry> getGeometries() {
    return geometries;
  }

  public void setGeometries(List<GeoJsonGeometry> geometries) {
    this.geometries = geometries;
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
    return Objects.equals(this.type, geoJsonGeometry.type) &&
        Objects.equals(this.coordinates, geoJsonGeometry.coordinates) &&
        Objects.equals(this.geometries, geoJsonGeometry.geometries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, coordinates, geometries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoJsonGeometry {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
    sb.append("    geometries: ").append(toIndentedString(geometries)).append("\n");
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

