package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * GeoJSONGeometryCollection
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class GeoJSONGeometryCollection   {
  @JsonProperty("geometries")
  @Valid
  private List<GeoJsonGeometry> geometries = new ArrayList<>();

  public GeoJSONGeometryCollection geometries(List<GeoJsonGeometry> geometries) {
    this.geometries = geometries;
    return this;
  }

  public GeoJSONGeometryCollection addGeometriesItem(GeoJsonGeometry geometriesItem) {
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
    GeoJSONGeometryCollection geoJSONGeometryCollection = (GeoJSONGeometryCollection) o;
    return Objects.equals(this.geometries, geoJSONGeometryCollection.geometries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(geometries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoJSONGeometryCollection {\n");
    
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

