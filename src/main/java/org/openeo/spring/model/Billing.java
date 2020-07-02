package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.BillingPlan;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Billing related data, e.g. the currency used or available plans to process jobs. This property MUST be specified if the back-end uses any billing related API functionalities, e.g. budgeting or estimates. The absence of this property doesn&#39;t mean the back-end is necessarily free to use for all. Providers may choose to bill users outside of the API, e.g. with a monthly fee that is not depending on individual API interactions.
 */
@ApiModel(description = "Billing related data, e.g. the currency used or available plans to process jobs. This property MUST be specified if the back-end uses any billing related API functionalities, e.g. budgeting or estimates. The absence of this property doesn't mean the back-end is necessarily free to use for all. Providers may choose to bill users outside of the API, e.g. with a monthly fee that is not depending on individual API interactions.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class Billing   {
  @JsonProperty("currency")
  private JsonNullable<String> currency = JsonNullable.undefined();

  @JsonProperty("default_plan")
  private String defaultPlan;

  @JsonProperty("plans")
  @Valid
  private List<BillingPlan> plans = null;

  public Billing currency(String currency) {
    this.currency = JsonNullable.of(currency);
    return this;
  }

  /**
   * The currency the back-end is billing in. The currency MUST be either a valid currency code as defined in ISO-4217 or a proprietary currency, e.g. tiles or back-end specific credits. If set to the default value `null`, budget and costs are not supported by the back-end and users can't be charged.
   * @return currency
  */
  @ApiModelProperty(example = "USD", required = true, value = "The currency the back-end is billing in. The currency MUST be either a valid currency code as defined in ISO-4217 or a proprietary currency, e.g. tiles or back-end specific credits. If set to the default value `null`, budget and costs are not supported by the back-end and users can't be charged.")
  @NotNull


  public JsonNullable<String> getCurrency() {
    return currency;
  }

  public void setCurrency(JsonNullable<String> currency) {
    this.currency = currency;
  }

  public Billing defaultPlan(String defaultPlan) {
    this.defaultPlan = defaultPlan;
    return this;
  }

  /**
   * Name of the default plan to use when the user doesn't specify a plan. Is allowed to be *case insensitive* throughout the API.
   * @return defaultPlan
  */
  @ApiModelProperty(example = "free", value = "Name of the default plan to use when the user doesn't specify a plan. Is allowed to be *case insensitive* throughout the API.")


  public String getDefaultPlan() {
    return defaultPlan;
  }

  public void setDefaultPlan(String defaultPlan) {
    this.defaultPlan = defaultPlan;
  }

  public Billing plans(List<BillingPlan> plans) {
    this.plans = plans;
    return this;
  }

  public Billing addPlansItem(BillingPlan plansItem) {
    if (this.plans == null) {
      this.plans = new ArrayList<>();
    }
    this.plans.add(plansItem);
    return this;
  }

  /**
   * Array of plans
   * @return plans
  */
  @ApiModelProperty(example = "[{\"name\":\"free\",\"description\":\"Free plan. Calculates one tile per second and a maximum amount of 100 tiles per hour.\",\"url\":\"http://cool-cloud-corp.com/plans/free-plan\",\"paid\":false},{\"name\":\"premium\",\"description\":\"Premium plan. Calculates unlimited tiles and each calculated tile costs 0.003 USD.\",\"url\":\"http://cool-cloud-corp.com/plans/premium-plan\",\"paid\":true}]", value = "Array of plans")

  @Valid

  public List<BillingPlan> getPlans() {
    return plans;
  }

  public void setPlans(List<BillingPlan> plans) {
    this.plans = plans;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Billing billing = (Billing) o;
    return Objects.equals(this.currency, billing.currency) &&
        Objects.equals(this.defaultPlan, billing.defaultPlan) &&
        Objects.equals(this.plans, billing.plans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, defaultPlan, plans);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Billing {\n");
    
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    defaultPlan: ").append(toIndentedString(defaultPlan)).append("\n");
    sb.append("    plans: ").append(toIndentedString(plans)).append("\n");
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

