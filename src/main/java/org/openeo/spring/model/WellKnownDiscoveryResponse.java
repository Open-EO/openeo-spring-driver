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
 * WellKnownDiscoveryResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class WellKnownDiscoveryResponse   {
  @JsonProperty("versions")
  @Valid
  private List<APIInstance> versions = new ArrayList<>();

  public WellKnownDiscoveryResponse versions(List<APIInstance> versions) {
    this.versions = versions;
    return this;
  }

  public WellKnownDiscoveryResponse addVersionsItem(APIInstance versionsItem) {
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
    WellKnownDiscoveryResponse wellKnownDiscoveryResponse = (WellKnownDiscoveryResponse) o;
    return Objects.equals(this.versions, wellKnownDiscoveryResponse.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WellKnownDiscoveryResponse {\n");
    
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

