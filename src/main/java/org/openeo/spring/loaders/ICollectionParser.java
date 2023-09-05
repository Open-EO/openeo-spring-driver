package org.openeo.spring.loaders;

import java.util.concurrent.Callable;

import org.openeo.spring.model.Collection;

/**
 * Tagging interface for callable tasks that return a collection as result.
 */
public interface ICollectionParser extends Callable<Collection> {}

