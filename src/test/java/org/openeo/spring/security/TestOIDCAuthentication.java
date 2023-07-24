package org.openeo.spring.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.openeo.spring.api.CredentialsApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(CredentialsApiController.class)
@ActiveProfiles("oidc")
public class TestOIDCAuthentication {

    @Autowired
    private MockMvc mvc;

    @Test
    public void disabledBasicAuth_shouldReturn501() throws Exception {      
        mvc.perform(get("/credentials/basic")
        ).andExpect(
                status().is(501));
    }
}
