package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.openeo.spring.model.DefaultOpenIDConnectClient;
import org.openeo.spring.model.Link;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * OpenIDConnectProvider
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-23T11:15:01.633381875+02:00[Europe/Rome]")
public class OpenIDConnectProvider   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("issuer")
  private URI issuer;

  @JsonProperty("scopes")
  @Valid
  private List<String> scopes = null;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("default_clients")
  @Valid
  private Set<DefaultOpenIDConnectClient> defaultClients = null;

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public OpenIDConnectProvider id(String id) {
    this.id = id;
    return this;
  }

  /**
   * A **unique** identifier for the OpenID Connect Provider to be as prefix for the Bearer token.
   * @return id
  */
  @ApiModelProperty(required = true, value = "A **unique** identifier for the OpenID Connect Provider to be as prefix for the Bearer token.")
  @NotNull

@Pattern(regexp="[\\d\\w]{1,20}") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OpenIDConnectProvider issuer(URI issuer) {
    this.issuer = issuer;
    return this;
  }

  /**
   * The [issuer location](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) (also referred to as 'authority' in some client libraries) is the URL of the OpenID Connect provider, which conforms to a set of rules: 1. After appending `/.well-known/openid-configuration` to the URL, a [HTTP/1.1 GET request](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest) to the concatenated URL MUST return a [OpenID Connect Discovery Configuration Response](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationResponse). The response provides all information required to authenticate using OpenID Connect. 2. The URL MUST NOT contain a terminating forward slash `/`.
   * @return issuer
  */
  @ApiModelProperty(example = "https://accounts.google.com", required = true, value = "The [issuer location](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) (also referred to as 'authority' in some client libraries) is the URL of the OpenID Connect provider, which conforms to a set of rules: 1. After appending `/.well-known/openid-configuration` to the URL, a [HTTP/1.1 GET request](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest) to the concatenated URL MUST return a [OpenID Connect Discovery Configuration Response](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationResponse). The response provides all information required to authenticate using OpenID Connect. 2. The URL MUST NOT contain a terminating forward slash `/`.")
  @NotNull

  @Valid

  public URI getIssuer() {
    return issuer;
  }

  public void setIssuer(URI issuer) {
    this.issuer = issuer;
  }

  public OpenIDConnectProvider scopes(List<String> scopes) {
    this.scopes = scopes;
    return this;
  }

  public OpenIDConnectProvider addScopesItem(String scopesItem) {
    if (this.scopes == null) {
      this.scopes = new ArrayList<>();
    }
    this.scopes.add(scopesItem);
    return this;
  }

  /**
   * A list of OpenID Connect scopes that the client MUST include when requesting authorization. If scopes are specified, the list MUST at least contain the `openid` scope.
   * @return scopes
  */
  @ApiModelProperty(value = "A list of OpenID Connect scopes that the client MUST include when requesting authorization. If scopes are specified, the list MUST at least contain the `openid` scope.")


  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  public OpenIDConnectProvider title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The name that is publicly shown in clients for this OpenID Connect provider.
   * @return title
  */
  @ApiModelProperty(required = true, value = "The name that is publicly shown in clients for this OpenID Connect provider.")
  @NotNull


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public OpenIDConnectProvider description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A description that explains how the authentication procedure works.  It should make clear how to register and get credentials. This should include instruction on setting up `client_id`, `client_secret` and `redirect_uri`.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(value = "A description that explains how the authentication procedure works.  It should make clear how to register and get credentials. This should include instruction on setting up `client_id`, `client_secret` and `redirect_uri`.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OpenIDConnectProvider defaultClients(Set<DefaultOpenIDConnectClient> defaultClients) {
    this.defaultClients = defaultClients;
    return this;
  }

  public OpenIDConnectProvider addDefaultClientsItem(DefaultOpenIDConnectClient defaultClientsItem) {
    if (this.defaultClients == null) {
      this.defaultClients = new LinkedHashSet<>();
    }
    this.defaultClients.add(defaultClientsItem);
    return this;
  }

  /**
   * List of default OpenID Connect clients that can be used by an openEO client for OpenID Connect based authentication.  A default OpenID Connect client is managed by the backend implementer. It MUST be configured to be usable without a client secret, which limits its applicability to OpenID Connect grant types like \"Authorization Code Grant with PKCE\" and \"Device Authorization Grant with PKCE\"  A default OpenID Connect client is provided without availability guarantees. The backend implementer CAN revoke, reset or update it any time. As such, openEO clients SHOULD NOT store or cache default OpenID Connect client information for long term usage. A default OpenID Connect client is intended to simplify authentication for novice users. For production use cases, it is RECOMMENDED to set up a dedicated OpenID Connect client.
   * @return defaultClients
  */
  @ApiModelProperty(value = "List of default OpenID Connect clients that can be used by an openEO client for OpenID Connect based authentication.  A default OpenID Connect client is managed by the backend implementer. It MUST be configured to be usable without a client secret, which limits its applicability to OpenID Connect grant types like \"Authorization Code Grant with PKCE\" and \"Device Authorization Grant with PKCE\"  A default OpenID Connect client is provided without availability guarantees. The backend implementer CAN revoke, reset or update it any time. As such, openEO clients SHOULD NOT store or cache default OpenID Connect client information for long term usage. A default OpenID Connect client is intended to simplify authentication for novice users. For production use cases, it is RECOMMENDED to set up a dedicated OpenID Connect client.")

  @Valid

  public Set<DefaultOpenIDConnectClient> getDefaultClients() {
    return defaultClients;
  }

  public void setDefaultClients(Set<DefaultOpenIDConnectClient> defaultClients) {
    this.defaultClients = defaultClients;
  }

  public OpenIDConnectProvider links(List<Link> links) {
    this.links = links;
    return this;
  }

  public OpenIDConnectProvider addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this provider, for example a help page or a page to register a new user account.  For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(value = "Links related to this provider, for example a help page or a page to register a new user account.  For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

  @Valid

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenIDConnectProvider openIDConnectProvider = (OpenIDConnectProvider) o;
    return Objects.equals(this.id, openIDConnectProvider.id) &&
        Objects.equals(this.issuer, openIDConnectProvider.issuer) &&
        Objects.equals(this.scopes, openIDConnectProvider.scopes) &&
        Objects.equals(this.title, openIDConnectProvider.title) &&
        Objects.equals(this.description, openIDConnectProvider.description) &&
        Objects.equals(this.defaultClients, openIDConnectProvider.defaultClients) &&
        Objects.equals(this.links, openIDConnectProvider.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, issuer, scopes, title, description, defaultClients, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenIDConnectProvider {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    issuer: ").append(toIndentedString(issuer)).append("\n");
    sb.append("    scopes: ").append(toIndentedString(scopes)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    defaultClients: ").append(toIndentedString(defaultClients)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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

