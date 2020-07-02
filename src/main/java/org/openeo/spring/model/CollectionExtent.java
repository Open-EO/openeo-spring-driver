package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openeo.spring.model.CollectionSpatialExtent;
import org.openeo.spring.model.CollectionTemporalExtent;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The extent of the features in the collection. Additional members MAY be added to represent other extents, for example, thermal or pressure ranges.
 */
@ApiModel(description = "The extent of the features in the collection. Additional members MAY be added to represent other extents, for example, thermal or pressure ranges.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class CollectionExtent   {
  @JsonProperty("spatial")
  private CollectionSpatialExtent spatial;

  @JsonProperty("temporal")
  private CollectionTemporalExtent temporal;

  public CollectionExtent spatial(CollectionSpatialExtent spatial) {
    this.spatial = spatial;
    return this;
  }

  /**
   * Get spatial
   * @return spatial
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public CollectionSpatialExtent getSpatial() {
    return spatial;
  }

  public void setSpatial(CollectionSpatialExtent spatial) {
    this.spatial = spatial;
  }

  public CollectionExtent temporal(CollectionTemporalExtent temporal) {
    this.temporal = temporal;
    return this;
  }

  /**
   * Get temporal
   * @return temporal
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public CollectionTemporalExtent getTemporal() {
    return temporal;
  }

  public void setTemporal(CollectionTemporalExtent temporal) {
    this.temporal = temporal;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionExtent collectionExtent = (CollectionExtent) o;
    return Objects.equals(this.spatial, collectionExtent.spatial) &&
        Objects.equals(this.temporal, collectionExtent.temporal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(spatial, temporal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionExtent {\n");
    
    sb.append("    spatial: ").append(toIndentedString(spatial)).append("\n");
    sb.append("    temporal: ").append(toIndentedString(temporal)).append("\n");
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

