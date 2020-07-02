package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.AnyOfarrayjsonSchema;
import org.openeo.spring.model.JsonSchema;
import org.openeo.spring.model.OneOfjsonSchemaTypeset;
import org.openeo.spring.model.Parameter;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ParameterJsonSchema
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class ParameterJsonSchema extends JsonSchema  {
  @JsonProperty("parameters")
  @Valid
  private List<Parameter> parameters = null;

  public ParameterJsonSchema parameters(List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public ParameterJsonSchema addParametersItem(Parameter parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * A list of parameters passed to the child process graph.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.
   * @return parameters
  */
  @ApiModelProperty(value = "A list of parameters passed to the child process graph.  The order in the array corresponds to the parameter order to be used in clients that don't support named parameters.")

  @Valid

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParameterJsonSchema parameterJsonSchema = (ParameterJsonSchema) o;
    return Objects.equals(this.parameters, parameterJsonSchema.parameters) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameters, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParameterJsonSchema {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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

