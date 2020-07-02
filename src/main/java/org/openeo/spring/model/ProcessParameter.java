package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openeo.spring.model.BaseParameter;
import org.openeo.spring.model.ParameterSchema;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ProcessParameter
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ProcessParameter extends BaseParameter  {
  @JsonProperty("schema")
  private ParameterSchema schema = null;

  public ProcessParameter schema(ParameterSchema schema) {
    this.schema = schema;
    return this;
  }

  /**
   * Get schema
   * @return schema
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public ParameterSchema getSchema() {
    return schema;
  }

  public void setSchema(ParameterSchema schema) {
    this.schema = schema;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProcessParameter processParameter = (ProcessParameter) o;
    return Objects.equals(this.schema, processParameter.schema) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessParameter {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
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

