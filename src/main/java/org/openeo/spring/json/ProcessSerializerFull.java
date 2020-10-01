package org.openeo.spring.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openeo.spring.model.Process;

public class ProcessSerializerFull extends StdSerializer<Process>{
	
	private static final long serialVersionUID = -5946586040848103586L;

	public ProcessSerializerFull() {
		this(null);
	}
	
	public ProcessSerializerFull(Class<Process> classType) {
		super(classType);
	}

	@Override
	public void serialize(Process value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("id", value.getId());
//		if(value.getSummary() != null) {
//			gen.writeStringField("summary", value.getSummary());
//		}
//		if(value.getDescription() != null) {
//			gen.writeStringField("description", value.getDescription());
//		}
//		JSONArray parameters = (JSONArray) value.getParameters();		
//		if(parameters != null) {
//			gen.writeObjectField("parameters", parameters.toList());
//		}
//		JSONObject processGraph = (JSONObject) value.getProcessGraph();		
//		if(processGraph != null) {
//			gen.writeObjectField("process_graph", processGraph.toMap());
//		}
		gen.writeEndObject();
	}

}
