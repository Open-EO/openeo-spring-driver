package org.openeo.spring.conformance;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Identifiers of link relations for OGC standards.
 */
public enum OgcLinkRelation {
    CONFORMANCE("http://www.opengis.net/def/rel/ogc/1.0/conformance");

    private final String uri;

    private OgcLinkRelation(String uri) {
        this.uri = uri;
    }

    public URI getUri() {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid conformance URI: " + uri);
        }
    }
    
    @Override
    public String toString() {
        return uri;
    }
}
