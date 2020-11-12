package org.openeo.spring.model;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * By default, only ranges with a minimum and a maximum value can be specified. Ranges can be specified for ordinal values only, which means they need to have a rank order. Therefore, ranges can only be specified for numbers and some special types of strings. Examples: grades (A to F), dates or times. Implementors are free to add other derived statistical values to the object, for example &#x60;mean&#x60; or &#x60;stddev&#x60;.
 */
@ApiModel(description = "By default, only ranges with a minimum and a maximum value can be specified. Ranges can be specified for ordinal values only, which means they need to have a rank order. Therefore, ranges can only be specified for numbers and some special types of strings. Examples: grades (A to F), dates or times. Implementors are free to add other derived statistical values to the object, for example `mean` or `stddev`.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class CollectionSummaryStats   {
  @JsonProperty("min")
  private double min = 0;

  @JsonProperty("max")
  private double max = 0;

  public CollectionSummaryStats min(double min) {
    this.min = min;
    return this;
  }

  /**
   * Get min
   * @return min
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public double getMin() {
    return min;
  }

  public void setMin(double min) {
    this.min = min;
  }

  public CollectionSummaryStats max(double max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public Object getMax() {
    return max;
  }

  public void setMax(double max) {
    this.max = max;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionSummaryStats collectionSummaryStats = (CollectionSummaryStats) o;
    return Objects.equals(this.min, collectionSummaryStats.min) &&
        Objects.equals(this.max, collectionSummaryStats.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionSummaryStats {\n");
    
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
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

