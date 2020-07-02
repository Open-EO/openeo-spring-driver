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
 * HTTPBasicAccessToken
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
public class HTTPBasicAccessToken   {
  @JsonProperty("access_token")
  private String accessToken;

  public HTTPBasicAccessToken accessToken(String accessToken) {
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
    HTTPBasicAccessToken htTPBasicAccessToken = (HTTPBasicAccessToken) o;
    return Objects.equals(this.accessToken, htTPBasicAccessToken.accessToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HTTPBasicAccessToken {\n");
    
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

