package org.openeo.spring.conformance;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Conformance classes related to OGC API specs.
 */
public interface OgcApiConformance {

    /** Gets the URI associated with the conformance class. */
    URI getUri();

    /**
     * OGC API - Common (Part 1 & 2)
     * @see https://docs.ogc.org/is/19-072/19-072.html
     * @see https://docs.ogc.org/DRAFTS/20-024.html
     */
    enum Common implements OgcApiConformance {
        CORE("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/core"),
        COLLECTIONS("http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/collections");

        private final String uri;

        private Common(String uri) {
            this.uri = uri;
        }

        @Override
        public URI getUri() {
            try {
                return new URI(uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid conformance URI: " + uri);
            }
        }
    }

    /**
     * OGC API - Coverages
     * @see https://docs.ogc.org/DRAFTS/19-087.html 
     */
    enum Coverages implements OgcApiConformance {
        CORE("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/core"),
        SCALING("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/scaling"),
        SUBSETTING("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/subsetting"),
        FIELD_SELECTION("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/fieldselection"),
        CRS("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/crs"),
        COVERAGE_TILES("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/tiles"),
        COVERAGE_SCENES("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/scenes"),
        HTML("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/html"),
        GeoTIFF("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geotiff"),
        netCDF("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/netcdf"),
        CIS_JSON("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/cisjson"),
        CoverageJSON("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/coveragejson"),
        LAS("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/las"),
        LASZip("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/laszip"),
        PNG("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/png"),
        JPEG_XL("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/jpegxl"),
        JPEG_2000("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/jpeg2000"),
        GEO_ZARR("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/zarr"),
        OpenAPI3("http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/oas30");

        private final String uri;

        private Coverages(String uri) {
            this.uri = uri;
        }

        @Override
        public URI getUri() {
            try {
                return new URI(uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid conformance URI: " + uri);
            }
        }
    }
}
