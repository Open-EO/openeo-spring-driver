package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openeo.spring.model.ProcessGraphWithMetadata;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * StoreSecondaryWebServiceRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class StoreSecondaryWebServiceRequest   {
  @JsonProperty("title")
  private JsonNullable<String> title = JsonNullable.undefined();

  @JsonProperty("description")
  private JsonNullable<String> description = JsonNullable.undefined();

  @JsonProperty("process")
  private ProcessGraphWithMetadata process;

  @JsonProperty("type")
  private String type;

  @JsonProperty("enabled")
  private Boolean enabled = true;

  @JsonProperty("configuration")
  private Object _configuration;

  @JsonProperty("plan")
  private JsonNullable<String> plan = JsonNullable.undefined();

  @JsonProperty("budget")
  private JsonNullable<BigDecimal> budget = JsonNullable.undefined();

  public StoreSecondaryWebServiceRequest title(String title) {
    this.title = JsonNullable.of(title);
    return this;
  }

  /**
   * A short description to easily distinguish entities.
   * @return title
  */
  @ApiModelProperty(example = "NDVI based on Sentinel 2", value = "A short description to easily distinguish entities.")


  public JsonNullable<String> getTitle() {
    return title;
  }

  public void setTitle(JsonNullable<String> title) {
    this.title = title;
  }

  public StoreSecondaryWebServiceRequest description(String description) {
    this.description = JsonNullable.of(description);
    return this;
  }

  /**
   * Detailed multi-line description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
  */
  @ApiModelProperty(example = "Deriving minimum NDVI measurements over pixel time series of Sentinel 2", value = "Detailed multi-line description to explain the entity.  [CommonMark 0.29](http://commonmark.org/) syntax MAY be used for rich text representation.")


  public JsonNullable<String> getDescription() {
    return description;
  }

  public void setDescription(JsonNullable<String> description) {
    this.description = description;
  }

  public StoreSecondaryWebServiceRequest process(ProcessGraphWithMetadata process) {
    this.process = process;
    return this;
  }

  /**
   * Get process
   * @return process
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public ProcessGraphWithMetadata getProcess() {
    return process;
  }

  public void setProcess(ProcessGraphWithMetadata process) {
    this.process = process;
  }

  public StoreSecondaryWebServiceRequest type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Definition of the service type to access result data. All available service types can be retrieved via `GET /service_types`. Service types MUST be accepted *case insensitive*.
   * @return type
  */
  @ApiModelProperty(example = "wms", required = true, value = "Definition of the service type to access result data. All available service types can be retrieved via `GET /service_types`. Service types MUST be accepted *case insensitive*.")
  @NotNull


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StoreSecondaryWebServiceRequest enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Describes whether a secondary web service is responding to requests (true) or not (false). Defaults to true. Disabled services don't produce any costs.
   * @return enabled
  */
  @ApiModelProperty(value = "Describes whether a secondary web service is responding to requests (true) or not (false). Defaults to true. Disabled services don't produce any costs.")


  public Boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public StoreSecondaryWebServiceRequest _configuration(Object _configuration) {
    this._configuration = _configuration;
    return this;
  }

  /**
   * Map of arguments, i.e. the parameter names supported by the secondary web service combined with actual values. See `GET /service_types` for supported parameters and valid arguments. For example, this could specify the required version of the service, visualization details or any other service dependant configuration.
   * @return _configuration
  */
  @ApiModelProperty(example = "{\"version\":\"1.3.0\"}", value = "Map of arguments, i.e. the parameter names supported by the secondary web service combined with actual values. See `GET /service_types` for supported parameters and valid arguments. For example, this could specify the required version of the service, visualization details or any other service dependant configuration.")


  public Object getConfiguration() {
    return _configuration;
  }

  public void setConfiguration(Object _configuration) {
    this._configuration = _configuration;
  }

  public StoreSecondaryWebServiceRequest plan(String plan) {
    this.plan = JsonNullable.of(plan);
    return this;
  }

  /**
   * The billing plan to process and charge the job with.  The plans and the default plan can be retrieved by calling `GET /`.  Billing plans MUST be accepted *case insensitive*. Billing plans not on the list of available plans MUST be rejected with openEO error `BillingPlanInvalid`.  If no billing plan is specified by the client, the server MUST default to the default billing plan in `GET /`. If the default billing plan of the provider changes, the job or service MUST not be affected by the change, i.e. the default plan which is valid during job or service creation must be permanently assigned to the job or service until the client requests to change it.
   * @return plan
  */
  @ApiModelProperty(example = "free", value = "The billing plan to process and charge the job with.  The plans and the default plan can be retrieved by calling `GET /`.  Billing plans MUST be accepted *case insensitive*. Billing plans not on the list of available plans MUST be rejected with openEO error `BillingPlanInvalid`.  If no billing plan is specified by the client, the server MUST default to the default billing plan in `GET /`. If the default billing plan of the provider changes, the job or service MUST not be affected by the change, i.e. the default plan which is valid during job or service creation must be permanently assigned to the job or service until the client requests to change it.")


  public JsonNullable<String> getPlan() {
    return plan;
  }

  public void setPlan(JsonNullable<String> plan) {
    this.plan = plan;
  }

  public StoreSecondaryWebServiceRequest budget(BigDecimal budget) {
    this.budget = JsonNullable.of(budget);
    return this;
  }

  /**
   * Maximum amount of costs the request is allowed to produce. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If no currency is set, this field MUST NOT be a number.   If possible, back-ends SHOULD reject jobs with openEO error `PaymentRequired` if the budget is too low to process the request completely. Otherwise, when reaching the budget jobs MAY try to return partial results if possible. Otherwise the request and results are discarded. Users SHOULD be warned by clients that reaching the budget MAY discard the results and that setting this value should be well-wrought.   Setting the budget to `null` means there is no specified budget.
   * @return budget
  */
  @ApiModelProperty(example = "100", value = "Maximum amount of costs the request is allowed to produce. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If no currency is set, this field MUST NOT be a number.   If possible, back-ends SHOULD reject jobs with openEO error `PaymentRequired` if the budget is too low to process the request completely. Otherwise, when reaching the budget jobs MAY try to return partial results if possible. Otherwise the request and results are discarded. Users SHOULD be warned by clients that reaching the budget MAY discard the results and that setting this value should be well-wrought.   Setting the budget to `null` means there is no specified budget.")

  @Valid

  public JsonNullable<BigDecimal> getBudget() {
    return budget;
  }

  public void setBudget(JsonNullable<BigDecimal> budget) {
    this.budget = budget;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StoreSecondaryWebServiceRequest storeSecondaryWebServiceRequest = (StoreSecondaryWebServiceRequest) o;
    return Objects.equals(this.title, storeSecondaryWebServiceRequest.title) &&
        Objects.equals(this.description, storeSecondaryWebServiceRequest.description) &&
        Objects.equals(this.process, storeSecondaryWebServiceRequest.process) &&
        Objects.equals(this.type, storeSecondaryWebServiceRequest.type) &&
        Objects.equals(this.enabled, storeSecondaryWebServiceRequest.enabled) &&
        Objects.equals(this._configuration, storeSecondaryWebServiceRequest._configuration) &&
        Objects.equals(this.plan, storeSecondaryWebServiceRequest.plan) &&
        Objects.equals(this.budget, storeSecondaryWebServiceRequest.budget);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, process, type, enabled, _configuration, plan, budget);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StoreSecondaryWebServiceRequest {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    process: ").append(toIndentedString(process)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    _configuration: ").append(toIndentedString(_configuration)).append("\n");
    sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
    sb.append("    budget: ").append(toIndentedString(budget)).append("\n");
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

