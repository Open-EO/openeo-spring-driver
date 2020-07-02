package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * File
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class File   {
  @JsonProperty("path")
  private String path;

  @JsonProperty("size")
  private Integer size;

  @JsonProperty("modified")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime modified;

  public File path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Path of the file, relative to the user's root directory. MUST NOT start with a slash and MUST NOT be url-encoded.
   * @return path
  */
  @ApiModelProperty(example = "folder/file.txt", required = true, value = "Path of the file, relative to the user's root directory. MUST NOT start with a slash and MUST NOT be url-encoded.")
  @NotNull


  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public File size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * File size in bytes.
   * @return size
  */
  @ApiModelProperty(example = "1024", value = "File size in bytes.")


  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public File modified(OffsetDateTime modified) {
    this.modified = modified;
    return this;
  }

  /**
   * Date and time the file has lastly been modified, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.
   * @return modified
  */
  @ApiModelProperty(example = "2018-01-03T10:55:29Z", value = "Date and time the file has lastly been modified, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.")

  @Valid

  public OffsetDateTime getModified() {
    return modified;
  }

  public void setModified(OffsetDateTime modified) {
    this.modified = modified;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    File file = (File) o;
    return Objects.equals(this.path, file.path) &&
        Objects.equals(this.size, file.size) &&
        Objects.equals(this.modified, file.modified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, size, modified);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class File {\n");
    
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    modified: ").append(toIndentedString(modified)).append("\n");
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

