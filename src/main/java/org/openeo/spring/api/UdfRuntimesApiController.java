package org.openeo.spring.api;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:45:00.334+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:/api/v1.0}")
public class UdfRuntimesApiController implements UdfRuntimesApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UdfRuntimesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
