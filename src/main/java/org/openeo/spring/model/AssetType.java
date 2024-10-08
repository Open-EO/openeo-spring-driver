package org.openeo.spring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Types of a STAC asset.
 */
public enum AssetType  {
	
	FEATURE("Feature"),
	COLLECTION("Collection"),
	CATALOG("Catalog");

	private String value;

	AssetType(String value) {
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
	public static AssetType fromValue(String value) {
		for (AssetType b : AssetType.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
