package org.openeo.spring.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.UserStorage;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Holds user information. If no budget or storage limit applies to the user account the corresponding properties MUST be set to null.
 */
@ApiModel(description = "Holds user information. If no budget or storage limit applies to the user account the corresponding properties MUST be set to null.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
public class UserDataResponse   {
  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("storage")
  private JsonNullable<UserStorage> storage = JsonNullable.undefined();

  @JsonProperty("budget")
  private JsonNullable<BigDecimal> budget = JsonNullable.undefined();

  @JsonProperty("links")
  @Valid
  private List<Link> links = null;

  public UserDataResponse userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Unique identifier of the user. MUST match the specified pattern. This is usually a randomly generated internal identifier from the provider not meant for displaying purposes.
   * @return userId
  */
  @ApiModelProperty(example = "john_doe", required = true, value = "Unique identifier of the user. MUST match the specified pattern. This is usually a randomly generated internal identifier from the provider not meant for displaying purposes.")
  @NotNull

@Pattern(regexp="^[\\w\\-\\.~]+$") 
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public UserDataResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The user name, a human-friendly displayable name. Could be  the user's real name or a nickname.
   * @return name
  */
  @ApiModelProperty(value = "The user name, a human-friendly displayable name. Could be  the user's real name or a nickname.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserDataResponse storage(UserStorage storage) {
    this.storage = JsonNullable.of(storage);
    return this;
  }

  /**
   * Get storage
   * @return storage
  */
  @ApiModelProperty(value = "")

  @Valid

  public JsonNullable<UserStorage> getStorage() {
    return storage;
  }

  public void setStorage(JsonNullable<UserStorage> storage) {
    this.storage = storage;
  }

  public UserDataResponse budget(BigDecimal budget) {
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

  public UserDataResponse links(List<Link> links) {
    this.links = links;
    return this;
  }

  public UserDataResponse addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Links related to the user profile, e.g. where payments are handled or the user profile could be edited.  It is RECOMMENDED to provide links with the following `rel` (relation) types:  1. `payment`: A page where users can recharge their user account with money or credits.  2. `edit-form`: Points to a page where the user can edit his user profile.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).
   * @return links
  */
  @ApiModelProperty(example = "[{\"href\":\"https://example.openeo.org/john_doe/payment/\",\"rel\":\"payment\"},{\"href\":\"https://example.openeo.org/john_doe/edit/\",\"rel\":\"edit-form\"},{\"href\":\"https://example.openeo.org/john_doe/\",\"rel\":\"alternate\",\"type\":\"text/html\",\"title\":\"User profile\"},{\"href\":\"https://example.openeo.org/john_doe.vcf\",\"rel\":\"alternate\",\"type\":\"text/vcard\",\"title\":\"vCard of John Doe\"}]", value = "Links related to the user profile, e.g. where payments are handled or the user profile could be edited.  It is RECOMMENDED to provide links with the following `rel` (relation) types:  1. `payment`: A page where users can recharge their user account with money or credits.  2. `edit-form`: Points to a page where the user can edit his user profile.  For additional relation types see also the lists of [common relation types in openEO](#section/API-Principles/Web-Linking).")

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
    UserDataResponse userDataResponse = (UserDataResponse) o;
    return Objects.equals(this.userId, userDataResponse.userId) &&
        Objects.equals(this.name, userDataResponse.name) &&
        Objects.equals(this.storage, userDataResponse.storage) &&
        Objects.equals(this.budget, userDataResponse.budget) &&
        Objects.equals(this.links, userDataResponse.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, name, storage, budget, links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDataResponse {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    storage: ").append(toIndentedString(storage)).append("\n");
    sb.append("    budget: ").append(toIndentedString(budget)).append("\n");
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

