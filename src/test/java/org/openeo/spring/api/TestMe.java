package org.openeo.spring.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.openeo.spring.bearer.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MeApiController.class)
public abstract class TestMe {

    @Autowired
    private MockMvc mvc;
    
    @Autowired
    ITokenService tokenService;

    @Test
    @WithMockUser(username = "john")
    @WithUserDetails("john")
    // see also : https://stackoverflow.com/a/43920932/1329340
    public void getMe_shouldReturn200() throws Exception {
        mvc.perform(get("/me")
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.user_id").exists(),
                jsonPath("$.name").value("john"));
    }
    
    @Test
    @WithMockUser(username = "jill")
    public void getMeWrongToken_shouldReturn403() throws Exception {
        // manually generate token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        UserDetails fakeUser = new User("fakeJill", user.getPassword(), user.getAuthorities());
        String token = tokenService.generateToken(fakeUser);
        
        mvc.perform(get("/me")
                .header(HttpHeaders.AUTHORIZATION,
                        String.format("Bearer basic//%s", token))
        ).andExpectAll(
                status().is(403),
                content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    public void getMeUnauthenticated_shouldReturn401() throws Exception {
        mvc.perform(get("/me")
        ).andExpectAll(
                status().is(401),
                header().exists(HttpHeaders.WWW_AUTHENTICATE));
    }
}
