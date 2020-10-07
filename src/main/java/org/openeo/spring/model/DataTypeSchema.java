package org.openeo.spring.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

/**
 * Either a single data type or a list of data types.
 */
@ApiModel(description = "Either a single data type or a list of data types.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
public class DataTypeSchema {
	
	@JsonProperty("type")
	private byte[] type;
	
	@JsonProperty("subtype")
	private String subType;

	public Object getType() {
		if(this.type == null) return null;
		ByteArrayInputStream in = new ByteArrayInputStream(this.type);
		JSONArray typeArray = null;
		JSONObject typeObject = null;
		try {
		    ObjectInputStream is = new ObjectInputStream(in);
		    typeArray = new JSONArray((List<Object>) is.readObject());
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
		    ObjectInputStream is = new ObjectInputStream(in);
		    typeObject = new JSONObject(is.readObject());
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(typeArray != null) {
			return typeArray;
		}else if (typeObject != null) {
			return typeObject;
		}
		return null;
	}

	public void setType(Object type) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(type);
		    this.type = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class DataTypeSchema {\n");

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
