package org.openeo.spring.api.loaders;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.api.CollectionsApiController;
import org.openeo.spring.api.loaders.ODCCollectionsLoader.Builder;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Loader of collections from a local JSON STAC catalogue.
 */
public class STACFileCollectionsLoader implements ICollectionsLoader {

    private final Logger log = LogManager.getLogger(STACFileCollectionsLoader.class);

    /*
     * members
     */
    private final Resource resource;
    private final EngineTypes engineType;

    /** Constructor. */
    private STACFileCollectionsLoader(Resource resource, EngineTypes engineType) {
        this.resource = resource;
        this.engineType = engineType;
    }

    /**
     * Internal loader builder.
     */
    public static class Builder {

        private final Resource resource;
        private EngineTypes engine;

        private Builder(Resource resource) {
            this.resource = resource;
        }

        public static Builder of(Resource resource) {
            return new Builder(resource);
        }

        public Builder engine(EngineTypes type) {
            engine = type;
            return this;
        }

        public STACFileCollectionsLoader build() {
            check();
            STACFileCollectionsLoader obj = new STACFileCollectionsLoader(resource, engine);
            return obj;
        }

        private void check() {
            boolean ok = true;
            ok &= (null != resource);
            ok &= (null != engine);

            if (!ok) {
                throw new RuntimeException("Invalid loader construction.");
                // TODO add reason
            }
        }
    }

    @Override
    public Collections call() throws Exception {

        Collections collectionsList = new Collections();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String absPath = String.format("%s/%s",
                CollectionsApiController.CACHE_ROOT_DIR,
                this.resource.getFilename());

        log.debug("Importing {} collections catalogue from file: {}", engineType, absPath);

        try {
            File collectionsFile = new File(absPath);
            collectionsList = mapper.readValue(collectionsFile, Collections.class);
        } catch (IOException e) {
            log.error("Error while unmarshalling '{}'.", absPath, e);
        }
        return collectionsList;
    }

    @Override
    public EngineTypes getEngineType() {
        return this.engineType;
    }
}
