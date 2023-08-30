package org.openeo.spring.json;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Process;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
		if(value.getEngine() != null) {
			gen.writeStringField("engine", value.getEngine().toString());
		}
		gen.writeStringField("id", value.getId());
		if(value.getSummary() != null) {
			gen.writeStringField("summary", value.getSummary());
		}
		if(value.getDescription() != null) {
			gen.writeStringField("description", value.getDescription());
		}
		JSONArray parameters = (JSONArray) value.getParameters();		
		if(parameters != null) {
			gen.writeObjectField("parameters", parameters.toList());
		}
		List<String> categories = value.getCategories();
		if(categories != null) {
			gen.writeObjectField("categories", categories.toArray());
		}
		List<Link> links = value.getLinks();		
		if(links != null) {
			gen.writeObjectField("links", links.toArray());
		}
		JSONObject returns = (JSONObject) value.getReturns();		
		if(returns != null) {
			gen.writeObjectField("returns", returns.toMap());
		}
		JSONObject exceptions = (JSONObject) value.getExceptions();		
		if(exceptions != null) {
			gen.writeObjectField("exceptions", exceptions.toMap());
		}
		JSONArray examples = (JSONArray) value.getExamples();		
		if(examples != null) {
			gen.writeObjectField("examples", examples.toList());
		}
//		JSONObject processGraph = (JSONObject) value.getProcessGraph();		
//		if(processGraph != null) {
//			gen.writeObjectField("process_graph", processGraph.toMap());
//		}
		gen.writeEndObject();
	}

}
