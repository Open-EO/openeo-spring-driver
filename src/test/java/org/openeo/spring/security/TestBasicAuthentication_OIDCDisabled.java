package org.openeo.spring.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * Run test suite for case where OIDC auth is disabled.
 */
@ActiveProfiles("ba")
public class TestBasicAuthentication_OIDCDisabled extends TestBasicAuthentication {
    
    @Test
    public void disabledOIDCAuth_shouldReturn501() throws Exception {      
        mvc.perform(get("/credentials/oidc")
        ).andExpect(
                status().is(501));
    }
}
