package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openeo.spring.model.DataTypeSchema;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The data that is returned from this process.
 */
@ApiModel(description = "The data that is returned from this process.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ProcessReturnValue   {
  @JsonProperty("description")
  private String description;

  @JsonProperty("schema")
  private DataTypeSchema schema = null;

  public ProcessReturnValue description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```
   * @return description
  */
  @ApiModelProperty(value = "Detailed description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProcessReturnValue schema(DataTypeSchema schema) {
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
    ProcessReturnValue processReturnValue = (ProcessReturnValue) o;
    return Objects.equals(this.description, processReturnValue.description) &&
        Objects.equals(this.schema, processReturnValue.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, schema);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessReturnValue {\n");
    
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

