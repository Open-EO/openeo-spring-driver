package org.openeo.spring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * UdfProgrammingLanguage
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class UdfProgrammingLanguage extends UdfRuntime  {
  @JsonProperty("default")
  private String _default;

  @JsonProperty("versions")
  @Valid
  private Map<String, ProgrammingLanguageVersion> versions = new HashMap<>();

  @Override
public UdfProgrammingLanguage _default(String _default) {
    return this;
  }

  /**
   * The default version. MUST be one of the keys in the `versions` object.
   * @return _default
  */
  @Override
@ApiModelProperty(required = true, value = "The default version. MUST be one of the keys in the `versions` object.")
  @NotNull


  public String getDefault() {
    return _default;
  }

  @Override
public void setDefault(String _default) {
    this._default = _default;
  }

  public UdfProgrammingLanguage versions(Map<String, ProgrammingLanguageVersion> versions) {
    this.versions = versions;
    return this;
  }

  public UdfProgrammingLanguage putVersionsItem(String key, ProgrammingLanguageVersion versionsItem) {
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
    UdfProgrammingLanguage udfProgrammingLanguage = (UdfProgrammingLanguage) o;
    return Objects.equals(this._default, udfProgrammingLanguage._default) &&
        Objects.equals(this.versions, udfProgrammingLanguage.versions) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_default, versions, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UdfProgrammingLanguage {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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

