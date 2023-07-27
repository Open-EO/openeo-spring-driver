package org.openeo.spring.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest()
@ActiveProfiles("test")
public class TestCorsConf {

    @Autowired
    private MockMvc mvc;

    @ParameterizedTest
    @MethodSource("testAPIResources")
    public void preflightRequest_shouldReturn204(String resource) throws Exception {      
        mvc.perform(options(resource)
                .header(HttpHeaders.ORIGIN, "https://kingink.org:666")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization, Content-Type")
        ).andExpectAll(
                status().is(204),
//                header().exists(HttpHeaders.CONTENT_TYPE) TODO (SHOULD contain, anyway)
                header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS),
                header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS),
                header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN),
                header().exists(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS));
    }
    
    @ParameterizedTest
    @MethodSource("testAPIResources")
    @WithMockUser(value = "robin")
    public void request_shouldReturnCorsHeaders(String resource) throws Exception {      
        mvc.perform(get(resource)
                .header(HttpHeaders.ORIGIN, "https://deadjoe.org")
        ).andExpectAll(
                header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN),
                header().exists(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS),
                header().exists(HttpHeaders.CONTENT_TYPE));
    }
    
    /** Provides inputs for parameterized tests. */
    private static Stream<Arguments> testAPIResources() {
        return Stream.of(
                Arguments.of("/credentials/basic"),
                Arguments.of("/"),                
                Arguments.of("/jobs"),
                Arguments.of("/collections"),
                Arguments.of("/.well-known/openeo"),
                Arguments.of("/"));
    }
}
