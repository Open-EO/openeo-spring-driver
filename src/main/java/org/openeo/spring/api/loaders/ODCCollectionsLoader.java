package org.openeo.spring.api.loaders;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openeo.spring.api.CollectionsApiController;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Loader of collections from a (remote) ODC collections catalogue.
 */
public class ODCCollectionsLoader implements ICollectionsLoader {

    private final Logger log = LogManager.getLogger(ODCCollectionsLoader.class);

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
        JSONObject odcSTACMetdata =  JSONMarshaller.readJsonFromUrl(this.endpoint);

        if (odcSTACMetdata != null) {

            // JSONObject odcCollections = odcSTACMetdata.getJSONObject("collections");
            final String odcSTACMetdataStr = odcSTACMetdata.toString(4);

            // parallelize writing of catalog to disk
            new Thread(() -> {
                try {
                    String absPath = String.format("%s/%s",
                            CollectionsApiController.CACHE_ROOT_DIR,
                            this.cache.getFilename());
                    File collectionsFile = new File(absPath);

                    JSONMarshaller.syncWiteToFile(odcSTACMetdata, collectionsFile);

                } catch (IOException e) {
                    log.error("Error while serializing ODC catalog to disk.", e);
                }
            }, "ODCCatalogueSerializer").start();

            try {
                collectionsList = JSONMarshaller.readValue(odcSTACMetdataStr, Collections.class);
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
}
