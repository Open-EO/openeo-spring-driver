package org.openeo.spring.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.MapKey;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * BatchJobResult for items of type "Collection".
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
//@Entity
@Table(name = "job_results_collection")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchJobResultCollection extends BatchJobResult implements Serializable {

	private static final long serialVersionUID = -879934306104454217L;

    {
        // init section
        setType(AssetType.COLLECTION);
    }
    
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

    @JsonProperty("sci:citation")
    private String citation;

    @JsonProperty("providers")
    @Valid
    private List<Providers> providers = null;

    @JsonProperty("extent")
    private CollectionExtent extent;

    @JsonProperty("links")
    @Valid
    private List<Link> links = new ArrayList<>();

    @JsonProperty("cube:dimensions")
    @Valid
//    @JsonSerialize(using = AssetsSerializer.class)
//    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
//    @JoinTable(name = "result_asset_mapping", 
//      joinColumns = {@JoinColumn(name = "result_id", referencedColumnName = "id")},
//      inverseJoinColumns = {@JoinColumn(name = "asset_id", referencedColumnName = "href")})
    @MapKey(name = "href")
    private Map<String, Dimension> cubeColonDimensions = null;

    @JsonProperty("summaries")
    @Valid
    private CollectionSummaries summaries = null;

    @JsonProperty("assets")
    @Valid
    private Map<String, Asset> assets = null;

    @Override
    public void setType(AssetType type) {
        if (AssetType.COLLECTION != type) {
            throw new RuntimeException("Wrong type for STAC Collection: " + type);
        }
        super.setType(type);
    }

    public BatchJobResultCollection title(String title) {
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

    public BatchJobResultCollection description(String description) {
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

    public BatchJobResultCollection keywords(List<String> keywords) {
      this.keywords = keywords;
      return this;
    }

    public BatchJobResultCollection addKeywordsItem(String keywordsItem) {
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

    public BatchJobResultCollection version(String version) {
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

    public BatchJobResultCollection deprecated(Boolean deprecated) {
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

    public BatchJobResultCollection license(String license) {
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

    public BatchJobResultCollection citation(String citation) {
        this.citation = citation;
        return this;
    }


    @ApiModelProperty(example = "Copernicus Sentinel Data [2018]", required = false, value = "Citation of the data")
    @NotNull

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public BatchJobResultCollection providers(List<Providers> providers) {
      this.providers = providers;
      return this;
    }

    public BatchJobResultCollection addProvidersItem(Providers providersItem) {
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


    public List<Providers> getProviders() {
      return providers;
    }

    public void setProviders(List<Providers> providers) {
      this.providers = providers;
    }

    public BatchJobResultCollection extent(CollectionExtent extent) {
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

    public CollectionExtent getExtent() {
      return extent;
    }

    public void setExtent(CollectionExtent extent) {
      this.extent = extent;
    }

    @Override
    public BatchJobResultCollection links(List<Link> links) {
      this.links = links;
      return this;
    }

    @Override
    public BatchJobResultCollection addLinksItem(Link linksItem) {
      this.links.add(linksItem);
      return this;
    }

    /**
     * Links related to this collection. Could reference to licensing information, other meta data formats with additional information or a preview image. It is RECOMMENDED to provide links with the following `rel` (relation) types: 1. `root` and `parent`: URL to the data discovery endpoint at `/collections`. 2. `license`: A link to the license(s) should be specified if the `license` field is set to `proprietary` or `various`. 3. `example`: Links to examples of processes that use this collection. 4. `derived_from`: Allows linking to the data this collection was derived from. 5. `cite-as`: [DOI](https://www.doi.org/) links should be added. DOIs can also be  listed in the STAC fields `sci:doi` and `sci:publications`, see the [STAC scientific extension](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions/scientific) for more details. 6. `latest-version`: If a collection has been marked as deprecated, a link should point to the latest version of the collection. The relation types `predecessor-version` (link to older version) and `successor-version` (link to newer version) can also be used to show the relation between versions. 7. `alternate`: An alternative representation of the collection. For example, this could be the collection available through another catalog service such as OGC CSW, a human-readable HTML version or a metadata document following another standard such as ISO 19115 or DCAT. For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking) and the STAC specification for Collections.
     * @return links
    */
    @Override
    @ApiModelProperty(required = true, value = "Links related to this collection. Could reference to licensing information, other meta data formats with additional information or a preview image. It is RECOMMENDED to provide links with the following `rel` (relation) types: 1. `root` and `parent`: URL to the data discovery endpoint at `/collections`. 2. `license`: A link to the license(s) should be specified if the `license` field is set to `proprietary` or `various`. 3. `example`: Links to examples of processes that use this collection. 4. `derived_from`: Allows linking to the data this collection was derived from. 5. `cite-as`: [DOI](https://www.doi.org/) links should be added. DOIs can also be  listed in the STAC fields `sci:doi` and `sci:publications`, see the [STAC scientific extension](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions/scientific) for more details. 6. `latest-version`: If a collection has been marked as deprecated, a link should point to the latest version of the collection. The relation types `predecessor-version` (link to older version) and `successor-version` (link to newer version) can also be used to show the relation between versions. 7. `alternate`: An alternative representation of the collection. For example, this could be the collection available through another catalog service such as OGC CSW, a human-readable HTML version or a metadata document following another standard such as ISO 19115 or DCAT. For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking) and the STAC specification for Collections.")
    @NotNull

    @Valid

    public List<Link> getLinks() {
      return links;
    }

    @Override
    public void setLinks(List<Link> links) {
      this.links = links;
    }

    public BatchJobResultCollection cubeColonDimensions(Map<String, Dimension> cubeColonDimensions) {
      this.cubeColonDimensions = cubeColonDimensions;
      return this;
    }

    public BatchJobResultCollection putCubeColonDimensionsItem(String key, Dimension cubeColonDimensionsItem) {
      if (this.cubeColonDimensions == null) {
        this.cubeColonDimensions = new HashMap<>();
      }
      this.cubeColonDimensions.put(key, cubeColonDimensionsItem);
      return this;
    }

    /**
     * Uniquely named dimensions of the data cube.  The keys of the object are the dimension names. For interoperability, it is RECOMMENDED to use the following dimension names if there is only a single dimension with the specified criteria:  * `x` for the dimension of type `spatial` with the axis set to `x` * `y` for the dimension of type `spatial` with the axis set to `y` * `z` for the dimension of type `spatial` with the axis set to `z` * `t` for the dimension of type `temporal` * `bands` for dimensions of type `bands`  This property REQUIRES to add `datacube` to the list of `stac_extensions`.
     * @return cubeColonDimensions
    */
    @ApiModelProperty(value = "Uniquely named dimensions of the data cube.  The keys of the object are the dimension names. For interoperability, it is RECOMMENDED to use the following dimension names if there is only a single dimension with the specified criteria:  * `x` for the dimension of type `spatial` with the axis set to `x` * `y` for the dimension of type `spatial` with the axis set to `y` * `z` for the dimension of type `spatial` with the axis set to `z` * `t` for the dimension of type `temporal` * `bands` for dimensions of type `bands`  This property REQUIRES to add `datacube` to the list of `stac_extensions`.")

    @Valid

    public Map<String, Dimension> getCubeColonDimensions() {
      return cubeColonDimensions;
    }

    public void setCubeColonDimensions(Map<String, Dimension> cubeColonDimensions) {
      this.cubeColonDimensions = cubeColonDimensions;
    }

    public BatchJobResultCollection summaries(CollectionSummaries summaries) {
      this.summaries = summaries;
      return this;
    }

  //  public Collection putSummariesItem(String key, CollectionSummaries summariesItem) {
//      if (this.summaries == null) {
//        this.summaries = new HashMap<>();
//      }
//      this.summaries.put(key, summariesItem);
//      return this;
  //  }

    /**
     * Collection properties from STAC extensions (e.g. EO, SAR, Satellite or Scientific) or even custom extensions.  Summaries are either a unique set of all available values *or* statistics. Statistics by default only specify the range (minimum and maximum values), but can optionally be accompanied by additional statistical values. The range can specify the potential range of values, but it is recommended to be as precise as possible. The set of values must contain at least one element and it is strongly recommended to list all values. It is recommended to list as many properties as reasonable so that consumers get a full overview of the Collection. Properties that are covered by the Collection specification (e.g. `providers` and `license`) may not be repeated in the summaries.  Potential fields for the summaries can be found here:  * **[STAC Common Metadata](https://github.com/radiantearth/stac-spec/tree/v0.9.0/item-spec/common-metadata.md)**:   A list of commonly used fields throughout all domains * **[Content Extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions/README.md#list-of-content-extensions)**:   Domain-specific fields for domains such as EO, SAR and point clouds. * **Custom Properties**:   It is generally allowed to add custom fields.
     * @return summaries
    */
    @ApiModelProperty(value = "Collection properties from STAC extensions (e.g. EO, SAR, Satellite or Scientific) or even custom extensions.  Summaries are either a unique set of all available values *or* statistics. Statistics by default only specify the range (minimum and maximum values), but can optionally be accompanied by additional statistical values. The range can specify the potential range of values, but it is recommended to be as precise as possible. The set of values must contain at least one element and it is strongly recommended to list all values. It is recommended to list as many properties as reasonable so that consumers get a full overview of the Collection. Properties that are covered by the Collection specification (e.g. `providers` and `license`) may not be repeated in the summaries.  Potential fields for the summaries can be found here:  * **[STAC Common Metadata](https://github.com/radiantearth/stac-spec/tree/v0.9.0/item-spec/common-metadata.md)**:   A list of commonly used fields throughout all domains * **[Content Extensions](https://github.com/radiantearth/stac-spec/tree/v0.9.0/extensions/README.md#list-of-content-extensions)**:   Domain-specific fields for domains such as EO, SAR and point clouds. * **Custom Properties**:   It is generally allowed to add custom fields.")

    @Valid

    public CollectionSummaries getSummaries() {
      return summaries;
    }

    public void setSummaries(CollectionSummaries summaries) {
      this.summaries = summaries;
    }

    @Override
    public BatchJobResultCollection assets(Map<String, Asset> assets) {
      this.assets = assets;
      return this;
    }

    @Override
    public BatchJobResultCollection putAssetsItem(String key, Asset assetsItem) {
      if (this.assets == null) {
        this.assets = new HashMap<>();
      }
      this.assets.put(key, assetsItem);
      return this;
    }

    /**
     * Dictionary of asset objects for data that can be downloaded, each with a unique key. The keys MAY be used by clients as file names. Implementing this property REQUIRES to add `collection-assets` to the list of `stac_extensions`.
     * @return assets
    */
    @Override
    @ApiModelProperty(value = "Dictionary of asset objects for data that can be downloaded, each with a unique key. The keys MAY be used by clients as file names. Implementing this property REQUIRES to add `collection-assets` to the list of `stac_extensions`.")

    @Valid

    public Map<String, Asset> getAssets() {
      return assets;
    }

    @Override
    public void setAssets(Map<String, Asset> assets) {
      this.assets = assets;
    }


    @Override
    public boolean equals(java.lang.Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      BatchJobResultCollection collection = (BatchJobResultCollection) o;

      return super.equals(o) &&
          Objects.equals(this.title, collection.title) &&
          Objects.equals(this.description, collection.description) &&
          Objects.equals(this.keywords, collection.keywords) &&
          Objects.equals(this.version, collection.version) &&
          Objects.equals(this.deprecated, collection.deprecated) &&
          Objects.equals(this.license, collection.license) &&
          Objects.equals(this.citation, collection.citation) &&
          Objects.equals(this.providers, collection.providers) &&
          Objects.equals(this.extent, collection.extent) &&
          Objects.equals(this.links, collection.links) &&
          Objects.equals(this.cubeColonDimensions, collection.cubeColonDimensions) &&
          Objects.equals(this.summaries, collection.summaries) &&
          Objects.equals(this.assets, collection.assets);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), title, description, keywords, version, deprecated, citation, license, providers, extent, links, cubeColonDimensions, summaries, assets);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class BatchJobResult-Collection {\n");
      sb.append("    stacVersion: ").append(toIndentedString(getStacVersion())).append("\n");
      sb.append("    stacExtensions: ").append(toIndentedString(getStacExtensions())).append("\n");
      sb.append("    id: ").append(toIndentedString(getId())).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    description: ").append(toIndentedString(description)).append("\n");
      sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
      sb.append("    version: ").append(toIndentedString(version)).append("\n");
      sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
      sb.append("    license: ").append(toIndentedString(license)).append("\n");
      sb.append("    citation: ").append(toIndentedString(citation)).append("\n");
      sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
      sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
      sb.append("    links: ").append(toIndentedString(links)).append("\n");
      sb.append("    cubeColonDimensions: ").append(toIndentedString(cubeColonDimensions)).append("\n");
      sb.append("    summaries: ").append(toIndentedString(summaries)).append("\n");
      sb.append("    assets: ").append(toIndentedString(assets)).append("\n");
      sb.append("}");
      return sb.toString();
    }
}
