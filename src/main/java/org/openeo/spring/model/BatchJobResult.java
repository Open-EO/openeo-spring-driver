package org.openeo.spring.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.json.JSONObject;
import org.openeo.spring.json.AssetsSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

/**
 * Abstract BatchJobResult.
 * @see <a href="https://api.openeo.org/#tag/Batch-Jobs/operation/list-results">List batch job results</a>
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
// TODO FIXME instead of polymorphic BatchJobResult, rely on existing Feature/Collection model classes
@MappedSuperclass
-> see javadoc of MappedSuperclass
public abstract class BatchJobResult {

	@JsonProperty("stac_version")
	private String stacVersion;

	@JsonProperty("stac_extensions")
	@Valid
	@Embedded
	private Set<String> stacExtensions = new HashSet<String>();
	
	@Id
	@JsonProperty("id")
	private String id;

	@JsonProperty("type")
	@Enumerated
	@Column(name = "asset_type")
	private AssetType type;

	@JsonProperty("assets")
	@JsonSerialize(using = AssetsSerializer.class)
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "result_asset_mapping", 
      joinColumns = {@JoinColumn(name = "result_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "asset_id", referencedColumnName = "href")})
    @MapKey(name = "href")
	private Map<String, Asset> assets = new HashMap<>();

	@JsonProperty("links")
	@Valid
	@Embedded
	private List<Link> links = new ArrayList<>();
	
	/**
	 * Factory of concrete objects based on type.
	 *
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@SuppressWarnings("unchecked")
    public static final <T extends BatchJobResult> T factory(String resultJson) throws JsonMappingException, JsonProcessingException {
	    T result;
	    JSONObject jsonObj = new JSONObject(resultJson);
	    
	    if (!jsonObj.has(TYPE_KEY)) {
	        throw new RuntimeException("Invalid job result json: no 'type' element found.");
	    }
	    
	    // fetch type (feature or collection)
	    String resultTypeStr = jsonObj.getString(TYPE_KEY);
	    AssetType resultType = AssetType.valueOf(resultTypeStr.toUpperCase());
	    
	    if (null == resultType) {
	        // TODO other type of exception: this is a client error
	        throw new RuntimeException("Unknown job type: " + resultTypeStr);
	    }

	    switch (resultType) {
	    case FEATURE:
	        result = (T) reify(resultJson, BatchJobResultFeature.class);
	        break;
	    case COLLECTION:
	        result = (T) reify(resultJson, BatchJobResultCollection.class);
	        break;
	    default:
	        throw new RuntimeException("Unhandled result type:" + resultType);
	    }
	    return result;
	}
	
	/** Overload of {@link BatchJobResult#factory(String)}. */
	public static final <T extends BatchJobResult> T factory(JSONObject jsonObj) throws JsonMappingException, JsonProcessingException {
	    return factory(jsonObj.toString());
	}
	
	/**
	 * Factory of empty (concrete) job result instances.
	 */
	@SuppressWarnings("unchecked")
    public static final <T extends BatchJobResult> T ofType(AssetType type) {
	    T obj;
	    
	    switch (type) {
        case FEATURE:
            obj = (T) new BatchJobResultFeature();
            break;
        case COLLECTION:
            obj = (T) new BatchJobResultCollection();
            break;
        default:
            throw new RuntimeException("Unhandled result type:" + type);
        }
	    
	    return obj;
	}
	/*
	 * getters/setters
	 */

	public BatchJobResult stacVersion(String stacVersion) {
		this.stacVersion = stacVersion;
		return this;
	}

	/**
	 * The [version of the STAC
	 * specification](https://github.com/radiantearth/stac-spec/releases), which MAY
	 * not be equal to the [STAC API version](#section/STAC). Supports versions
	 * 0.9.x and 1.x.x.
	 * 
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

	public BatchJobResult stacExtensions(Set<String> stacExtensions) {
		this.stacExtensions = stacExtensions;
		return this;
	}

	public BatchJobResult addStacExtensionsItem(String stacExtensionsItem) {
		if (this.stacExtensions == null) {
			this.stacExtensions = new LinkedHashSet<>();
		}
		this.stacExtensions.add(stacExtensionsItem);
		return this;
	}

	/**
	 * A list of implemented STAC extensions. The list contains URLs to the JSON
	 * Schema files it can be validated against. For official extensions, a
	 * \"shortcut\" can be used. This means you can specify the folder name of the
	 * extension in the STAC repository, for example `sar` for the SAR extension. If
	 * the versions of the extension and the collection diverge, you can specify the
	 * URL of the JSON schema file.
	 * 
	 * @return stacExtensions
	 */
	@ApiModelProperty(value = "A list of implemented STAC extensions. The list contains URLs to the JSON Schema files it can be validated against. For official extensions, a \"shortcut\" can be used. This means you can specify the folder name of the extension in the STAC repository, for example `sar` for the SAR extension. If the versions of the extension and the collection diverge, you can specify the URL of the JSON schema file.")

	@Valid

	public Set<String> getStacExtensions() {
		return stacExtensions;
	}

	public void setStacExtensions(Set<String> stacExtensions) {
		this.stacExtensions = stacExtensions;
	}

	public BatchJobResult id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Unique identifier of the batch job, generated by the back-end during
	 * creation. MUST match the specified pattern.
	 * 
	 * @return id
	 */
	@ApiModelProperty(example = "a3cca2b2aa1e3b5b", required = true, value = "Unique identifier of the batch job, generated by the back-end during creation. MUST match the specified pattern.")
	@NotNull
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BatchJobResult type(AssetType type) {
		this.type = type;
		return this;
	}

	/**
	 * Get type
	 * 
	 * @return type
	 */
	@ApiModelProperty(required = true, value = "")
	@NotNull

	public AssetType getType() {
		return type;
	}

	public void setType(AssetType type) {
		this.type = type;
	}

	public BatchJobResult assets(Map<String, Asset> assets) {
		this.assets = assets;
		return this;
	}

	public BatchJobResult putAssetsItem(String key, Asset assetsItem) {
		this.assets.put(key, assetsItem);
		return this;
	}

	/**
	 * Dictionary of asset objects for data that can be downloaded, each with a
	 * unique key. The keys MAY be used by clients as file names. It is RECOMMENDED
	 * to link to a copy of this STAC Item with relative links in the assets, which
	 * allows users to easily publish their processed data with a basic set of
	 * metadata.
	 * 
	 * @return assets
	 */
	@ApiModelProperty(example = "{\"preview.png\":{\"href\":\"https://example.openeo.org/api/download/583fba8b2ce583fba8b2ce/preview.png\",\"type\":\"image/png\",\"title\":\"Thumbnail\",\"roles\":[\"thumbnail\"]},\"process.json\":{\"href\":\"https://example.openeo.org/api/download/583fba8b2ce583fba8b2ce/process.json\",\"type\":\"application/json\",\"title\":\"Original Process\",\"roles\":[\"process\",\"reproduction\"]},\"1.tif\":{\"href\":\"https://example.openeo.org/api/download/583fba8b2ce583fba8b2ce/1.tif\",\"type\":\"image/tiff; application=geotiff\",\"title\":\"Band 1\",\"roles\":[\"data\"]},\"2.tif\":{\"href\":\"https://example.openeo.org/api/download/583fba8b2ce583fba8b2ce/2.tif\",\"type\":\"image/tiff; application=geotiff\",\"title\":\"Band 2\",\"roles\":[\"data\"]},\"inspire.xml\":{\"href\":\"https://example.openeo.org/api/download/583fba8b2ce583fba8b2ce/inspire.xml\",\"type\":\"application/xml\",\"title\":\"INSPIRE metadata\",\"description\":\"INSPIRE compliant XML metadata\",\"roles\":[\"metadata\"]}}", required = true, value = "Dictionary of asset objects for data that can be downloaded, each with a unique key. The keys MAY be used by clients as file names.  It is RECOMMENDED to link to a copy of this STAC Item with relative links in the assets, which allows users to easily publish their processed data with a basic set of metadata.")
	@NotNull

	@Valid

	public Map<String, Asset> getAssets() {
		return assets;
	}

	public void setAssets(Map<String, Asset> assets) {
		this.assets = assets;
	}

	public BatchJobResult links(List<Link> links) {
		this.links = links;
		return this;
	}

	public BatchJobResult addLinksItem(Link linksItem) {
		this.links.add(linksItem);
		return this;
	}

	/**
	 * Links related to this batch job result, e.g. a link to an invoice, additional
	 * log files or external documentation. The links MUST NOT contain links to the
	 * processed and downloadable data. Instead specify these in the `assets`
	 * property. Clients MUST NOT download the data referenced in the links by
	 * default. For relation types see the lists of [common relation types in
	 * openEO](#section/API-Principles/Web-Linking).
	 * 
	 * @return links
	 */
	@ApiModelProperty(required = true, value = "Links related to this batch job result, e.g. a link to an  invoice, additional log files or external documentation. The links MUST NOT contain links to the processed and downloadable data. Instead specify these in the `assets` property. Clients MUST NOT download the data referenced in the links by default. For relation types see the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")
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
		BatchJobResult batchJobResult = (BatchJobResult) o;
		return Objects.equals(this.stacVersion, batchJobResult.stacVersion)
				&& Objects.equals(this.stacExtensions, batchJobResult.stacExtensions)
				&& Objects.equals(this.id, batchJobResult.id) && Objects.equals(this.type, batchJobResult.type)
				&& Objects.equals(this.assets, batchJobResult.assets)
				&& Objects.equals(this.links, batchJobResult.links);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stacVersion, stacExtensions, id, type, assets, links);
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	protected String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
	
	/**
	 * Maps a JSON text to the given Java object type.
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	private static final <T extends BatchJobResult> T reify(String jsonText, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
	    T obj = JOM.readValue(jsonText, clazz);
	    return obj;
	}
	
	/** Internal JSON object factory. */ 
	private static final ObjectMapper JOM = new ObjectMapper();
	
	/** The jey for the result type in the JSON schema. */
	private static final String TYPE_KEY = "type";
}
