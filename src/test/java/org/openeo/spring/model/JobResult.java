package org.openeo.spring.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Access to resources for the test suites.
 */
public enum JobResult {
    TEST_FEATURE("stac_feature.200.json"),
    TEST_COLL("stac_collection.200.json");

    private /*static*/ final Path ROOT = Paths.get("src", "test", "resources", "job_results");

    private final Path path;

    private JobResult(String path) {
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
    
    /**
     * Loads the content of the result file to a string object.
     */
    String loadText() {
        String result = null;
        try {
            result = new BufferedReader(
                    new InputStreamReader(getInputStream()))
                .lines().parallel().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Missing test resource: " + this);
        }
        
        return result;
    }
}
