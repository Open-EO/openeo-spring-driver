package org.openeo.spring.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-30T14:48:14.663+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:/api/v1.0}")
public class MeApiController implements MeApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public MeApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
