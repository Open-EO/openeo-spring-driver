package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * BillingPlan
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class BillingPlan   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("paid")
  private Boolean paid;

  @JsonProperty("url")
  private URI url;

  public BillingPlan name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the plan. Is allowed to be *case insensitive* throughout the API.
   * @return name
  */
  @ApiModelProperty(example = "free", required = true, value = "Name of the plan. Is allowed to be *case insensitive* throughout the API.")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BillingPlan description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A description that gives a rough overview over the plan.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(example = "Free plan for testing.", required = true, value = "A description that gives a rough overview over the plan.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")
  @NotNull


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BillingPlan paid(Boolean paid) {
    this.paid = paid;
    return this;
  }

  /**
   * Indicates whether the plan is a paid plan (`true`) or a free plan (`false`).
   * @return paid
  */
  @ApiModelProperty(required = true, value = "Indicates whether the plan is a paid plan (`true`) or a free plan (`false`).")
  @NotNull


  public Boolean isPaid() {
    return paid;
  }

  public void setPaid(Boolean paid) {
    this.paid = paid;
  }

  public BillingPlan url(URI url) {
    this.url = url;
    return this;
  }

  /**
   * URL to a web page with more details about the plan.
   * @return url
  */
  @ApiModelProperty(example = "http://cool-cloud-corp.com/plans/free-plan", value = "URL to a web page with more details about the plan.")

  @Valid

  public URI getUrl() {
    return url;
  }

  public void setUrl(URI url) {
    this.url = url;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillingPlan billingPlan = (BillingPlan) o;
    return Objects.equals(this.name, billingPlan.name) &&
        Objects.equals(this.description, billingPlan.description) &&
        Objects.equals(this.paid, billingPlan.paid) &&
        Objects.equals(this.url, billingPlan.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, paid, url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingPlan {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    paid: ").append(toIndentedString(paid)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
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

