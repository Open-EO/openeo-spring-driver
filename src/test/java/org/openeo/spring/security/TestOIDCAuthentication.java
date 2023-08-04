package org.openeo.spring.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ActiveProfiles("oidc")
public class TestOIDCAuthentication {

    @Autowired
    private MockMvc mvc;
    
    @Test
    public void get_noAuth_shouldReturnProviders200() throws Exception {
        mvc.perform(get("/credentials/oidc")
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.providers").exists(),
                jsonPath("$.providers").isArray(),
                jsonPath("$.providers").isNotEmpty(),
//                jsonPath("$.providers[*].*", is(7)), TODO
                jsonPath("$.providers[*].id").exists(),
                jsonPath("$.providers[*].issuer").exists(),
                jsonPath("$.providers[*].scopes").hasJsonPath(),
                jsonPath("$.providers[*].title").exists(),
                jsonPath("$.providers[*].description").hasJsonPath(),
                jsonPath("$.providers[*].default_clients").hasJsonPath(),
                jsonPath("$.providers[*].links").hasJsonPath()
                );
    }
    
    @Test
    @WithMockUser(value = "brutus")
    public void get_protectedResourceAuth_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/jobs")
        ).andExpect(
                status().isOk());
    }
    
    @ParameterizedTest
    @MethodSource("providePublicAPIResources")
    public void get_publicResourceNoAuth_shouldSucceedWith200(String resource) throws Exception {
        mvc.perform(get(resource)
        ).andExpect(
                status().isOk());
    }
    
    @Test
    public void get_InvalidToken_shouldReturn401() throws Exception {
        mvc.perform(get("/jobs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer oidc/ACME/00000000000FAKE00000000000")
        ).andExpectAll(
                status().is(401),
                header().exists(HttpHeaders.WWW_AUTHENTICATE)
                );
    }
    
    @Test
    public void get_InvalidTokenPrefix_shouldReturn403() throws Exception {
        mvc.perform(get("/jobs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer wysiwyg//00000000000FAKE00000000000")
        ).andExpectAll(
                status().is(403),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").hasJsonPath(),
                jsonPath("$.code").exists(),
                jsonPath("$.message").exists(),
                jsonPath("$.links").hasJsonPath()
                );
    }
    
    @Test
    public void disabledBasicAuth_shouldReturn501() throws Exception {      
        mvc.perform(get("/credentials/basic")
        ).andExpectAll(
                status().is(501),
                jsonPath("$.id").hasJsonPath(),
                jsonPath("$.code").exists(),
                jsonPath("$.message").exists(),
                jsonPath("$.links").hasJsonPath());
    }
    
    private static List<String> providePublicAPIResources() {
        return Arrays.asList(GlobalSecurityConfig.NOAUTH_API_RESOURCES);
    }
}
