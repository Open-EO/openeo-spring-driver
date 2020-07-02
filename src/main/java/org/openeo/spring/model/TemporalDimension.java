package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A temporal dimension based on the ISO 8601 standard. The temporal reference system for the data is expected to be ISO 8601 compliant (Gregorian calendar / UTC). Data not compliant with ISO 8601 can be represented as an *Additional Dimension Object* with &#x60;type&#x60; set to &#x60;temporal&#x60;.
 */
@ApiModel(description = "A temporal dimension based on the ISO 8601 standard. The temporal reference system for the data is expected to be ISO 8601 compliant (Gregorian calendar / UTC). Data not compliant with ISO 8601 can be represented as an *Additional Dimension Object* with `type` set to `temporal`.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class TemporalDimension   {
  @JsonProperty("values")
  @Valid
  private List<String> values = null;

  @JsonProperty("extent")
  @Valid
  private List<String> extent = new ArrayList<>();

  @JsonProperty("step")
  private JsonNullable<String> step = JsonNullable.undefined();

  public TemporalDimension values(List<String> values) {
    this.values = values;
    return this;
  }

  public TemporalDimension addValuesItem(String valuesItem) {
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

  public TemporalDimension extent(List<String> extent) {
    this.extent = extent;
    return this;
  }

  public TemporalDimension addExtentItem(String extentItem) {
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

  public TemporalDimension step(String step) {
    this.step = JsonNullable.of(step);
    return this;
  }

  /**
   * The space between the temporal instances as [ISO 8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations), e.g. `P1D`. Use `null` for irregularly spaced steps.
   * @return step
  */
  @ApiModelProperty(value = "The space between the temporal instances as [ISO 8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations), e.g. `P1D`. Use `null` for irregularly spaced steps.")


  public JsonNullable<String> getStep() {
    return step;
  }

  public void setStep(JsonNullable<String> step) {
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
    TemporalDimension temporalDimension = (TemporalDimension) o;
    return Objects.equals(this.values, temporalDimension.values) &&
        Objects.equals(this.extent, temporalDimension.extent) &&
        Objects.equals(this.step, temporalDimension.step);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, extent, step);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TemporalDimension {\n");
    
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

