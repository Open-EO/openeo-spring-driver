package org.openeo.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * DimensionOther
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DimensionOther extends Dimension implements HasUnit {
  @JsonProperty("extent")
  @Valid
  @ElementCollection
  private List<BigDecimal> extent = null;

  @JsonProperty("values")
  @Valid
  @ElementCollection
  @Column(name = "dim_values", nullable = true)
  private List<String> values = null;

  @JsonProperty("step")
  private String step = null;

  @JsonProperty("unit")
  private String unit;

  @JsonProperty("reference_system")
  private String referenceSystem;

  public DimensionOther extent(List<BigDecimal> extent) {
    this.extent = extent;
    return this;
  }

  public DimensionOther addExtentItem(BigDecimal extentItem) {
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

  public DimensionOther values(List<String> values) {
    this.values = values;
    return this;
  }

  public DimensionOther addValuesItem(String valuesItem) {
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

  public DimensionOther step(String step) {
    this.step = step;
    return this;
  }

  /**
   * If the dimension consists of [interval](https://en.wikipedia.org/wiki/Level_of_measurement#Interval_scale) values, the space between the values. Use `null` for irregularly spaced steps.
   * @return step
  */
  @ApiModelProperty(value = "If the dimension consists of [interval](https://en.wikipedia.org/wiki/Level_of_measurement#Interval_scale) values, the space between the values. Use `null` for irregularly spaced steps.")

  @Valid

  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }

  public DimensionOther unit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * The unit of measurement for the data, preferably the symbols from [SI](https://physics.nist.gov/cuu/Units/units.html) or [UDUNITS](https://ncics.org/portfolio/other-resources/udunits2/).
   * @return unit
  */
  @ApiModelProperty(value = "The unit of measurement for the data, preferably the symbols from [SI](https://physics.nist.gov/cuu/Units/units.html) or [UDUNITS](https://ncics.org/portfolio/other-resources/udunits2/).")


  @Override
  public String getUnit() {
    return unit;
  }

  @Override
  public void setUnit(String unit) {
    this.unit = unit;
  }

  public DimensionOther referenceSystem(String referenceSystem) {
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
    DimensionOther dimensionOther = (DimensionOther) o;
    return Objects.equals(this.extent, dimensionOther.extent) &&
        Objects.equals(this.values, dimensionOther.values) &&
        Objects.equals(this.step, dimensionOther.step) &&
        Objects.equals(this.unit, dimensionOther.unit) &&
        Objects.equals(this.referenceSystem, dimensionOther.referenceSystem) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extent, values, step, unit, referenceSystem, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DimensionOther {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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

