package org.openeo.spring.model;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).
 */
@Entity
@ApiModel(description = "A link to another resource on the web. Bases on [RFC 5899](https://tools.ietf.org/html/rfc5988).")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Providers   {
    
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    @Column(nullable = true)
    private URI url;

    @JsonProperty("roles")
    @ElementCollection
    @CollectionTable(name = "providers_roles", joinColumns = @JoinColumn(name = "provider_id"))
    private List<String> roles;

    @JsonProperty("description")
    private String description;
    
    @JsonUnwrapped
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "processing_id", referencedColumnName = "id", nullable = true)
    private ProcessingExtension processing;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Providers name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.
     * @return rel
     */
    @ApiModelProperty(example = "EURAC", required = true, value = "Relationship between the current document and the linked document. SHOULD be a [registered link relation type](https://www.iana.org/assignments/link-relations/link-relations.xml) whenever feasible.")
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Providers url(URI url) {
        this.url = url;
        return this;
    }

    /**
     * The value MUST be a valid URL.
     * @return href
     */
    @ApiModelProperty(example = "https://example.openeo.org", required = true, value = "The value MUST be a valid URL.")
    @Valid
    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public Providers roles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public Providers roles(String ... roles) {
        return this.roles(Arrays.asList(roles));
    }

    @ApiModelProperty(example = "[producer, host]", value = "The value MUST be a list of strings that gives the providers status.")
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Providers description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Used as a human-readable label for a link.
     * @return title
     */
    @ApiModelProperty(example = "openEO", value = "Used as a human-readable label for a link.")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public ProcessingExtension getProcessingExtension() {
        return processing;
    }

    public void setProcessingExtension(ProcessingExtension proc) {
        this.processing = proc;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Providers providers = (Providers) o;
        return Objects.equals(this.name, providers.name) &&
                Objects.equals(this.url, providers.url) &&
                Objects.equals(this.roles, providers.roles) &&
                Objects.equals(this.description, providers.description) &&
                Objects.equals(this.processing, providers.processing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, roles, description);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Providers {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    [processing:extension]: ").append(toIndentedString(processing)).append("\n");
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

