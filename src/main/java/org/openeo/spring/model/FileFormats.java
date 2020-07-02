package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openeo.spring.model.FileFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FileFormats
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class FileFormats   {
  @JsonProperty("input")
  @Valid
  private Map<String, FileFormat> input = new HashMap<>();

  @JsonProperty("output")
  @Valid
  private Map<String, FileFormat> output = new HashMap<>();

  public FileFormats input(Map<String, FileFormat> input) {
    this.input = input;
    return this;
  }

  public FileFormats putInputItem(String key, FileFormat inputItem) {
    this.input.put(key, inputItem);
    return this;
  }

  /**
   * Map of supported input file formats, i.e. file formats a back-end can **read** from. The property keys are the file format names that are used by clients and users, for example in process graphs.
   * @return input
  */
  @ApiModelProperty(required = true, value = "Map of supported input file formats, i.e. file formats a back-end can **read** from. The property keys are the file format names that are used by clients and users, for example in process graphs.")
  @NotNull

  @Valid

  public Map<String, FileFormat> getInput() {
    return input;
  }

  public void setInput(Map<String, FileFormat> input) {
    this.input = input;
  }

  public FileFormats output(Map<String, FileFormat> output) {
    this.output = output;
    return this;
  }

  public FileFormats putOutputItem(String key, FileFormat outputItem) {
    this.output.put(key, outputItem);
    return this;
  }

  /**
   * Map of supported output file formats, i.e. file formats a back-end can **write** to. The property keys are the file format names that are used by clients and users, for example in process graphs.
   * @return output
  */
  @ApiModelProperty(required = true, value = "Map of supported output file formats, i.e. file formats a back-end can **write** to. The property keys are the file format names that are used by clients and users, for example in process graphs.")
  @NotNull

  @Valid

  public Map<String, FileFormat> getOutput() {
    return output;
  }

  public void setOutput(Map<String, FileFormat> output) {
    this.output = output;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileFormats fileFormats = (FileFormats) o;
    return Objects.equals(this.input, fileFormats.input) &&
        Objects.equals(this.output, fileFormats.output);
  }

  @Override
  public int hashCode() {
    return Objects.hash(input, output);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileFormats {\n");
    
    sb.append("    input: ").append(toIndentedString(input)).append("\n");
    sb.append("    output: ").append(toIndentedString(output)).append("\n");
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

