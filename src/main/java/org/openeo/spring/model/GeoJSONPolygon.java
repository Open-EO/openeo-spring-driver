package org.openeo.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * GeoJSONPolygon
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class GeoJSONPolygon   {
  @JsonProperty("coordinates")
  @Valid
  private List<List<List<BigDecimal>>> coordinates = new ArrayList<>();

  public GeoJSONPolygon coordinates(List<List<List<BigDecimal>>> coordinates) {
    this.coordinates = coordinates;
    return this;
  }

  public GeoJSONPolygon addCoordinatesItem(List<List<BigDecimal>> coordinatesItem) {
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

  public List<List<List<BigDecimal>>> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<List<List<BigDecimal>>> coordinates) {
    this.coordinates = coordinates;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeoJSONPolygon geoJSONPolygon = (GeoJSONPolygon) o;
    return Objects.equals(this.coordinates, geoJSONPolygon.coordinates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coordinates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoJSONPolygon {\n");
    
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
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

