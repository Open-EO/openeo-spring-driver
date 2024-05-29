package org.openeo.spring.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.openeo.spring.loaders.JSONMarshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for (de)serialization of batch job results of STAC type "Feature".
 */
public class TestBatchJobResultFeature {
    @Test
    public void deserializeJsonText_success()
            throws JsonMappingException, JsonProcessingException {
        
        String jsonText = JobResult.TEST_FEATURE.loadText();
        
        ObjectMapper OM = JSONMarshaller.MAPPER;
        BatchJobResultFeature res = OM.readValue(jsonText, BatchJobResultFeature.class);
        
        // assertions
        testFields(res);
    }
    
    @Test
    public void deserializeJsonTextViaObject_success()
            throws JsonMappingException, JsonProcessingException {
        
        String jsonText = JobResult.TEST_FEATURE.loadText();
        JSONObject responseJson = new JSONObject(jsonText);
        
        ObjectMapper OM = JSONMarshaller.MAPPER;
        BatchJobResultFeature res = OM.readValue(responseJson.toString(), BatchJobResultFeature.class);
        
        // assertions
        testFields(res);
    }
    
    /** Shared assertions for verify deserialization of {@link JobResult#TEST_COLL}. */
    private void testFields(BatchJobResultFeature f) {
        assertEquals(f.getId().toString(), "a3cca2b2aa1e3b5b");
        // ...
    }
}
