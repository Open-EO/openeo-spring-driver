package org.openeo.spring.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Access to resources for the test suites.
 */
public enum Resource {
    TYXOO_COV("spi12_19712100_tnst_1x1km_mon_cordexadj_qdm.xml"),
    TYXOO_COLL("spi12_19712100_tnst_1x1km_mon_cordexadj_qdm.json"),
    OYXOO_COV("rx1day_19712100_tnst_1x1km_year_cordexadj_qdm.xml"),
    OYXOO_COLL("rx1day_19712100_tnst_1x1km_year_cordexadj_qdm.json");

    private /*static*/ final Path ROOT = Paths.get("src", "test", "resources");

    private final Path path;

    private Resource(String path) {
        this.path = ROOT.resolve(path);
    }

    /** Gets the resource's path. */
    Path get() {
        return this.path;
    }

    /** Gets the basename of the resource (no directory nor suffix). */
    String getBaseName() {
        String fileName = path.getFileName().toString();
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    /** Creates an input stream on the resource. */
    InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.get());
    }
}
