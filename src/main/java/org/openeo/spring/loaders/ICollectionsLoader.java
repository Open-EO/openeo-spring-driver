package org.openeo.spring.loaders;

import java.util.concurrent.Callable;

import org.openeo.spring.model.Collections;
import org.openeo.spring.model.EngineTypes;

/**
 * Base interface for all loader tasks.
 */
public interface ICollectionsLoader extends Callable<Collections> {

    /** Returns the type of engine from where the collections are fetched. */
    EngineTypes getEngineType();

}
