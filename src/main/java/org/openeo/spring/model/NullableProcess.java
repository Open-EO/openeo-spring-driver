package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.ProcessExample;
import org.openeo.spring.model.ProcessParameter;
import org.openeo.spring.model.ProcessReturnValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NullableProcess
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class NullableProcess extends Process  {
  @JsonProperty("summary")
  private JsonNullable<Object> summary = JsonNullable.undefined();

  @JsonProperty("description")
  private JsonNullable<Object> description = JsonNullable.undefined();

  @JsonProperty("parameters")
  private JsonNullable<Object> parameters = JsonNullable.undefined();

  @JsonProperty("returns")
  private JsonNullable<Object> returns = JsonNullable.undefined();

  public NullableProcess summary(Object summary) {
    this.summary = JsonNullable.of(summary);
    return this;
  }

  /**
   * Get summary
   * @return summary
  */
  @ApiModelProperty(value = "")


  public JsonNullable<Object> getSummary() {
    return summary;
  }

  public void setSummary(JsonNullable<Object> summary) {
    this.summary = summary;
  }

  public NullableProcess description(Object description) {
    this.description = JsonNullable.of(description);
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @ApiModelProperty(value = "")


  public JsonNullable<Object> getDescription() {
    return description;
  }

  public void setDescription(JsonNullable<Object> description) {
    this.description = description;
  }

  public NullableProcess parameters(Object parameters) {
    this.parameters = JsonNullable.of(parameters);
    return this;
  }

  /**
   * Get parameters
   * @return parameters
  */
  @ApiModelProperty(value = "")


  public JsonNullable<Object> getParameters() {
    return parameters;
  }

  public void setParameters(JsonNullable<Object> parameters) {
    this.parameters = parameters;
  }

  public NullableProcess returns(Object returns) {
    this.returns = JsonNullable.of(returns);
    return this;
  }

  /**
   * Get returns
   * @return returns
  */
  @ApiModelProperty(value = "")


  public JsonNullable<Object> getReturns() {
    return returns;
  }

  public void setReturns(JsonNullable<Object> returns) {
    this.returns = returns;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NullableProcess nullableProcess = (NullableProcess) o;
    return Objects.equals(this.summary, nullableProcess.summary) &&
        Objects.equals(this.description, nullableProcess.description) &&
        Objects.equals(this.parameters, nullableProcess.parameters) &&
        Objects.equals(this.returns, nullableProcess.returns) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(summary, description, parameters, returns, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NullableProcess {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    returns: ").append(toIndentedString(returns)).append("\n");
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

