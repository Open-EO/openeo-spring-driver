package org.openeo.spring.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ActiveProfiles("oidc")
@Disabled("OIDC login/sessions not tested yet")
public class TestOIDCAuthentication {

    @Autowired
    private MockMvc mvc;

    @Test
    public void disabledBasicAuth_shouldReturn501() throws Exception {      
        mvc.perform(get("/credentials/basic")
        ).andExpectAll(
                status().is(501),
                header().exists("id"),
                header().exists("code"),
                header().exists("message"),
                header().exists("links"));
    }
}
