package org.openeo.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A spatial dimension in one of the horizontal (x or y) or vertical (z) directions.
 */
@ApiModel(description = "A spatial dimension in one of the horizontal (x or y) or vertical (z) directions.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class SpatialDimension   {
  /**
   * Axis of the spatial dimension (`x`, `y` or `z`).
   */
  public enum AxisEnum {
    X("x"),
    
    Y("y"),
    
    Z("z");

    private String value;

    AxisEnum(String value) {
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
    public static AxisEnum fromValue(String value) {
      for (AxisEnum b : AxisEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("axis")
  private AxisEnum axis;

  @JsonProperty("extent")
  @Valid
  private List<BigDecimal> extent = null;

  @JsonProperty("values")
  @Valid
  private List<BigDecimal> values = null;

  @JsonProperty("step")
  private JsonNullable<BigDecimal> step = JsonNullable.undefined();

  @JsonProperty("reference_system")
  private String referenceSystem = null;

  public SpatialDimension axis(AxisEnum axis) {
    this.axis = axis;
    return this;
  }

  /**
   * Axis of the spatial dimension (`x`, `y` or `z`).
   * @return axis
  */
  @ApiModelProperty(required = true, value = "Axis of the spatial dimension (`x`, `y` or `z`).")
  @NotNull


  public AxisEnum getAxis() {
    return axis;
  }

  public void setAxis(AxisEnum axis) {
    this.axis = axis;
  }

  public SpatialDimension extent(List<BigDecimal> extent) {
    this.extent = extent;
    return this;
  }

  public SpatialDimension addExtentItem(BigDecimal extentItem) {
    if (this.extent == null) {
      this.extent = new ArrayList<>();
    }
    this.extent.add(extentItem);
    return this;
  }

  /**
   * Extent (lower and upper bounds) of the dimension as two-dimensional array. Open intervals with `null` are not allowed.
   * @return extent
  */
  @ApiModelProperty(value = "Extent (lower and upper bounds) of the dimension as two-dimensional array. Open intervals with `null` are not allowed.")

  @Valid
@Size(min=2,max=2) 
  public List<BigDecimal> getExtent() {
    return extent;
  }

  public void setExtent(List<BigDecimal> extent) {
    this.extent = extent;
  }

  public SpatialDimension values(List<BigDecimal> values) {
    this.values = values;
    return this;
  }

  public SpatialDimension addValuesItem(BigDecimal valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * A set of all potential values.
   * @return values
  */
  @ApiModelProperty(value = "A set of all potential values.")

  @Valid
@Size(min=1) 
  public List<BigDecimal> getValues() {
    return values;
  }

  public void setValues(List<BigDecimal> values) {
    this.values = values;
  }

  public SpatialDimension step(BigDecimal step) {
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

  public SpatialDimension referenceSystem(String referenceSystem) {
    this.referenceSystem = referenceSystem;
    return this;
  }

  /**
   * The spatial reference system for the data, specified as [EPSG code](http://www.epsg-registry.org/), [WKT2 (ISO 19162) string](http://docs.opengeospatial.org/is/18-010r7/18-010r7.html) or [PROJ definition (deprecated)](https://proj.org/usage/quickstart.html). Defaults to EPSG code 4326.
   * @return referenceSystem
  */
  @ApiModelProperty(value = "The spatial reference system for the data, specified as [EPSG code](http://www.epsg-registry.org/), [WKT2 (ISO 19162) string](http://docs.opengeospatial.org/is/18-010r7/18-010r7.html) or [PROJ definition (deprecated)](https://proj.org/usage/quickstart.html). Defaults to EPSG code 4326.")

  @Valid

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
    SpatialDimension spatialDimension = (SpatialDimension) o;
    return Objects.equals(this.axis, spatialDimension.axis) &&
        Objects.equals(this.extent, spatialDimension.extent) &&
        Objects.equals(this.values, spatialDimension.values) &&
        Objects.equals(this.step, spatialDimension.step) &&
        Objects.equals(this.referenceSystem, spatialDimension.referenceSystem);
  }

  @Override
  public int hashCode() {
    return Objects.hash(axis, extent, values, step, referenceSystem);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpatialDimension {\n");
    
    sb.append("    axis: ").append(toIndentedString(axis)).append("\n");
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
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

