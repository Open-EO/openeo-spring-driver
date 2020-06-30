package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * HTTPBasicAuthenticationResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class HTTPBasicAuthenticationResponse   {
  @JsonProperty("access_token")
  private String accessToken;

  public HTTPBasicAuthenticationResponse accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * The access token (without `basic//` prefix) to be used in the Bearer token for authorization in subsequent API calls.
   * @return accessToken
  */
  @ApiModelProperty(example = "b34ba2bdf9ac9ee1", required = true, value = "The access token (without `basic//` prefix) to be used in the Bearer token for authorization in subsequent API calls.")
  @NotNull


  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HTTPBasicAuthenticationResponse htTPBasicAuthenticationResponse = (HTTPBasicAuthenticationResponse) o;
    return Objects.equals(this.accessToken, htTPBasicAuthenticationResponse.accessToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HTTPBasicAuthenticationResponse {\n");
    
    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
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

