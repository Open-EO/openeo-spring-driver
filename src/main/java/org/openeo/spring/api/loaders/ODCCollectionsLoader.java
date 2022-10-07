package org.openeo.spring.api.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openeo.spring.api.CollectionsApiController;
import org.openeo.spring.api.loaders.WCSCollectionsLoader.Builder;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.Providers;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Loader of collections from a (remote) ODC collections catalogue.
 */
public class ODCCollectionsLoader implements ICollectionsLoader {

    private final Logger log = LogManager.getLogger(ODCCollectionsLoader.class);

    /** Characters set assumed in remote catalogs. */
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /*
     * members
     */
    private final String endpoint;
    private final Resource cache;

    /** Constructor. */
    private ODCCollectionsLoader(String endpoint, Resource cache) {
        this.endpoint = endpoint;
        this.cache = cache;
    }

    /**
     * Internal loader builder.
     */
    public static class Builder {

        private final String endpoint;
        private Resource cache;

        private Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        public static Builder of(String endpoint) {
            return new Builder(endpoint);
        }

        public Builder cache(Resource p) {
            cache = p;
            return this;
        }

        public ODCCollectionsLoader build() {
            check();
            ODCCollectionsLoader obj = new ODCCollectionsLoader(endpoint, cache);
            return obj;
        }

        private void check() {
            boolean ok = true;
            ok &= (null != cache);
            ok &= !cache.getFilename().isEmpty();

            if (!ok) {
                throw new RuntimeException("Invalid loader construction.");
                // TODO add reason
            }
        }
    }

    @Override
    public Collections call() throws Exception {

        Collections collectionsList = new Collections();
        JSONObject odcSTACMetdata =  readJsonFromUrl(this.endpoint);

        if (odcSTACMetdata != null) {

            // JSONObject odcCollections = odcSTACMetdata.getJSONObject("collections");
            final String odcSTACMetdataStr = odcSTACMetdata.toString(4);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            // parallelize writing of catalog to disk
            new Thread(() -> {
                log.info("Dumping ODC catalogue to file...");

                try {
                    String absPath = String.format("%s/%s".formatted(
                            CollectionsApiController.CACHE_ROOT_DIR,
                            this.cache.getFilename()));
                    File collectionsFile = new File(absPath);

                    if (!collectionsFile.exists()) {
                        collectionsFile.createNewFile();
                    }

                    try (FileWriter file = new FileWriter(collectionsFile)) {
                        file.write(odcSTACMetdataStr);
                        log.info("ODC catalogue serialized: {}.", collectionsFile.getName());
                    }
                } catch (IOException e) {
                    log.error("Error while serializing ODC catalog to disk.", e);
                }
            }, "ODCCatalogueSerializer").start();

            try {
                collectionsList = mapper.readValue(odcSTACMetdataStr, Collections.class);
                //collectionsList = mapper.readValue(collectionsFile, Collections.class);
            } catch (JsonProcessingException e) {
                log.error("Error while unmarshalling remote catalog '{}'", this.endpoint, e);
            }
        }
        return collectionsList;
    }

    @Override
    public EngineTypes getEngineType() {
        return EngineTypes.ODC_DASK;
    }


    /**
     * Parses the content of a document to JSON.
     *
     * @param url     the URL of the JSON document
     * @param charset the characters encoding of the remote document
     * @return the global {@code JSONObject} object representing the unmarshalled document;
     *         {@code null} if the document cannot not be fetch or is invalid.
     */
    private JSONObject readJsonFromUrl(String url, Charset charset) {

        JSONObject json = null;

        log.debug("Trying to read JSON from '{}'", url);

        try (InputStream is = new URL(url).openStream()) {

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(is, charset)
                    );
            String jsonText = readAll(rd);
//          log.debug(jsonText);
            json = new JSONObject(jsonText);

        } catch (IOException ioe) {
            log.error("Error while parsing JSON from {}", url, ioe);
        } catch (JSONException je) {
            log.error("Error while parsing JSON from {}", url, je);
        }

        return json;
    }

    /**
     * Override method with default UTF-8 character encoding.
     *
     * @param url     the URL of the JSON document
     * @see #readJsonFromUrl(String, Charset)
     */
    private JSONObject readJsonFromUrl(String url) {
        return readJsonFromUrl(url, UTF8);
    }

    /** Reads all chars from an input stream onto a string. */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
