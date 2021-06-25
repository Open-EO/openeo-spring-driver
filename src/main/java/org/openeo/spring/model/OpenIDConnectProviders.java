package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.OpenIDConnectProvider;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * OpenIDConnectProviders
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-23T11:15:01.633381875+02:00[Europe/Rome]")
public class OpenIDConnectProviders   {
  @JsonProperty("providers")
  @Valid
  private List<OpenIDConnectProvider> providers = new ArrayList<>();

  public OpenIDConnectProviders providers(List<OpenIDConnectProvider> providers) {
    this.providers = providers;
    return this;
  }

  public OpenIDConnectProviders addProvidersItem(OpenIDConnectProvider providersItem) {
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
  public List<OpenIDConnectProvider> getProviders() {
    return providers;
  }

  public void setProviders(List<OpenIDConnectProvider> providers) {
    this.providers = providers;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenIDConnectProviders openIDConnectProviders = (OpenIDConnectProviders) o;
    return Objects.equals(this.providers, openIDConnectProviders.providers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(providers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenIDConnectProviders {\n");
    
    sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

