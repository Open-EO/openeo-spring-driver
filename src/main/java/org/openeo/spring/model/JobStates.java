package org.openeo.spring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The current status of a batch job. The following status changes can occur: *
 * `POST /jobs`: The status is initialized as `created`. * `POST
 * /jobs/{job_id}/results`: The status is set to `queued`, if processing doesn't
 * start instantly. * Once the processing starts the status is set to `running`.
 * * Once the data is available to download the status is set to `finished`. *
 * Whenever an error occurs during processing, the status must be set to
 * `error`. * `DELETE /jobs/{job_id}/results`: The status is set to `canceled`
 * if the status was `running` beforehand and partial or preliminary results are
 * available to be downloaded. Otherwise the status is set to `created`.
 */
public enum JobStates {
	CREATED("created"),

	QUEUED("queued"),

	RUNNING("running"),

	CANCELED("canceled"),

	FINISHED("finished"),

	ERROR("error");

	private String value;

	JobStates(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static JobStates fromValue(String value) {
		for (JobStates b : JobStates.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
