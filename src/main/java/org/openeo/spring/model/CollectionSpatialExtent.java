package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The *potential* spatial extent of the features in the collection.
 */
@ApiModel(description = "The *potential* spatial extent of the features in the collection.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class CollectionSpatialExtent   {
  @JsonProperty("bbox")
  @Valid
  private List<List<BigDecimal>> bbox = null;

  public CollectionSpatialExtent bbox(List<List<BigDecimal>> bbox) {
    this.bbox = bbox;
    return this;
  }

  public CollectionSpatialExtent addBboxItem(List<BigDecimal> bboxItem) {
    if (this.bbox == null) {
      this.bbox = new ArrayList<>();
    }
    this.bbox.add(bboxItem);
    return this;
  }

  /**
   * One or more bounding boxes that describe the spatial extent of the dataset. If multiple areas are provided, the union of the bounding boxes describes the spatial extent.
   * @return bbox
  */
  @ApiModelProperty(value = "One or more bounding boxes that describe the spatial extent of the dataset. If multiple areas are provided, the union of the bounding boxes describes the spatial extent.")

  @Valid
@Size(min=1) 
  public List<List<BigDecimal>> getBbox() {
    return bbox;
  }

  public void setBbox(List<List<BigDecimal>> bbox) {
    this.bbox = bbox;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionSpatialExtent collectionSpatialExtent = (CollectionSpatialExtent) o;
    return Objects.equals(this.bbox, collectionSpatialExtent.bbox);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bbox);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionSpatialExtent {\n");
    
    sb.append("    bbox: ").append(toIndentedString(bbox)).append("\n");
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

