package org.openeo.spring.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-02T08:31:05.442+02:00[Europe/Rome]")
@Controller
@RequestMapping("${openapi.openEO.base-path:/api/v1.0}")
public class FilesApiController implements FilesApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public FilesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
