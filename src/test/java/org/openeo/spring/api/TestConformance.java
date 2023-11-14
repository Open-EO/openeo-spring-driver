package org.openeo.spring.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.openeo.spring.conformance.OgcApiConformance.Coverages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConformanceApiController.class)
public class TestConformance {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getConformance_shouldReturn200PlusURIs() throws Exception {
        mvc.perform(get("/conformance")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.conformsTo").exists(),
                jsonPath("$.conformsTo").isNotEmpty(),
                jsonPath("$.conformsTo").isArray(),
                jsonPath("$.conformsTo[0]").value(Coverages.CORE.getUri().toString()),
                jsonPath("$.conformsTo[1]").value(Coverages.SUBSETTING.getUri().toString()));
    }
}
