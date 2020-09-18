package org.openeo.spring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * ProgrammingLanguageVersion
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class ProgrammingLanguageVersion   {
  @JsonProperty("libraries")
  @Valid
  private Map<String, ProgrammingLanguageLibrary> libraries = new HashMap<>();

  public ProgrammingLanguageVersion libraries(Map<String, ProgrammingLanguageLibrary> libraries) {
    this.libraries = libraries;
    return this;
  }

  public ProgrammingLanguageVersion putLibrariesItem(String key, ProgrammingLanguageLibrary librariesItem) {
    this.libraries.put(key, librariesItem);
    return this;
  }

  /**
   * Map of installed libraries, modules, packages or extensions for the programming language. The names of them are used as the property keys.
   * @return libraries
  */
  @ApiModelProperty(required = true, value = "Map of installed libraries, modules, packages or extensions for the programming language. The names of them are used as the property keys.")
  @NotNull

  @Valid

  public Map<String, ProgrammingLanguageLibrary> getLibraries() {
    return libraries;
  }

  public void setLibraries(Map<String, ProgrammingLanguageLibrary> libraries) {
    this.libraries = libraries;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProgrammingLanguageVersion programmingLanguageVersion = (ProgrammingLanguageVersion) o;
    return Objects.equals(this.libraries, programmingLanguageVersion.libraries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(libraries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProgrammingLanguageVersion {\n");
    
    sb.append("    libraries: ").append(toIndentedString(libraries)).append("\n");
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

