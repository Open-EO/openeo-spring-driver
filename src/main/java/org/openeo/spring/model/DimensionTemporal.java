package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * DimensionTemporal
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonInclude(JsonInclude.Include.NON_NULL)
// TODO should this class be based on OffsetDateTime like CollectionTemporalExtent to 
//      control its serialization format (OffsetDateTimeSerializer) and avoid inconsistencies?
public class DimensionTemporal extends Dimension  {
  @JsonProperty("values")
  @Valid
  private List<String> values = null;

  @JsonProperty("extent")
  @Valid
  private List<String> extent = new ArrayList<>();

  @JsonProperty("step")
  private String step = null;

  public DimensionTemporal values(List<String> values) {
    this.values = values;
    return this;
  }

  public DimensionTemporal addValuesItem(String valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * If the dimension consists of set of specific values they can be listed here. The dates and/or times must be strings compliant to [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601).
   * @return values
  */
  @ApiModelProperty(value = "If the dimension consists of set of specific values they can be listed here. The dates and/or times must be strings compliant to [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601).")

@Size(min=1) 
  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public DimensionTemporal extent(List<String> extent) {
    this.extent = extent;
    return this;
  }

  public DimensionTemporal addExtentItem(String extentItem) {
    this.extent.add(extentItem);
    return this;
  }

  /**
   * Extent (lower and upper bounds) of the dimension as two-dimensional array. The dates and/or times must be strings compliant to [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601). `null` is allowed for open date ranges.
   * @return extent
  */
  @ApiModelProperty(required = true, value = "Extent (lower and upper bounds) of the dimension as two-dimensional array. The dates and/or times must be strings compliant to [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601). `null` is allowed for open date ranges.")
  @NotNull

@Size(min=2,max=2) 
  public List<String> getExtent() {
    return extent;
  }

  public void setExtent(List<String> extent) {
    this.extent = extent;
  }

  public DimensionTemporal step(String step) {
    this.step = step;
    return this;
  }

  /**
   * The space between the temporal instances as [ISO 8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations), e.g. `P1D`. Use `null` for irregularly spaced steps.
   * @return step
  */
  @ApiModelProperty(value = "The space between the temporal instances as [ISO 8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations), e.g. `P1D`. Use `null` for irregularly spaced steps.")


  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DimensionTemporal dimensionTemporal = (DimensionTemporal) o;
    return Objects.equals(this.values, dimensionTemporal.values) &&
        Objects.equals(this.extent, dimensionTemporal.extent) &&
        Objects.equals(this.step, dimensionTemporal.step) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, extent, step, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DimensionTemporal {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
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

