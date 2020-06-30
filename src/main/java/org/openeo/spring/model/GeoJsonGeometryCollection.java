package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.GeoJsonGeometry;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * GeoJsonGeometryCollection
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class GeoJsonGeometryCollection   {
  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
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

  @JsonProperty("geometries")
  @Valid
  private List<GeoJsonGeometry> geometries = new ArrayList<>();

  public GeoJsonGeometryCollection type(TypeEnum type) {
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

  public GeoJsonGeometryCollection geometries(List<GeoJsonGeometry> geometries) {
    this.geometries = geometries;
    return this;
  }

  public GeoJsonGeometryCollection addGeometriesItem(GeoJsonGeometry geometriesItem) {
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
    GeoJsonGeometryCollection geoJsonGeometryCollection = (GeoJsonGeometryCollection) o;
    return Objects.equals(this.type, geoJsonGeometryCollection.type) &&
        Objects.equals(this.geometries, geoJsonGeometryCollection.geometries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, geometries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoJsonGeometryCollection {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

