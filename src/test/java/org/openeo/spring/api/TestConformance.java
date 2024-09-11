package org.openeo.spring.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.openeo.spring.conformance.OgcApiConformance.Common;
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
                jsonPath("$.conformsTo[0]").value(Common.CORE.getUri().toString()),
                jsonPath("$.conformsTo[1]").value(Common.COLLECTIONS.getUri().toString()),
                jsonPath("$.conformsTo[2]").value(Coverages.CORE.getUri().toString()),
                jsonPath("$.conformsTo[3]").value(Coverages.BBOX.getUri().toString()),
                jsonPath("$.conformsTo[4]").value(Coverages.DATETIME.getUri().toString()),
                jsonPath("$.conformsTo[5]").value(Coverages.GeoTIFF.getUri().toString()),
                jsonPath("$.conformsTo[6]").value(Coverages.netCDF.getUri().toString()));
//                jsonPath("$.conformsTo[3]").value(Coverages.SUBSETTING.getUri().toString()));
    }
}
