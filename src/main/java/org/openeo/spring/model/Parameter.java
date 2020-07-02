package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openeo.spring.model.BaseParameter;
import org.openeo.spring.model.DataTypeSchema;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Parameter
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class Parameter extends BaseParameter  {
  @JsonProperty("schema")
  private DataTypeSchema schema = null;

  public Parameter schema(DataTypeSchema schema) {
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

  public DataTypeSchema getSchema() {
    return schema;
  }

  public void setSchema(DataTypeSchema schema) {
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
    Parameter parameter = (Parameter) o;
    return Objects.equals(this.schema, parameter.schema) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Parameter {\n");
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

