package org.openeo.spring.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.openeo.spring.conformance.OgcLinkRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DefaultApiController.class)
public class TestLandingPage {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getLandingPage_shouldReturn200() throws Exception {
        mvc.perform(get("/")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.api_version").value("1.0.0"),
                jsonPath("$.stac_version").value("0.9.0"),
                jsonPath("$.backend_version").exists(),
                jsonPath("$.id").exists(),
                jsonPath("$.title").exists(),
                jsonPath("$.description").exists(),
                jsonPath("$.production").isBoolean(),
                jsonPath("$.endpoints").isArray(),
                jsonPath("$.endpoints").isNotEmpty(),
                jsonPath("$.billing").isNotEmpty(),
                jsonPath("$.links").isArray(),
                jsonPath("$.links[?(@.rel=='data')]").exists(),
                // TODO should we have a rel=conformance too?
                jsonPath(String.format("$.links[?(@.rel=='%s')]",
                        OgcLinkRelation.CONFORMANCE)).exists());
    }
}
