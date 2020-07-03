package org.openeo.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * AdditionalDimension
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class AdditionalDimension   {
  @JsonProperty("extent")
  @Valid
  private List<BigDecimal> extent = null;

  @JsonProperty("values")
  @Valid
  private List<String> values = null;

  @JsonProperty("step")
  private JsonNullable<BigDecimal> step = JsonNullable.undefined();

  @JsonProperty("unit")
  private String unit;

  @JsonProperty("reference_system")
  private String referenceSystem;

  public AdditionalDimension extent(List<BigDecimal> extent) {
    this.extent = extent;
    return this;
  }

  public AdditionalDimension addExtentItem(BigDecimal extentItem) {
    if (this.extent == null) {
      this.extent = new ArrayList<>();
    }
    this.extent.add(extentItem);
    return this;
  }

  /**
   * If the dimension consists of [ordinal](https://en.wikipedia.org/wiki/Level_of_measurement#Ordinal_scale) values, the extent (lower and upper bounds) of the values as two-dimensional array. Use `null` for open intervals.
   * @return extent
  */
  @ApiModelProperty(value = "If the dimension consists of [ordinal](https://en.wikipedia.org/wiki/Level_of_measurement#Ordinal_scale) values, the extent (lower and upper bounds) of the values as two-dimensional array. Use `null` for open intervals.")

  @Valid
@Size(min=2,max=2) 
  public List<BigDecimal> getExtent() {
    return extent;
  }

  public void setExtent(List<BigDecimal> extent) {
    this.extent = extent;
  }

  public AdditionalDimension values(List<String> values) {
    this.values = values;
    return this;
  }

  public AdditionalDimension addValuesItem(String valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * A set of all potential values, especially useful for [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level) values.  **Important:** The order of the values MUST be exactly how the dimension values are also ordered in the data (cube). If the values specify band names, the values MUST be in the same order as they are in the corresponding band fields (i.e. `eo:bands` or `sar:bands`).
   * @return values
  */
  @ApiModelProperty(value = "A set of all potential values, especially useful for [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level) values.  **Important:** The order of the values MUST be exactly how the dimension values are also ordered in the data (cube). If the values specify band names, the values MUST be in the same order as they are in the corresponding band fields (i.e. `eo:bands` or `sar:bands`).")

  @Valid
@Size(min=1) 
  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public AdditionalDimension step(BigDecimal step) {
    this.step = JsonNullable.of(step);
    return this;
  }

  /**
   * If the dimension consists of [interval](https://en.wikipedia.org/wiki/Level_of_measurement#Interval_scale) values, the space between the values. Use `null` for irregularly spaced steps.
   * @return step
  */
  @ApiModelProperty(value = "If the dimension consists of [interval](https://en.wikipedia.org/wiki/Level_of_measurement#Interval_scale) values, the space between the values. Use `null` for irregularly spaced steps.")

  @Valid

  public JsonNullable<BigDecimal> getStep() {
    return step;
  }

  public void setStep(JsonNullable<BigDecimal> step) {
    this.step = step;
  }

  public AdditionalDimension unit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * The unit of measurement for the data, preferably the symbols from [SI](https://physics.nist.gov/cuu/Units/units.html) or [UDUNITS](https://ncics.org/portfolio/other-resources/udunits2/).
   * @return unit
  */
  @ApiModelProperty(value = "The unit of measurement for the data, preferably the symbols from [SI](https://physics.nist.gov/cuu/Units/units.html) or [UDUNITS](https://ncics.org/portfolio/other-resources/udunits2/).")


  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public AdditionalDimension referenceSystem(String referenceSystem) {
    this.referenceSystem = referenceSystem;
    return this;
  }

  /**
   * The reference system for the data.
   * @return referenceSystem
  */
  @ApiModelProperty(value = "The reference system for the data.")


  public String getReferenceSystem() {
    return referenceSystem;
  }

  public void setReferenceSystem(String referenceSystem) {
    this.referenceSystem = referenceSystem;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalDimension additionalDimension = (AdditionalDimension) o;
    return Objects.equals(this.extent, additionalDimension.extent) &&
        Objects.equals(this.values, additionalDimension.values) &&
        Objects.equals(this.step, additionalDimension.step) &&
        Objects.equals(this.unit, additionalDimension.unit) &&
        Objects.equals(this.referenceSystem, additionalDimension.referenceSystem);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extent, values, step, unit, referenceSystem);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalDimension {\n");
    
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    referenceSystem: ").append(toIndentedString(referenceSystem)).append("\n");
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

