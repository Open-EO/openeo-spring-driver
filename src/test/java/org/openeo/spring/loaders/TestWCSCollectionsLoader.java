package org.openeo.spring.loaders;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openeo.spring.loaders.WCSCollectionsLoader.BANDS_DIM;
import static org.openeo.spring.model.Dimension.TypeEnum.BANDS;
import static org.openeo.spring.model.Dimension.TypeEnum.OTHER;
import static org.openeo.spring.model.Dimension.TypeEnum.SPATIAL;
import static org.openeo.spring.model.Dimension.TypeEnum.TEMPORAL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openeo.spring.api.LinkRelType;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.CollectionSpatialExtent;
import org.openeo.spring.model.CollectionSummaries;
import org.openeo.spring.model.CollectionTemporalExtent;
import org.openeo.spring.model.Dimension;
import org.openeo.spring.model.Dimension.TypeEnum;
import org.openeo.spring.model.DimensionBands;
import org.openeo.spring.model.DimensionSpatial;
import org.openeo.spring.model.DimensionTemporal;
import org.openeo.spring.model.HasUnit;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Providers;

/**
 * Unit tests for the {@link WCSCollectionsLoader} class.
 */
// TODO fetch and compare metadata attributes from XML input to unmarshalled object
@DisplayName("WCS collections loader")
@Disabled("Unsatisfied link error to GDAL libraries: add binding to JUnit configuration")
class TestWCSCollectionsLoader {

    static final Logger log = LogManager.getLogger(TestWCSCollectionsLoader.class);

    private static Resource txyoo_res = Resource.TYXOO_COV;
    private static Resource oxyoo_res = Resource.OYXOO_COV;

    private static String txyoo_id = txyoo_res.getBaseName();
    private static String oxyoo_id = oxyoo_res.getBaseName();

    private static InputStream txyoo_is;
    private static InputStream oxyoo_is;
    private static Providers extraProvider;

    private static Collection txyoo_coll;
    private static Collection oxyoo_coll;

    @BeforeAll
    @Order(1)
    static void setup() throws IOException, URISyntaxException {
        
        // TEST
        printEnvLibs(false);

        txyoo_is = txyoo_res.getInputStream();
        oxyoo_is = oxyoo_res.getInputStream(); // no time dimension

        extraProvider = new Providers()
                .name("Eurac_EO_WCS")
//                .description("Test provider.")
                .roles("host")
                .url(new URI("http://www.eurac.edu"));

        txyoo_id = txyoo_res.getBaseName();
        oxyoo_id = oxyoo_res.getBaseName();
        
        oxyoo_coll = WCSCollectionsLoader.parseCollection(oxyoo_id, oxyoo_is, extraProvider);
        txyoo_coll = WCSCollectionsLoader.parseCollection(txyoo_id, txyoo_is, extraProvider);
    }

    @AfterAll
    static void tearDown() throws IOException {
        for (InputStream is : Arrays.asList(txyoo_is, oxyoo_is)) {
            if (null != is) {
                is.close();
            }
        }
    }

    @Nested
    @DisplayName("5D spatial coverage")
    class testSpatial5D implements ICollectionTester {

        // o* = oracle*
        private String oId = oxyoo_id;
        private String[] oDLabels = "year Y X M rcp".split(" ");
        private int ix = 2;
        private int iy = 1;
        private TypeEnum[] oDTypes = { OTHER, SPATIAL, SPATIAL, OTHER, OTHER };
        private String[] oDUnits = "a m m GridSpacing GridSpacing".split(" ");
        private int oSrs = 3035;
        private String oBand = "rx1day";
        // TODO fetch this and all other from XML

        @Override
        public Collection getCollection() {
            return oxyoo_coll;
        }

        @Test
        @Override
        @DisplayName("collection id")
        public void testId() {
            Collection coll = getCollection();
            assertEquals(oId, coll.getId(), "Cube has incorrect id");
        }

        @Test
        @Override
        @DisplayName("dimensions")
        public void testDimensions() {
            Collection coll = getCollection();

            Map<String, Dimension> dimsMap = coll.getCubeColonDimensions();
            assertEquals(5+1, dimsMap.size(), "Cube does not have 5+1 dimensions");

            // for each dimension:
            final AtomicInteger i = new AtomicInteger(0);
            while (i.get() < oDLabels.length) {

                String label = oDLabels[i.get()];
                assertTrue(dimsMap.containsKey(label), "Collection is missing a dimension.");

                Dimension dim = dimsMap.get(label);

                // grouped asserts
                assertAll(label,
                        () -> assertEquals(oDTypes[i.get()], dim.getType(),
                                "Dimension has wrong type"),

                        () -> assertEquals(oDUnits[i.get()], ((HasUnit) dim).getUnit(),
                                "Dimension has wrong unit")

//                        () -> assertNotNull(dim.getDescription(),
//                                "Dimension has no description")
                        );
                i.incrementAndGet();
            }

            DimensionSpatial xDim = (DimensionSpatial) dimsMap.get(oDLabels[ix]);
            DimensionSpatial yDim = (DimensionSpatial) dimsMap.get(oDLabels[iy]);
            assertAll("srs",
                    () -> assertEquals(oSrs, xDim.getReferenceSystem(), "wrong spatial CRS (x)."),
                    () -> assertEquals(oSrs, yDim.getReferenceSystem(), "wrong spatial CRS (y)."));
        }

        @Test
        @Override
        @DisplayName("bands")
        public void testBands() {
            Collection coll = getCollection();
            Map<String, Dimension> dimsMap = coll.getCubeColonDimensions();

            assertTrue(dimsMap.containsKey(BANDS_DIM), "collection has no bands");

            Dimension dim = dimsMap.get(BANDS_DIM);
            assertTrue(dim instanceof DimensionBands, "bands collection has wrong class");
            assertEquals(BANDS, dim.getType(), "bands collection has wrong type");

            DimensionBands bands = (DimensionBands) dim;
            List<String> names = bands.getValues();

            assertAll("names",
                    () -> assertEquals(1, names.size(), "collection should have 1 band"),
                    () -> assertEquals(oBand, names.get(0), "band has wrong name")
                    );
        }

        @Test
        @Override
        @DisplayName("extent")
        public void testExtent() {
            Collection coll = getCollection();

            CollectionSpatialExtent spExt = coll.getExtent().getSpatial();
            CollectionTemporalExtent tExt = coll.getExtent().getTemporal();

            // being the first array the overall extent, etc
            assertAll("spatial",
                    () -> assertNotNull(spExt, "collection should have a spatial extent"),
                    () -> assertEquals(1, spExt.getBbox().size(), "there is only 1 main bbox"),
                    () -> assertEquals(2*2, spExt.getBbox().get(0).size(), "bbox is spatially 2D")
                    );

            assertAll("temporal",
                    () -> assertNotNull(tExt, "collection should have a temporal extent"),
                    () -> assertEquals(0, tExt.getInterval().size(), "time extent is empty"));
            // FIXME: time extent should always be filled with some timestamps even when time dimension is missing
        }

        @Test
        @Override
        @DisplayName("core metadata")
        public void testCoreMetadata() {
            Collection coll = getCollection();

            assertEquals(Collection.TYPE, coll.getType(), "collection should be of type 'Collection'");
            assertNotNull(coll.getTitle(), "collection should have a title");
            assertNotNull(coll.getDescription(), "collection should have a description");
            assertNotNull(coll.getLicense(), "collection should have a licence");
            assertNotNull(coll.getVersion(), "collection should have a version");
            assertEquals(5, coll.getKeywords().size(), "collection has unexpected number of keywords");
        }

        @Test
        @Override
        @DisplayName("stac extensions")
        public void testExtensions() {
            Collection coll = getCollection();
            List<String> exts = WCSCollectionsLoader.STAC_EXTENSIONS;

            assertTrue(coll.getStacExtensions().containsAll(exts), "collection does not declare all extensions");
        }

        @Test
        @Override
        @DisplayName("links")
        public void testLinks() {
            Collection coll = getCollection();
            List<Link> links = coll.getLinks();

            assertEquals(2, links.size(), "collection has unexpected number of links");

            assertAll("type",
                    () -> assertEquals(LinkRelType.LICENCE.toString(), links.get(0).getRel(),
                            "collection's 1st link is not of type licence"),
                    () -> assertEquals(LinkRelType.ABOUT.toString(), links.get(1).getRel(),
                            "collection's 2nd link is not of type about")
                    );
        }

        @Test
        @Override
        @DisplayName("providers")
        public void testProviders() {
            Collection coll = getCollection();

            assertEquals(2+1, coll.getProviders().size(), "collection should have 3 providers");
        }

        @Test
        @Override
        @DisplayName("summaries")
        public void testSummaries() {
            Collection coll = getCollection();
            CollectionSummaries summ = coll.getSummaries();

            assertEquals(1, summ.getBands().size(), "collection should have 1 band summary");
            assertEquals(oBand, summ.getBands().get(0).getName(), "collection band summary name check");
        }

        @Test
        @Override
        @Disabled
        @DisplayName("assets")
        public void testAssets() {}

        @Test
        @Override
        @DisplayName("stac_unmarshal")
        public void testStacOracle() throws IOException {

            Collection coll = getCollection();
            Resource res = Resource.OYXOO_COLL;

            Collection oColl = JSONMarshaller.readValue(res.getInputStream() , Collection.class);

            assertEquals(oColl, coll, "GML and STAC oracle unmarshalling do not coincide");
        }
    }

    @Nested
    @DisplayName("5D spatio-temporal coverage")
    class testSpatioTemporal5D implements ICollectionTester {

        // o* = oracle*
        private String oId = txyoo_id;
        private String[] oDLabels = "t X Y M rcp".split(" ");
        private int ix = 2;
        private int iy = 1;
        private TypeEnum[] oDTypes = { TEMPORAL, SPATIAL, SPATIAL, OTHER, OTHER };
        private String[] oDUnits = "P1M m m GridSpacing GridSpacing".split(" ");
        private int oSrs = 3035;
        private String oBand = "spi12";

        @Override
        public Collection getCollection() {
            return txyoo_coll;
        }

        @Test
        @Override
        @DisplayName("collection id")
        public void testId() {
            Collection coll = getCollection();
            assertEquals(oId, coll.getId(), "Cube has incorrect id");
        }

        @Test
        @Override
        @DisplayName("dimensions")
        public void testDimensions() {
            Collection coll = getCollection();

            Map<String, Dimension> dimsMap = coll.getCubeColonDimensions();
            assertEquals(5+1, dimsMap.size(), "Cube does not have 5+1 dimensions");

            // for each dimension:
            final AtomicInteger i = new AtomicInteger(0);
            while (i.get() < oDLabels.length) {

                String label = oDLabels[i.get()];
                assertTrue(dimsMap.containsKey(label), "Collection is missing a dimension.");

                Dimension dim = dimsMap.get(label);

                // grouped asserts
                String unit = (dim instanceof HasUnit)
                        ? ((HasUnit) dim).getUnit()
                        : ((DimensionTemporal) dim).getStep(); // risky bet
                
                assertAll(label,
                        () -> assertEquals(oDTypes[i.get()], dim.getType(),
                                "Dimension has wrong type"),

                        () -> assertEquals(oDUnits[i.get()], unit,
                                "Dimension has wrong unit")

//                        () -> assertNotNull(dim.getDescription(),
//                                "Dimension has no description")
                        );
                i.incrementAndGet();
            }

            DimensionSpatial xDim = (DimensionSpatial) dimsMap.get(oDLabels[ix]);
            DimensionSpatial yDim = (DimensionSpatial) dimsMap.get(oDLabels[iy]);
            assertAll("srs",
                    () -> assertEquals(oSrs, xDim.getReferenceSystem(), "wrong spatial CRS (x)."),
                    () -> assertEquals(oSrs, yDim.getReferenceSystem(), "wrong spatial CRS (y)."));
        }

        @Test
        @Override
        @DisplayName("bands")
        public void testBands() {
            Collection coll = getCollection();
            Map<String, Dimension> dimsMap = coll.getCubeColonDimensions();

            assertTrue(dimsMap.containsKey(BANDS_DIM), "collection has no bands");

            Dimension dim = dimsMap.get(BANDS_DIM);
            assertTrue(dim instanceof DimensionBands, "bands collection has wrong class");
            assertEquals(BANDS, dim.getType(), "bands collection has wrong type");

            DimensionBands bands = (DimensionBands) dim;
            List<String> names = bands.getValues();

            assertAll("names",
                    () -> assertEquals(1, names.size(), "collection should have 1 band"),
                    () -> assertEquals(oBand, names.get(0), "band has wrong name")
                    );
        }

        @Test
        @Override
        @DisplayName("extent")
        public void testExtent() {
            Collection coll = getCollection();

            CollectionSpatialExtent spExt = coll.getExtent().getSpatial();
            CollectionTemporalExtent tExt = coll.getExtent().getTemporal();

            // being the first array the overall extent, etc
            assertAll("spatial",
                    () -> assertNotNull(spExt, "collection should have a spatial extent"),
                    () -> assertEquals(1, spExt.getBbox().size(), "there is only 1 main bbox"),
                    () -> assertEquals(2*2, spExt.getBbox().get(0).size(), "bbox is spatially 2D")
                    );

            assertAll("temporal",
                    () -> assertNotNull(tExt, "collection should have a temporal extent"),
                    () -> assertEquals(1, tExt.getInterval().size(), "time extent exists"),
                    () -> assertEquals(2, tExt.getInterval().get(0).size(), "time interval is filled"));
            // FIXME: time extent should always be filled with some timestamps even when time dimension is missing
        }

        @Test
        @Override
        @DisplayName("core metadata")
        public void testCoreMetadata() {
            Collection coll = getCollection();

            assertEquals(Collection.TYPE, coll.getType(), "collection should be of type 'Collection'");
            assertNotNull(coll.getTitle(), "collection should have a title");
            assertNotNull(coll.getDescription(), "collection should have a description");
            assertNotNull(coll.getLicense(), "collection should have a licence");
            assertNotNull(coll.getVersion(), "collection should have a version");
            assertEquals(4, coll.getKeywords().size(), "collection has unexpected number of keywords");
        }

        @Test
        @Override
        @DisplayName("stac extensions")
        public void testExtensions() {
            Collection coll = getCollection();
            List<String> exts = WCSCollectionsLoader.STAC_EXTENSIONS;

            assertTrue(coll.getStacExtensions().containsAll(exts), "collection does not declare all extensions");
        }

        @Test
        @Override
        @DisplayName("links")
        public void testLinks() {
            Collection coll = getCollection();
            List<Link> links = coll.getLinks();

            assertEquals(2, links.size(), "collection has unexpected number of links");

            assertAll("type",
                    () -> assertEquals(LinkRelType.LICENCE.toString(), links.get(0).getRel(),
                            "collection's 1st link is not of type licence"),
                    () -> assertEquals(LinkRelType.ABOUT.toString(), links.get(1).getRel(),
                            "collection's 2nd link is not of type about")
                    );
        }

        @Test
        @Override
        @DisplayName("providers")
        public void testProviders() {
            Collection coll = getCollection();

            assertEquals(2+1, coll.getProviders().size(), "collection should have 3 providers");
        }

        @Test
        @Override
        @DisplayName("summaries")
        public void testSummaries() {
            Collection coll = getCollection();
            CollectionSummaries summ = coll.getSummaries();

            assertEquals(1, summ.getBands().size(), "collection should have 1 band summary");
            assertEquals(oBand, summ.getBands().get(0).getName(), "collection band summary name check");
        }

        @Test
        @Override
        @Disabled
        @DisplayName("assets")
        public void testAssets() {}

        @Test
        @Override
        @DisplayName("stac_unmarshal")
        public void testStacOracle() throws IOException {

            Collection coll = getCollection();
            Resource res = Resource.TYXOO_COLL;

            Collection oColl = JSONMarshaller.readValue(res.getInputStream() , Collection.class);

            assertEquals(oColl, coll, "GML and STAC oracle unmarshalling do not coincide");
        }
    }
    
    /**
     * Prints the version of PROJ and GDAL bound libraries + all system runtime environment. 
     */
    private static void printEnvLibs(boolean env) {
        ////////////////////////////////////////////////////////////////////////////
        // Environment
        if (env) {
            System.out.println("ENVIRONMENT\n--------");
            for (Map.Entry<String, String> envItem : System.getenv().entrySet()) {
                System.out.format("%s=%s%n", envItem.getKey(), envItem.getValue());
            }
            System.out.println("--------");
        }
        
        // GDAL version
        System.out.println(String.format("GDAL VERSION >>> %s",
                org.gdal.gdal.gdal.VersionInfo("RELEASE_NAME")));
        
        // PROJ version
        System.out.println(String.format("PROJ VERSION >>> %d.%d.%d",
                org.gdal.osr.osr.GetPROJVersionMajor(),
                org.gdal.osr.osr.GetPROJVersionMinor(),
                org.gdal.osr.osr.GetPROJVersionMicro()));
        ////////////////////////////////////////////////////////////////////////////
    }
}
