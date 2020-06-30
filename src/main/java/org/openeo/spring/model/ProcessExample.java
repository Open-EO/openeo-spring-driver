package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openeo.spring.model.ProcessArgumentValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ProcessExample
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class ProcessExample   {
  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("arguments")
  @Valid
  private Map<String, ProcessArgumentValue> arguments = new HashMap<>();

  @JsonProperty("returns")
  private JsonNullable<Object> returns = JsonNullable.undefined();

  public ProcessExample title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A title for the example.
   * @return title
  */
  @ApiModelProperty(value = "A title for the example.")


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ProcessExample description(String description) {
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

  public ProcessExample arguments(Map<String, ProcessArgumentValue> arguments) {
    this.arguments = arguments;
    return this;
  }

  public ProcessExample putArgumentsItem(String key, ProcessArgumentValue argumentsItem) {
    this.arguments.put(key, argumentsItem);
    return this;
  }

  /**
   * Get arguments
   * @return arguments
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public Map<String, ProcessArgumentValue> getArguments() {
    return arguments;
  }

  public void setArguments(Map<String, ProcessArgumentValue> arguments) {
    this.arguments = arguments;
  }

  public ProcessExample returns(Object returns) {
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
    ProcessExample processExample = (ProcessExample) o;
    return Objects.equals(this.title, processExample.title) &&
        Objects.equals(this.description, processExample.description) &&
        Objects.equals(this.arguments, processExample.arguments) &&
        Objects.equals(this.returns, processExample.returns);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, arguments, returns);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessExample {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    arguments: ").append(toIndentedString(arguments)).append("\n");
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

