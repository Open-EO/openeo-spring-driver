package org.openeo.spring.loaders;

import org.openeo.spring.model.Collection;

/**
 * Blueprint for test classes on STAC collections.
 */
interface ICollectionTester {

    /** Gets the collection that is under test. */
    Collection getCollection();

    /** Tests that the collection's {@code id} is correct. */
    void testId();

    /** Tests the collection's core metadata attributes (engine, extensions, title, etc). */
    void testCoreMetadata();

    /** Tests that the proper collection's STAC extensions are declared. */
    void testExtensions();

    /** Tests the collection dimensions' (spatial, temporal, or other) cardinality and semantics. */
    void testDimensions();

    /** Tests the collection bands' cardinality and semantics. */
    void testBands();

    /** Tests the collection's {@code extent} element. */
    void testExtent();

    /** Tests the collection's {@code links} element. */
    void testLinks();

    /** Tests the collection's {@code providers} element. */
    void testProviders();

    /** Tests the collection's {@code summaries} element. */
    void testSummaries();

    /** Tests the collection's {@code assets} element. */
    void testAssets();
}
