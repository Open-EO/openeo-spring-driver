package org.openeo.spring.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.openeo.spring.json.TimeIntervalSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The *potential* temporal extent of the features in the collection.
 */
@Embeddable
@ApiModel(description = "The *potential* temporal extent of the features in the collection.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class CollectionTemporalExtent   {

    @JsonProperty("interval")
    @Valid
    @Embedded
    @JsonSerialize(contentUsing = TimeIntervalSerializer.class)
    private List<List<OffsetDateTime>> interval = null;

    public CollectionTemporalExtent interval(List<List<OffsetDateTime>> interval) {
        this.interval = interval;
        return this;
    }

    public CollectionTemporalExtent addIntervalItem(List<OffsetDateTime> intervalItem) {
        if (this.interval == null) {
            this.interval = new ArrayList<>();
        }
        this.interval.add(intervalItem);
        return this;
    }

    /**
     * One or more time intervals that describe the temporal extent of the dataset. The value `null` is supported and indicates an open time interval. In the Core only a single time interval is supported. Extensions may support multiple intervals. If multiple intervals are provided, the union of the intervals describes the temporal extent.
     * @return interval
     */
    @ApiModelProperty(value = "One or more time intervals that describe the temporal extent of the dataset. The value `null` is supported and indicates an open time interval. In the Core only a single time interval is supported. Extensions may support multiple intervals. If multiple intervals are provided, the union of the intervals describes the temporal extent.")

    @Valid
    @Size(min=1) 
    public List<List<OffsetDateTime>> getInterval() {
        return interval;
    }

    public void setInterval(List<List<OffsetDateTime>> interval) {
        this.interval = interval;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionTemporalExtent collectionTemporalExtent = (CollectionTemporalExtent) o;
        return Objects.equals(this.interval, collectionTemporalExtent.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CollectionTemporalExtent {\n");

        sb.append("    interval: ").append(toIndentedString(interval)).append("\n");
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

