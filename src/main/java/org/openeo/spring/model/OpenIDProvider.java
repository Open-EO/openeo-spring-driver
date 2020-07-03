package org.openeo.spring.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * OpenIDProvider
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class OpenIDProvider   {
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

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public OpenIDProvider id(String id) {
    this.id = id;
    return this;
  }

  /**
   * A **unique** identifier for the OpenID Provider to be as prefix for the Bearer token.
   * @return id
  */
  @ApiModelProperty(required = true, value = "A **unique** identifier for the OpenID Provider to be as prefix for the Bearer token.")
  @NotNull

@Pattern(regexp="[\\d\\w]{1,20}") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OpenIDProvider issuer(URI issuer) {
    this.issuer = issuer;
    return this;
  }

  /**
   * The [issuer location](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) (also referred to as 'authority' in some client libraries) is the URL of the OpenID provider, which conforms to a set of rules: 1. After appending `/.well-known/openid-configuration` to the URL, a [HTTP/1.1 GET request](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest) to the concatenated URL must return a [OpenID Discovery Configuration Response](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationResponse). The response provides all information required to authenticate using OpenID Connect. 2. The URL MUST NOT contain a terminating forward slash `/`.
   * @return issuer
  */
  @ApiModelProperty(example = "https://accounts.google.com", required = true, value = "The [issuer location](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) (also referred to as 'authority' in some client libraries) is the URL of the OpenID provider, which conforms to a set of rules: 1. After appending `/.well-known/openid-configuration` to the URL, a [HTTP/1.1 GET request](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest) to the concatenated URL must return a [OpenID Discovery Configuration Response](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationResponse). The response provides all information required to authenticate using OpenID Connect. 2. The URL MUST NOT contain a terminating forward slash `/`.")
  @NotNull

  @Valid

  public URI getIssuer() {
    return issuer;
  }

  public void setIssuer(URI issuer) {
    this.issuer = issuer;
  }

  public OpenIDProvider scopes(List<String> scopes) {
    this.scopes = scopes;
    return this;
  }

  public OpenIDProvider addScopesItem(String scopesItem) {
    if (this.scopes == null) {
      this.scopes = new ArrayList<>();
    }
    this.scopes.add(scopesItem);
    return this;
  }

  /**
   * A list of OpenID Connect scopes that the client MUST use. If scopes are specified, the list MUST at least contain the `openid` scope.
   * @return scopes
  */
  @ApiModelProperty(value = "A list of OpenID Connect scopes that the client MUST use. If scopes are specified, the list MUST at least contain the `openid` scope.")


  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  public OpenIDProvider title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The name that is publicly shown in clients for this OpenID provider.
   * @return title
  */
  @ApiModelProperty(required = true, value = "The name that is publicly shown in clients for this OpenID provider.")
  @NotNull


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public OpenIDProvider description(String description) {
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

  public OpenIDProvider links(List<Link> links) {
    this.links = links;
    return this;
  }

  public OpenIDProvider addLinksItem(Link linksItem) {
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
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenIDProvider openIDProvider = (OpenIDProvider) o;
    return Objects.equals(this.id, openIDProvider.id) &&
        Objects.equals(this.issuer, openIDProvider.issuer) &&
        Objects.equals(this.scopes, openIDProvider.scopes) &&
        Objects.equals(this.title, openIDProvider.title) &&
        Objects.equals(this.description, openIDProvider.description) &&
        Objects.equals(this.links, openIDProvider.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, issuer, scopes, title, description, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenIDProvider {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    issuer: ").append(toIndentedString(issuer)).append("\n");
    sb.append("    scopes: ").append(toIndentedString(scopes)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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

