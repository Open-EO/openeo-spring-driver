package org.openeo.spring.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.json.JSONObject;
import org.openeo.spring.model.Process;
import org.openeo.spring.model.ProcessParameter;

public class ProcessSerializer extends StdSerializer<Process>{
	
	private static final long serialVersionUID = -5946586040848103586L;

	public ProcessSerializer() {
		this(null);
	}
	
	public ProcessSerializer(Class<Process> classType) {
		super(classType);
	}

	@Override
	public void serialize(Process value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("id", value.getId());
		if(value.getSummary() != null) {
			gen.writeStringField("summary", value.getSummary());
		}
		if(value.getDescription() != null) {
			gen.writeStringField("description", value.getDescription());
		}
		if(value.getParameters() != null && !value.getParameters().isEmpty()) {
			gen.writeStartArray();
			for(ProcessParameter parameter: value.getParameters()) {
				gen.writeEmbeddedObject(parameter);
			}
			gen.writeEndArray();
		}
		JSONObject processGraph = (JSONObject) value.getProcessGraph();		
		if(processGraph != null) {
			gen.writeObjectField("process_graph", processGraph.toMap());
		}
		gen.writeEndObject();
	}

}
