package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A parameter for a process graph. Data that is expected to be passed to a process graph either from the user directly or from the process that is executing the process graph.
 */
@ApiModel(description = "A parameter for a process graph. Data that is expected to be passed to a process graph either from the user directly or from the process that is executing the process graph.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ParameterReference   {
  @JsonProperty("from_parameter")
  private String fromParameter;

  public ParameterReference fromParameter(String fromParameter) {
    this.fromParameter = fromParameter;
    return this;
  }

  /**
   * The name of the parameter that data is expected to come from.
   * @return fromParameter
  */
  @ApiModelProperty(required = true, value = "The name of the parameter that data is expected to come from.")
  @NotNull


  public String getFromParameter() {
    return fromParameter;
  }

  public void setFromParameter(String fromParameter) {
    this.fromParameter = fromParameter;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParameterReference parameterReference = (ParameterReference) o;
    return Objects.equals(this.fromParameter, parameterReference.fromParameter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromParameter);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParameterReference {\n");
    
    sb.append("    fromParameter: ").append(toIndentedString(fromParameter)).append("\n");
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

