package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.BandDimension;
import org.openeo.spring.model.Dimension;
import org.openeo.spring.model.OneOfnumberstring;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DimensionBands
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class DimensionBands extends Dimension  {
  @JsonProperty("values")
  @Valid
  private List<OneOfnumberstring> values = new ArrayList<>();

  public DimensionBands values(List<OneOfnumberstring> values) {
    this.values = values;
    return this;
  }

  public DimensionBands addValuesItem(OneOfnumberstring valuesItem) {
    this.values.add(valuesItem);
    return this;
  }

  /**
   * A set of all potential values, especially useful for [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level) values.  **Important:** The order of the values MUST be exactly how the dimension values are also ordered in the data (cube). If the values specify band names, the values MUST be in the same order as they are in the corresponding band fields (i.e. `eo:bands` or `sar:bands`).
   * @return values
  */
  @ApiModelProperty(required = true, value = "A set of all potential values, especially useful for [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level) values.  **Important:** The order of the values MUST be exactly how the dimension values are also ordered in the data (cube). If the values specify band names, the values MUST be in the same order as they are in the corresponding band fields (i.e. `eo:bands` or `sar:bands`).")
  @NotNull

  @Valid
@Size(min=1) 
  public List<OneOfnumberstring> getValues() {
    return values;
  }

  public void setValues(List<OneOfnumberstring> values) {
    this.values = values;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DimensionBands dimensionBands = (DimensionBands) o;
    return Objects.equals(this.values, dimensionBands.values) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DimensionBands {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
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

