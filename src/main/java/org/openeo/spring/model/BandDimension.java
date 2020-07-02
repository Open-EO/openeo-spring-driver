package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.OneOfnumberstring;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A dimension for the bands.  The band dimension only specifies the band names as dimension labels. Further information to the bands are available in either &#x60;sar:bands&#x60; or &#x60;eo:bands&#x60; in the &#x60;summaries&#x60; property.
 */
@ApiModel(description = "A dimension for the bands.  The band dimension only specifies the band names as dimension labels. Further information to the bands are available in either `sar:bands` or `eo:bands` in the `summaries` property.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class BandDimension   {
  @JsonProperty("values")
  @Valid
  private List<OneOfnumberstring> values = new ArrayList<>();

  public BandDimension values(List<OneOfnumberstring> values) {
    this.values = values;
    return this;
  }

  public BandDimension addValuesItem(OneOfnumberstring valuesItem) {
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
    BandDimension bandDimension = (BandDimension) o;
    return Objects.equals(this.values, bandDimension.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BandDimension {\n");
    
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

