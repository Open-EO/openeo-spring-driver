package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.OpenIDProviderListProviders;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * OpenIDProviderList
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class OpenIDProviderList   {
  @JsonProperty("providers")
  @Valid
  private List<OpenIDProviderListProviders> providers = new ArrayList<>();

  public OpenIDProviderList providers(List<OpenIDProviderListProviders> providers) {
    this.providers = providers;
    return this;
  }

  public OpenIDProviderList addProvidersItem(OpenIDProviderListProviders providersItem) {
    this.providers.add(providersItem);
    return this;
  }

  /**
   * Get providers
   * @return providers
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid
@Size(min=1) 
  public List<OpenIDProviderListProviders> getProviders() {
    return providers;
  }

  public void setProviders(List<OpenIDProviderListProviders> providers) {
    this.providers = providers;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenIDProviderList openIDProviderList = (OpenIDProviderList) o;
    return Objects.equals(this.providers, openIDProviderList.providers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(providers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenIDProviderList {\n");
    
    sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
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

