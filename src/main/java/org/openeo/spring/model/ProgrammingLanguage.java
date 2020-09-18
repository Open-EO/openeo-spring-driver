package org.openeo.spring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.openapitools.jackson.nullable.JsonNullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * ProgrammingLanguage
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ProgrammingLanguage   {
  @JsonProperty("default")
  private JsonNullable<Object> _default = JsonNullable.undefined();

  @JsonProperty("versions")
  @Valid
  private Map<String, ProgrammingLanguageVersion> versions = new HashMap<>();

  public ProgrammingLanguage _default(Object _default) {
    this._default = JsonNullable.of(_default);
    return this;
  }

  /**
   * The default version. MUST be one of the keys in the `versions` object.
   * @return _default
  */
  @ApiModelProperty(required = true, value = "The default version. MUST be one of the keys in the `versions` object.")
  @NotNull


  public JsonNullable<Object> getDefault() {
    return _default;
  }

  public void setDefault(JsonNullable<Object> _default) {
    this._default = _default;
  }

  public ProgrammingLanguage versions(Map<String, ProgrammingLanguageVersion> versions) {
    this.versions = versions;
    return this;
  }

  public ProgrammingLanguage putVersionsItem(String key, ProgrammingLanguageVersion versionsItem) {
    this.versions.put(key, versionsItem);
    return this;
  }

  /**
   * Versions available for the programming language.
   * @return versions
  */
  @ApiModelProperty(required = true, value = "Versions available for the programming language.")
  @NotNull

  @Valid

  public Map<String, ProgrammingLanguageVersion> getVersions() {
    return versions;
  }

  public void setVersions(Map<String, ProgrammingLanguageVersion> versions) {
    this.versions = versions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProgrammingLanguage programmingLanguage = (ProgrammingLanguage) o;
    return Objects.equals(this._default, programmingLanguage._default) &&
        Objects.equals(this.versions, programmingLanguage.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_default, versions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProgrammingLanguage {\n");
    
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
    sb.append("    versions: ").append(toIndentedString(versions)).append("\n");
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

