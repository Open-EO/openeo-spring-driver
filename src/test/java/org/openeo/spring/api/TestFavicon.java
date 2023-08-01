package org.openeo.spring.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FaviconController.class)
public class TestFavicon {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getFavicon_shouldReturn200() throws Exception {
        mvc.perform(get("/favicon.ico")
        ).andExpectAll(
                status().is(200),
                content().contentType(MediaType.valueOf("image/x-icon")));
    }
}
