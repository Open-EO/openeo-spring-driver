package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.openeo.spring.model.AnyOfURIstring;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.STACCollectionExtent;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Collection
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T15:12:47.411+02:00[Europe/Rome]")
public class Collection   {
  @JsonProperty("stac_version")
  private String stacVersion;

  @JsonProperty("stac_extensions")
  @Valid
  private Set<AnyOfURIstring> stacExtensions = null;

  @JsonProperty("id")
  private String id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("keywords")
  @Valid
  private List<String> keywords = null;

  @JsonProperty("version")
  private String version;

  @JsonProperty("deprecated")
  private Boolean deprecated = false;

  @JsonProperty("license")
  private String license;

  @JsonProperty("providers")
  @Valid
  private List<Object> providers = null;

  @JsonProperty("extent")
  private STACCollectionExtent extent;

  @JsonProperty("links")
  @Valid
  private List<Link> links = new ArrayList<>();

  public Collection stacVersion(String stacVersion) {
    this.stacVersion = stacVersion;
    return this;
  }

  /**
   * The [version of the STAC specification](https://github.com/radiantearth/stac-spec/releases), which MAY not be equal to the [STAC API version](#section/STAC). Supports versions 0.9.x and 1.x.x.
   * @return stacVersion
  */
  @ApiModelProperty(required = true, value = "The [version of the STAC specification](https://github.com/radiantearth/stac-spec/releases), which MAY not be equal to the [STAC API version](#section/STAC). Supports versions 0.9.x and 1.x.x.")
  @NotNull


  public String getStacVersion() {
    return stacVersion;
  }

  public void setStacVersion(String stacVersion) {
    this.stacVersion = stacVersion;
  }

  public Collection stacExtensions(Set<AnyOfURIstring> stacExtensions) {
    this.stacExtensions = stacExtensions;
    return this;
  }

  public Collection addStacExtensionsItem(AnyOfURIstring stacExtensionsItem) {
    if (this.stacExtensions == null) {
      this.stacExtensions = new LinkedHashSet<>();
    }
    this.stacExtensions.add(stacExtensionsItem);
    return this;
  }

  /**
   * A list of implemented STAC extensions. The list contains URLs to the JSON Schema files it can be validated against. For official extensions, a \"shortcut\" can be used. This means you can specify the folder name of the extension in the STAC repository, for example `sar` for the SAR extension. If the versions of the extension and the collection diverge, you can specify the URL of the JSON schema file.
   * @return stacExtensions
  */
  @ApiModelProperty(value = "A list of implemented STAC extensions. The list contains URLs to the JSON Schema files it can be validated against. For official extensions, a \"shortcut\" can be used. This means you can specify the folder name of the extension in the STAC repository, for example `sar` for the SAR extension. If the versions of the extension and the collection diverge, you can specify the URL of the JSON schema file.")

  @Valid

  public Set<AnyOfURIstring> getStacExtensions() {
    return stacExtensions;
  }

  public void setStacExtensions(Set<AnyOfURIstring> stacExtensions) {
    this.stacExtensions = stacExtensions;
  }

  public Collection id(String id) {
    this.id = id;
    return this;
  }

  /**
   * A unique identifier for the collection, which MUST match the specified pattern.
   * @return id
  */
  @ApiModelProperty(example = "Sentinel-2A", required = true, value = "A unique identifier for the collection, which MUST match the specified pattern.")
  @NotNull

@Pattern(regexp="^[\\w\\-\\.~/]+$") 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Collection title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A short descriptive one-line title for the collection.
   * @return title
  */
  @ApiModelProperty(value = "A short descriptive one-line title for the collection.")


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Collection description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed multi-line description to explain the collection.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(required = true, value = "Detailed multi-line description to explain the collection.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")
  @NotNull


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Collection keywords(List<String> keywords) {
    this.keywords = keywords;
    return this;
  }

  public Collection addKeywordsItem(String keywordsItem) {
    if (this.keywords == null) {
      this.keywords = new ArrayList<>();
    }
    this.keywords.add(keywordsItem);
    return this;
  }

  /**
   * List of keywords describing the collection.
   * @return keywords
  */
  @ApiModelProperty(value = "List of keywords describing the collection.")


  public List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }

  public Collection version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Version of the collection.  This property REQUIRES to add `version` to the list of `stac_extensions`.
   * @return version
  */
  @ApiModelProperty(value = "Version of the collection.  This property REQUIRES to add `version` to the list of `stac_extensions`.")


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Collection deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  /**
   * Specifies that the collection is deprecated with the potential to be removed. It should be transitioned out of usage as soon as possible and users should refrain from using it in new projects.  A link with relation type `latest-version` SHOULD be added to the links and MUST refer to the collection that can be used instead.  This property REQUIRES to add `version` to the list of `stac_extensions`.
   * @return deprecated
  */
  @ApiModelProperty(value = "Specifies that the collection is deprecated with the potential to be removed. It should be transitioned out of usage as soon as possible and users should refrain from using it in new projects.  A link with relation type `latest-version` SHOULD be added to the links and MUST refer to the collection that can be used instead.  This property REQUIRES to add `version` to the list of `stac_extensions`.")


  public Boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
  }

  public Collection license(String license) {
    this.license = license;
    return this;
  }

  /**
   * License(s) of the data as a SPDX [License identifier](https://spdx.org/licenses/). Alternatively, use `proprietary` if the license is not on the SPDX license list or `various` if multiple licenses apply. In these two cases links to the license texts SHOULD be added, see the `license` link relation type.  Non-SPDX licenses SHOULD add a link to the license text with the `license` relation in the links section. The license text MUST NOT be provided as a value of this field. If there is no public license URL available, it is RECOMMENDED to host the license text and link to it.
   * @return license
  */
  @ApiModelProperty(example = "Apache-2.0", required = true, value = "License(s) of the data as a SPDX [License identifier](https://spdx.org/licenses/). Alternatively, use `proprietary` if the license is not on the SPDX license list or `various` if multiple licenses apply. In these two cases links to the license texts SHOULD be added, see the `license` link relation type.  Non-SPDX licenses SHOULD add a link to the license text with the `license` relation in the links section. The license text MUST NOT be provided as a value of this field. If there is no public license URL available, it is RECOMMENDED to host the license text and link to it.")
  @NotNull


  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public Collection providers(List<Object> providers) {
    this.providers = providers;
    return this;
  }

  public Collection addProvidersItem(Object providersItem) {
    if (this.providers == null) {
      this.providers = new ArrayList<>();
    }
    this.providers.add(providersItem);
    return this;
  }

  /**
   * A list of providers, which may include all organizations capturing or processing the data or the hosting provider. Providers should be listed in chronological order with the most recent provider being the last element of the list.
   * @return providers
  */
  @ApiModelProperty(value = "A list of providers, which may include all organizations capturing or processing the data or the hosting provider. Providers should be listed in chronological order with the most recent provider being the last element of the list.")


  public List<Object> getProviders() {
    return providers;
  }

  public void setProviders(List<Object> providers) {
    this.providers = providers;
  }

  public Collection extent(STACCollectionExtent extent) {
    this.extent = extent;
    return this;
  }

  /**
   * Get extent
   * @return extent
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public STACCollectionExtent getExtent() {
    return extent;
  }

  public void setExtent(STACCollectionExtent extent) {
    this.extent = extent;
  }

  public Collection links(List<Link> links) {
    this.links = links;
    return this;
  }

  public Collection addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to this collection. Could reference to licensing information, other meta data formats with additional information or a preview image. It is RECOMMENDED to provide links with the following `rel` (relation) types: 1. `root` and `parent`: URL to the data discovery endpoint at `/collections`. 2. `license`: A link to the license(s) should be specified if the `license` field is set to `proprietary` or `various`. 3. `example`: Links to examples of processes that use this collection. 4. `derived_from`: Allows linking to the data this collection was derived from. 5. `cite-as`: [DOI](https://www.doi.org/) links should be added. DOIs can also be  listed in the STAC fields `sci:doi` and `sci:publications`, see the [STAC scientific extension](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions/scientific) for more details. 6. `latest-version`: If a collection has been marked as deprecated, a link should point to the latest version of the collection. The relation types `predecessor-version` (link to older version) and `successor-version` (link to newer version) can also be used to show the relation between versions. 7. `alternate`: An alternative representation of the collection. For example, this could be the collection available through another catalog service such as OGC CSW, a human-readable HTML version or a metadata document following another standard such as ISO 19115 or DCAT. For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking) and the STAC specification for Collections.
   * @return links
  */
  @ApiModelProperty(required = true, value = "Links related to this collection. Could reference to licensing information, other meta data formats with additional information or a preview image. It is RECOMMENDED to provide links with the following `rel` (relation) types: 1. `root` and `parent`: URL to the data discovery endpoint at `/collections`. 2. `license`: A link to the license(s) should be specified if the `license` field is set to `proprietary` or `various`. 3. `example`: Links to examples of processes that use this collection. 4. `derived_from`: Allows linking to the data this collection was derived from. 5. `cite-as`: [DOI](https://www.doi.org/) links should be added. DOIs can also be  listed in the STAC fields `sci:doi` and `sci:publications`, see the [STAC scientific extension](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions/scientific) for more details. 6. `latest-version`: If a collection has been marked as deprecated, a link should point to the latest version of the collection. The relation types `predecessor-version` (link to older version) and `successor-version` (link to newer version) can also be used to show the relation between versions. 7. `alternate`: An alternative representation of the collection. For example, this could be the collection available through another catalog service such as OGC CSW, a human-readable HTML version or a metadata document following another standard such as ISO 19115 or DCAT. For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking) and the STAC specification for Collections.")
  @NotNull

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
    Collection collection = (Collection) o;
    return Objects.equals(this.stacVersion, collection.stacVersion) &&
        Objects.equals(this.stacExtensions, collection.stacExtensions) &&
        Objects.equals(this.id, collection.id) &&
        Objects.equals(this.title, collection.title) &&
        Objects.equals(this.description, collection.description) &&
        Objects.equals(this.keywords, collection.keywords) &&
        Objects.equals(this.version, collection.version) &&
        Objects.equals(this.deprecated, collection.deprecated) &&
        Objects.equals(this.license, collection.license) &&
        Objects.equals(this.providers, collection.providers) &&
        Objects.equals(this.extent, collection.extent) &&
        Objects.equals(this.links, collection.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stacVersion, stacExtensions, id, title, description, keywords, version, deprecated, license, providers, extent, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Collection {\n");
    
    sb.append("    stacVersion: ").append(toIndentedString(stacVersion)).append("\n");
    sb.append("    stacExtensions: ").append(toIndentedString(stacExtensions)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
    sb.append("    license: ").append(toIndentedString(license)).append("\n");
    sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
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

