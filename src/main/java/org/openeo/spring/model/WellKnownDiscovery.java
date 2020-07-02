package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.APIInstance;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * WellKnownDiscovery
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class WellKnownDiscovery   {
  @JsonProperty("versions")
  @Valid
  private List<APIInstance> versions = new ArrayList<>();

  public WellKnownDiscovery versions(List<APIInstance> versions) {
    this.versions = versions;
    return this;
  }

  public WellKnownDiscovery addVersionsItem(APIInstance versionsItem) {
    this.versions.add(versionsItem);
    return this;
  }

  /**
   * Get versions
   * @return versions
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<APIInstance> getVersions() {
    return versions;
  }

  public void setVersions(List<APIInstance> versions) {
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
    WellKnownDiscovery wellKnownDiscovery = (WellKnownDiscovery) o;
    return Objects.equals(this.versions, wellKnownDiscovery.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WellKnownDiscovery {\n");
    
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

