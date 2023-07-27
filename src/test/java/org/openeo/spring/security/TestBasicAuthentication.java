package org.openeo.spring.security;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.openeo.spring.api.CredentialsApiController;
import org.openeo.spring.bearer.JWTTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

/**
 * Tests the Basic Authentication login process
 * and Bearer token session management.
 * 
 * The class is abstract, to re-use the integration tests
 * for multiple profiles.
 * 
 * @see ActiveProfiles
 */
@RunWith(SpringRunner.class)
@WebMvcTest
//@ActiveProfiles("test") // -> src/test/resources/application-$PROFILE.properties
public abstract class TestBasicAuthentication {

    @Autowired    
    MockMvc mvc;
    
    @Autowired
    WebApplicationContext context;

    @Autowired
    JWTTokenService tokenService;
    
    @Test
    @WithMockUser(username = "satan", password = "petrodragonic")
    public void get_okBasic_shouldSucceedWith200() throws Exception {        
        MvcResult mvcResult = mvc.perform(get("/credentials/basic")
                .header(HttpHeaders.AUTHORIZATION, "Basic c2F0YW46cGV0cm9kcmFnb25pYw==")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                // token is in header
                header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")),
                // token is in body                
                jsonPath("$.access_token").exists()
        ).andReturn();

        // body token equals header token
        String response = mvcResult.getResponse().getContentAsString();
        String authHeader = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
  
        String headerToken = authHeader.substring("Bearer ".length());
        String bodyAccessToken = JsonPath.parse(response).read("$.access_token");
        
        assertEquals(bodyAccessToken, headerToken, "token in body and header should coincide");
    }
    
    @Test
    @WithMockUser(value = "satan")
    public void get_protectedResourcewWithToken_shouldSucceedWith200() throws Exception {
        // manually generate token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = tokenService.generateToken(user);
        
        mvc.perform(get("/collections")
                .header(HttpHeaders.AUTHORIZATION,
                        String.format("Bearer basic//%s", token))
        ).andExpect(
                status().isOk());
    }
    
    @Test
    public void get_protectedResourcewWithWrongToken_shouldReturn403() throws Exception {
        mvc.perform(get("/collections")
                .header(HttpHeaders.AUTHORIZATION, "Bearer basic//00000000000FAKE00000000000")
        ).andExpectAll(
                status().is(403)
//                header().exists("id"), FIXME ErrorAttributes not picked in tests
//                header().exists("code"),
//                header().exists("message"),
//                header().exists("links")
                );
    }
    
    @Test
    public void get_wrongTokenPrefix_shouldReturn403() throws Exception {
        mvc.perform(get("/collections")
                .header(HttpHeaders.AUTHORIZATION, "Bearer wysiwyg//00000000000FAKE00000000000")
        ).andExpectAll(
                status().is(403)
//                header().exists("id"), FIXME ErrorAttributes not picked in tests
//                header().exists("code"),
//                header().exists("message"),
//                header().exists("links")
                );
    }
    
    @Test
    @WithMockUser(value = "satan")
    public void get_expiredToken_shouldReturn403() throws Exception {
        // manually generate token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = tokenService.generateToken(user, -1, ChronoUnit.SECONDS);
        
        mvc.perform(get("/collections")
                .header(HttpHeaders.AUTHORIZATION,
                        String.format("Bearer basic//%s", token))
        ).andExpectAll(
                status().is(403)
//                header().exists("id"), FIXME ErrorAttributes not picked in tests
//                header().exists("code"),
//                header().exists("message"),
//                header().exists("links")
                );
    }
    
    @Test
    public void get_noAuth_shouldReturnAuthRequired401() throws Exception {
        mvc.perform(get("/credentials/basic")
        ).andExpectAll(
                status().is(401),
                header().exists(HttpHeaders.WWW_AUTHENTICATE)
//                header().exists("id"), FIXME ErrorAttributes not picked in tests
//                header().exists("code"),
//                header().exists("message"),
//                header().exists("links")
                );
    }
    
    /** @see BasicAuthenticationFilter */ 
    @Test
    @WithMockUser(username = "satan", password = "petrodragonic")
    public void get_wrongAuth_shouldReturn401() throws Exception {
        mvc.perform(get("/credentials/basic")
                .header(HttpHeaders.AUTHORIZATION, "Basic _InfestTheRatsNest_=")
        ).andExpectAll(
                status().is(401)
//                header().exists("id"), FIXME ErrorAttributes not picked in tests
//                header().exists("code"),
//                header().exists("message"),
//                header().exists("links")
                );
    }
    
    /**
     * Testing if disabling basic authentication returns the proper error.
     * The test is not optimal: as we are manually changing the internal field
     * in the controller, a posteriori of the initial mock setup from the properties.
     * 
     * TODO create profiles of properties then 
     */
    @Test
    @WithMockUser(username = "charlie")
    public void disabledBasicAuth_shouldReturn501() throws Exception {
        CredentialsApiController controller = context.getBean(CredentialsApiController.class);
        ReflectionTestUtils.setField(controller, "enableBasicAuth", false);
        
        mvc.perform(get("/credentials/basic")
        ).andExpectAll(
                status().is(501)
//                header().exists("id"), FIXME ErrorAttributes not picked in tests
//                header().exists("code"),
//                header().exists("message"),
//                header().exists("links")
                );
    }
}
