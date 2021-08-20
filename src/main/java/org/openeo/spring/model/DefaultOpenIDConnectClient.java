package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DefaultOpenIDConnectClient
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-23T11:15:01.633381875+02:00[Europe/Rome]")
public class DefaultOpenIDConnectClient   {
  @JsonProperty("id")
  private String id;

  /**
   * Gets or Sets grantTypes
   */
  public enum GrantTypesEnum {
    IMPLICIT("implicit"),
    
    AUTHORIZATION_CODE_PKCE("authorization_code+pkce"),
    
    URN_IETF_PARAMS_OAUTH_GRANT_TYPE_DEVICE_CODE_PKCE("urn:ietf:params:oauth:grant-type:device_code+pkce"),
    
    URN_IETF_PARAMS_OAUTH_GRANT_TYPE_DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code"),
    
    REFRESH_TOKEN("refresh_token");

    private String value;

    GrantTypesEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static GrantTypesEnum fromValue(String value) {
      for (GrantTypesEnum b : GrantTypesEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("grant_types")
  @Valid
  private Set<GrantTypesEnum> grantTypes = new LinkedHashSet<>();

  @JsonProperty("redirect_urls")
  @Valid
  private Set<URI> redirectUrls = null;

  public DefaultOpenIDConnectClient id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The OpenID Connect Client ID to be used in the authentication procedure.
   * @return id
  */
  @ApiModelProperty(required = true, value = "The OpenID Connect Client ID to be used in the authentication procedure.")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DefaultOpenIDConnectClient grantTypes(Set<GrantTypesEnum> grantTypes) {
    this.grantTypes = grantTypes;
    return this;
  }

  public DefaultOpenIDConnectClient addGrantTypesItem(GrantTypesEnum grantTypesItem) {
    this.grantTypes.add(grantTypesItem);
    return this;
  }

  /**
   * List of authorization grant types (flows) supported by the OpenID Connect client. A grant type descriptor consist of a OAuth 2.0 grant type, with an additional `+pkce` suffix when the grant type should be used with the PKCE extension as defined in [RFC 7636](https://www.rfc-editor.org/rfc/rfc7636.html).  Allowed values: - `implicit`: Implicit Grant as specified in [RFC 6749, sec. 1.3.2](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.3.2) - `authorization_code+pkce`: Authorization Code Grant as specified in [RFC 6749, sec. 1.3.1](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.3.1), with PKCE extension. - `urn:ietf:params:oauth:grant-type:device_code+pkce`: Device Authorization Grant (aka Device Code Flow) as specified in [RFC 8628](https://www.rfc-editor.org/rfc/rfc8628.html), with PKCE extension. Note that the combination of this grant with the PKCE extension is *not standardized* yet. - `refresh_token`: Refresh Token as specified in [RFC 6749, sec. 1.5](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.5)
   * @return grantTypes
  */
  @ApiModelProperty(required = true, value = "List of authorization grant types (flows) supported by the OpenID Connect client. A grant type descriptor consist of a OAuth 2.0 grant type, with an additional `+pkce` suffix when the grant type should be used with the PKCE extension as defined in [RFC 7636](https://www.rfc-editor.org/rfc/rfc7636.html).  Allowed values: - `implicit`: Implicit Grant as specified in [RFC 6749, sec. 1.3.2](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.3.2) - `authorization_code+pkce`: Authorization Code Grant as specified in [RFC 6749, sec. 1.3.1](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.3.1), with PKCE extension. - `urn:ietf:params:oauth:grant-type:device_code+pkce`: Device Authorization Grant (aka Device Code Flow) as specified in [RFC 8628](https://www.rfc-editor.org/rfc/rfc8628.html), with PKCE extension. Note that the combination of this grant with the PKCE extension is *not standardized* yet. - `refresh_token`: Refresh Token as specified in [RFC 6749, sec. 1.5](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.5)")
  @NotNull

@Size(min=1) 
  public Set<GrantTypesEnum> getGrantTypes() {
    return grantTypes;
  }

  public void setGrantTypes(Set<GrantTypesEnum> grantTypes) {
    this.grantTypes = grantTypes;
  }

  public DefaultOpenIDConnectClient redirectUrls(Set<URI> redirectUrls) {
    this.redirectUrls = redirectUrls;
    return this;
  }

  public DefaultOpenIDConnectClient addRedirectUrlsItem(URI redirectUrlsItem) {
    if (this.redirectUrls == null) {
      this.redirectUrls = new LinkedHashSet<>();
    }
    this.redirectUrls.add(redirectUrlsItem);
    return this;
  }

  /**
   * List of redirect URLs that are whitelisted by the OpenID Connect client. Redirect URLs MUST be provided when the OpenID Connect client supports the `implicit` or `authorization_code+pkce` authorization flows.
   * @return redirectUrls
  */
  @ApiModelProperty(value = "List of redirect URLs that are whitelisted by the OpenID Connect client. Redirect URLs MUST be provided when the OpenID Connect client supports the `implicit` or `authorization_code+pkce` authorization flows.")

  @Valid

  public Set<URI> getRedirectUrls() {
    return redirectUrls;
  }

  public void setRedirectUrls(Set<URI> redirectUrls) {
    this.redirectUrls = redirectUrls;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultOpenIDConnectClient defaultOpenIDConnectClient = (DefaultOpenIDConnectClient) o;
    return Objects.equals(this.id, defaultOpenIDConnectClient.id) &&
        Objects.equals(this.grantTypes, defaultOpenIDConnectClient.grantTypes) &&
        Objects.equals(this.redirectUrls, defaultOpenIDConnectClient.redirectUrls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, grantTypes, redirectUrls);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DefaultOpenIDConnectClient {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    grantTypes: ").append(toIndentedString(grantTypes)).append("\n");
    sb.append("    redirectUrls: ").append(toIndentedString(redirectUrls)).append("\n");
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

