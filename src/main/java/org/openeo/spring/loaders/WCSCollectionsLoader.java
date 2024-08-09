package org.openeo.spring.loaders;

import static org.openeo.spring.loaders.CRSUtils.EPSG_WGS84;
import static org.openeo.spring.loaders.CRSUtils.TEMPORAL_AXIS_LABELS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.openeo.spring.api.CollectionsApiController;
import org.openeo.spring.api.DefaultApiController;
import org.openeo.spring.api.LinkRelType;
import org.openeo.spring.loaders.CRSUtils.AxisMappingStrategy;
import org.openeo.spring.loaders.CRSUtils.CSAxisOrientation;
import org.openeo.spring.loaders.CRSUtils.CsType;
import org.openeo.spring.model.Asset;
import org.openeo.spring.model.BandSummary;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.CollectionExtent;
import org.openeo.spring.model.CollectionSpatialExtent;
import org.openeo.spring.model.CollectionSummaries;
import org.openeo.spring.model.CollectionSummaryStats;
import org.openeo.spring.model.CollectionTemporalExtent;
import org.openeo.spring.model.Collections;
import org.openeo.spring.model.Dimension;
import org.openeo.spring.model.Dimension.TypeEnum;
import org.openeo.spring.model.DimensionBands;
import org.openeo.spring.model.DimensionOther;
import org.openeo.spring.model.DimensionSpatial;
import org.openeo.spring.model.DimensionSpatial.AxisEnum;
import org.openeo.spring.model.DimensionTemporal;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.HasUnit;
import org.openeo.spring.model.Link;
import org.openeo.spring.model.Providers;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loader of collections from a (remote) WCS coverages catalogue.
 */
public class WCSCollectionsLoader implements ICollectionsLoader {

    private static final Logger log = LogManager.getLogger(WCSCollectionsLoader.class);

    /** STAC spec. version used. */
    // FIXME this should be dictated by the model implemented
    public static final String STAC_VERSION = DefaultApiController.IMPLEMENTED_STAC_VERSION;

    /**
     * The STAC extensions used in the catalog.
     *
     * @see <a href="https://github.com/stac-extensions">STAC extensions GitHub repositories</a>
     */
    public static final List<String> STAC_EXTENSIONS = Arrays.asList(
            "https://stac-extensions.github.io/datacube/v1.0.0/schema.json", // datacube:
            "https://stac-extensions.github.io/eo/v1.0.0/schema.json", // eo:
            "https://stac-extensions.github.io/scientific/v1.0.0/schema.json" // sci:
    );

    /** The version assigned to a collection when not stated otherwise in the input. */
    public static final String DEFAULT_COLL_VERSION = "v1";

    /** Label of the dimension of type "bands" where to put variables. */
    // NOTE: datacube STAC extension v2 adds "variables": we should use that to store GML rangeType fields
    // https://github.com/stac-extensions/datacube/blob/main/examples/item.json
    public static final String BANDS_DIM = "bands";

    /** Default internal concurrency. */
    public static final int DEFAULT_NTHREADS = 5;

    /**
     * Index of elements not found in a Java lists.
     * @see List#indexOf(Object)
     */
    private static final int NOT_FOUND = -1;

    /*
     * members
     */
    private final String endpoint;
    private final String serviceVersion;
    private final Providers provider = new Providers();
    private final Resource cache;
    private final int nthreads; // TODO internal parallelism

    /** Constructor. */
    private WCSCollectionsLoader(String endpoint, String serviceVersion,
            String providerName, String providerType, URI providerUrl,
            Resource cache, int nthreads) {

        this.endpoint = endpoint;
        this.serviceVersion = serviceVersion;
        this.provider
            .name(providerName)
            .roles(providerType)
            .url(providerUrl);

        this.cache = cache;
        this.nthreads = nthreads;
    }

    /** Constructor override. */
    private WCSCollectionsLoader(String endpoint, String serviceVersion, Providers provider,
            Resource cachePath, int nthreads) {

        this(endpoint, serviceVersion,
                provider.getName(),
                provider.getRoles().get(0),
                provider.getUrl(),
                cachePath, nthreads);
    }

    /**
     * Internal loader builder.
     */
    public static class Builder {

        private final String endpoint;
        private String version = "";
        private Providers provider = new Providers();
        private Resource cache;
        private int nthreads = DEFAULT_NTHREADS;

        private Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        public static Builder of(String endpoint) {
            return new Builder(endpoint);
        }

        public Builder version(String v) {
            this.version = v;
            return this;
        }

        public Builder provider(Providers p) {
            provider = p;
            return this;
        }

        public Builder cache(Resource p) {
            cache = p;
            return this;
        }

        /**
         * The number of threads that participate in the execution pool.
         * Use a number <=0 to let the internal default concurrency
         * settings; 1 for sequential execution.
         */
        public Builder parallelism(int n) {
            nthreads = (n > 0) ? n : DEFAULT_NTHREADS;
            return this;
        }

        public WCSCollectionsLoader build() {
            check();
            WCSCollectionsLoader obj = new WCSCollectionsLoader(endpoint, version, provider, cache, nthreads);
            return obj;
        }

        private void check() {
            boolean ok = true;
            ok &= !version.isEmpty();
            ok &= !provider.getName().isEmpty();
            ok &= !provider.getUrl().toString().isEmpty();
            ok &= (provider.getRoles().size() == 1);
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
        InputStream wcpsInputStream;
        URL urlWCPS;
        HttpURLConnection conn;

        try {
            urlWCPS = new URL(String.format("%s?"
                    + "SERVICE=WCS&"
                    + "VERSION=%s&"
                    + "REQUEST=GetCapabilities",
                    endpoint, serviceVersion));
            conn = (HttpURLConnection) urlWCPS.openConnection();
            conn.setRequestMethod("GET");
            wcpsInputStream = conn.getInputStream();

        } catch (MalformedURLException mue) {
            log.error("Malformed rasdaman endpoint.", mue);
            return collectionsList;
        } catch (IOException ioe) {
            log.error("Could not fetch rasdaman collections.", ioe);
            return collectionsList;
        }

        SAXBuilder builder = new SAXBuilder();
        Document capabilititesDoc;
        try {
            capabilititesDoc = builder.build(wcpsInputStream);
        } catch (JDOMException e) {
            log.error("Invalid rasdaman WCS capabilities document.", e);
            return collectionsList;
        } catch (IOException e) {
            log.error("Error while parsing rasdaman WCS capabilities document.", e);
            return collectionsList;
        }

        Element rootNodeCollectionsList = capabilititesDoc.getRootElement();
        Namespace defaultNSCollectionsList = rootNodeCollectionsList.getNamespace();
        log.trace("root node info: " + rootNodeCollectionsList.getName());

        List<Element> coverageList = rootNodeCollectionsList
                .getChildren("Contents", defaultNSCollectionsList).get(0)
                .getChildren("CoverageSummary", defaultNSCollectionsList);

        /*
         * go through each WCS coverage and convert it to STAC collection:
         */
        // TODO factor out to helper file all sub-tasks

        final ExecutorService exec = Executors.newFixedThreadPool(nthreads,
                new CollectionHandlerThreadFactory(this.getEngineType().name(), "WCSCoverageLoader-"));

        List<Future<Collection>> parsedCollections = new ArrayList<>();

        /*
         * Parallel parsing of all coverages in the capabilities list:
         */
        for (Element coverage : coverageList) {

            String coverageID = coverage.getChildText("CoverageId", defaultNSCollectionsList);

            parsedCollections.add(exec.submit(
                    new ICollectionParser() {
                        @Override
                        public Collection call() {
                            return parseCollection(coverageID);
                        }
                    }));
        }

        /*
         * collect the results
         */
        for (Future<Collection> result : parsedCollections) {
            Collection coll = result.get();
            if (null != coll) {
                 collectionsList.addCollectionsItem(coll);
            }
        }

        /*
         * report failed imports
         */
        int nOk  = collectionsList.size();
        int nTot = parsedCollections.size();
        if (nTot > nOk) {
            log.warn("{} coverages out of {} failed to be imported.", (nTot-nOk), nTot);
        }

        // cache catalog to JSON file:
        // TODO sync/async options
        new Thread(() -> {
            try {
                log.info("Dumping WCS catalogue to file...");

                String absPath = String.format("%s/%s",
                        CollectionsApiController.CACHE_ROOT_DIR,
                        this.cache.getFilename());
                File collectionsFile = new File(absPath);

                JSONMarshaller.syncWiteToFile(collectionsList, collectionsFile);
                log.info("WCS catalogue serialized: {}.", collectionsFile.getName());

            } catch (JsonGenerationException | JsonMappingException jse) {
                log.error("JSON error while serializing the WCPS catalog.", jse);

            } catch (IOException ioe) {
                log.error("I/O error while serializing the WCPS catalog to file.", ioe);
            }
        }, "WCSCatalogueSerializer").start();

        return collectionsList;
    }

    @Override
    public EngineTypes getEngineType() {
        return EngineTypes.WCPS;
    }

    /**
     * Parses a WCS coverage element onto a STAC collection.
     *
     * @param coverageID  the ID of the coverage to be used to fetch its description (WCS DescribeCoverage)
     * @return the STAC collection parsed from the WCS {@code coverage};
     *         {@code null} in case of errors/problems.
     */
    private Collection parseCollection(String coverageID) {

        log.debug("Fetching description of coverage '{}'", coverageID);
        
        Collection collection = null;

        URL urlCollections;
        HttpURLConnection connCollections;
        try {
            urlCollections = new URL(String.format("%s?"
                    + "SERVICE=WCS&"
                    + "VERSION=%s&"
                    + "REQUEST=DescribeCoverage&"
                    + "COVERAGEID=%s",
                    endpoint, serviceVersion, coverageID));
            connCollections = (HttpURLConnection) urlCollections.openConnection();
            connCollections.setRequestMethod("GET");
            try (InputStream currentCollectionInputStream = connCollections.getInputStream()) {
                collection = parseCollection(coverageID, currentCollectionInputStream, this.provider);
            }
        } catch (MalformedURLException mue) {
            log.error("Malformed rasdaman coverage URL.", mue);
        } catch (IOException ioe) {
            log.error("Could not fetch metadata of rasdaman coverage '{}'.", coverageID, ioe);
        }

        return collection;
    }

    /**
     * Overload method that parses a collection from an input stream.
     * @see #parseCollection(String)
     */
    static Collection parseCollection(String coverageID, InputStream stream,
            Providers ... extraProviders) {

        Collection currentCollection = new Collection();
        currentCollection.setEngine(EngineTypes.WCPS);

        log.debug("coverage info: {}", coverageID);

        currentCollection.setId(coverageID);
        currentCollection.setStacVersion(STAC_VERSION);

        SAXBuilder builderInt = new SAXBuilder();
        Document descriptionDocInt;
        try {
            descriptionDocInt = builderInt.build(stream);
        } catch (JDOMException e) {
            log.error("Invalid rasdaman '{}' coverage document.", coverageID, e);
            return null;
        } catch (IOException e) {
            log.error("Error while parsing rasdaman '{}' coverage document.", coverageID, e);
            return null;
        }

        List<Namespace> namespaces = descriptionDocInt.getNamespacesIntroduced();
        Element rootNode = descriptionDocInt.getRootElement();
        Namespace defaultNS = rootNode.getNamespace();
        Namespace gmlNS = null;
        Namespace sweNS = null;
        Namespace rasdamanNS = null;
        Namespace gmlcovNS = null;
//      Namespace gmlrgridNS = null;

        for (int n = 0; n < namespaces.size(); n++) {
            Namespace current = namespaces.get(n);
            if (current.getPrefix().equals("swe")) {
                sweNS = current;
            } else if (current.getPrefix().equals("gml")) {
                gmlNS = current;
            } else if (current.getPrefix().equals("rasdaman")) {
                rasdamanNS = current;
            } else if (current.getPrefix().equals("gmlcov")) {
                gmlcovNS = current;
//          } else if (current.getPrefix().equals("gmlrgrid")) {
//              gmlrgridNS = current;
            }
        }

        log.debug("root node info: {}", rootNode.getName());

        // bbox
        Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
        Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
        Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);

        // bbox corners
        String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
        String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");

        // (C)CRS axes labels
        String axisLabelsAttr = boundingBoxElement.getAttribute("axisLabels").getValue();
        List<String> axisLabels = Arrays.asList(axisLabelsAttr.split(" "));

        // (C)CRS axes UoMs
        String uomLabelsAttr = boundingBoxElement.getAttribute("uomLabels").getValue();
        List<String> uomLabels = Arrays.asList(uomLabelsAttr.split(" "));

        // handy lists to keep track of axes related to spatial dimensions:
        List<String> spatialAxisLabels = new ArrayList<>();
        Map<String, CSAxisOrientation> spatialAxis2Orientation= new LinkedHashMap<>();
        List<DimensionSpatial> spatialDims = new ArrayList<>();
        boolean hasSpatialCrs = false;

        // time extent
        List<String> timeAxisLabels = new ArrayList<>();
        List<DimensionTemporal> timeDims = new ArrayList<>();
        boolean hasTimeCrs = false;

        // overall extent
        CollectionExtent collectionExtent = new CollectionExtent();

        // extra metadata
        Element metadataElement = null;
        try {
            metadataElement = rootNode
                    .getChild("CoverageDescription", defaultNS)
                    .getChild("metadata", gmlcovNS)
                    .getChild("Extension", gmlcovNS)
                    .getChild("covMetadata", rasdamanNS);
        } catch (Exception e) {
            log.warn("Error in parsing bands.", e);
            // continue COLLECTION; ? skip or ignore?
        }

        /*
         * inner GRID
         */
        String gridType = null; // RectifiedGrid? ReferenceableGrid? etc.
        try {
            gridType = rootNode
                    .getChild("CoverageDescription", defaultNS)
                    .getChild("domainSet", gmlNS)
                    .getChildren().get(0)
                    .getName();
            log.debug("Grid type found: " + gridType);
        } catch (Exception e) {
            log.warn("Error while fetching grid type: {}", e.getMessage());
            return null;
        }

        String[] gridDims = null;
        try {
            gridDims = rootNode
                    .getChild("CoverageDescription", defaultNS)
                    .getChild("domainSet", gmlNS)
                    .getChild(gridType, gmlNS)
                    .getChild("limits", gmlNS)
                    .getChild("GridEnvelope", gmlNS)
                    .getChildText("high", gmlNS)
                    .split(" ");
        } catch (Exception e) {
            log.warn("Error in parsing grid dimensions: {}", e.getMessage());
            return null;
        }

        if (axisLabels.size() != gridDims.length) {
            log.warn("{}: unsupported coverage type (grid and CRS dimensions do not match).", coverageID);
            return null;
        }

        /*
         * Domain Set: DIMENSIONS
         */
        Map<String, Dimension> cubeDimensions = new HashMap<>();

        // CRS of the whole coverage (it might be compound space/time/other)
        String crsUri = boundingBoxElement.getAttributeValue("srsName");

        // Extract the single CRSs
        List<String> singleCrsUris = new ArrayList<>();
        if (crsUri.contains("/crs-compound")) {
            String[] splitted = crsUri.split("(&)?\\d=");
            if (splitted.length == 0) {
                log.error("Unrecognized compound CRS uri: {}", crsUri);
                return null;
            } else if (splitted.length == 1) {
                log.warn("Compound CRS contains only one CRS: {}", crsUri);
                singleCrsUris.add(crsUri);
            } else {
                singleCrsUris.addAll(Arrays.stream(splitted)
                        .skip(1)
                        .collect(Collectors.toList())); //Java 9: .toList());
                log.debug("Extracted {} single CRSs from: {}", (splitted.length - 1), crsUri);
            }
        } else {
            singleCrsUris.add(crsUri);
            log.debug("Coverage has single CRS: {}", crsUri);
        }

        CollectionSummaryStats epsg = new CollectionSummaryStats();
        CoordinateTransformation toWgs84 = null;
        Map<Integer, JsonNode> epsgCode2Json = new LinkedHashMap<>();
        Map<String, Integer> axisLabel2EpsgCode = new LinkedHashMap<>();

        // find the spatial EPSG and set the transform to WGS84:
        for (String uri : singleCrsUris) {
            if (uri.contains("/crs/EPSG")) {
                // check there are no multiple spatial CRSs (unsupported case):
                if (hasSpatialCrs) {
                    log.error("Multiple spatial CRSs found: {}", crsUri);
                    return null;
                }

                int epsgCode;
                try {
                    String epsgCodeStr = uri.substring(uri.lastIndexOf("/")+1);
                    epsgCode = Integer.parseInt(epsgCodeStr);
                    epsg.setMin(epsgCode);
                    epsg.setMax(epsgCode);
                    log.debug("Spatial CRS found: EPSG:" + epsgCode);

                } catch (NumberFormatException e) {
                    log.error("Unrecognized EPSG code in : " + uri);
                    return null;
                }

                // TODO see WGS84 static end of class
                final SpatialReference WGS84 = new SpatialReference();
                WGS84.ImportFromEPSG(EPSG_WGS84);

                // STAC imposes lon/lat order
                WGS84.SetAxisMappingStrategy(AxisMappingStrategy.OAMS_TRADITIONAL_GIS_ORDER.get());

                SpatialReference src = new SpatialReference();
                src.ImportFromEPSG(epsgCode);
                toWgs84 = new CoordinateTransformation(src, WGS84);

                // load JSONs to reach axes abbreviations (they match WCS coverage axes labels)
                JsonNode srcJson = epsgCode2Json.get(epsgCode);

                if (null == srcJson) {
                    try {
                        // srcGml = src.ExportToXML(/*GML_DIALECT (ignored)*/);
                        String[] strOut = { new String() };
                        src.ExportToPROJJSON(strOut);

                        ObjectMapper mapper = new ObjectMapper();
                        srcJson = mapper.readTree(strOut[0]);
                        epsgCode2Json.put(epsgCode, srcJson);

                    } catch (JsonProcessingException e) {
                        log.error("Irregular JSON format for EPSG:{}", epsgCode, e);
                        return null;
                    }
                }

                int N = src.GetAxesCount();

                for (int i = 0; i < N; ++i) {
                    String label = null;
                    String csType = null;

                    for (CsType cst : EnumSet.of(CsType.PROJCS, CsType.GEOGCS)) {
                        label = src.GetAxisName(cst.name(), i);
                        csType = cst.name();
                        if (null != label) {
                            break;
                        }
                    }

                    if (null == label) {
                        log.error("Unknown CRS type: {}", src.GetName());
//                      log.error("Unknown CRS type: EPSG:{}", epsgCode);// FIXME: install GDAL 3.x
                        return null;
                    }

                    int aoindex = src.GetAxisOrientation(csType, i);
                    CSAxisOrientation ao = CSAxisOrientation.of(aoindex);

                    // get axis abbreviation
                    String abbrev = CRSUtils.getAxisAbbrev(srcJson, i);
                    if (null == abbrev) {
                        log.error("Unsupported JSON structure for EPSG:{}:\n{}", epsgCode, srcJson);
                        return null;
                    }

                    spatialAxisLabels.add(abbrev);
                    spatialAxis2Orientation.put(abbrev, ao);
                    axisLabel2EpsgCode.put(abbrev, epsgCode);

                    log.debug("spatial axis found: {} (\"{}\", {})", label, abbrev, ao);

                    // guard
                    if (!axisLabels.contains(abbrev)) {
                        log.warn("{}: spatial axis '{}' not found in coverage definition.", coverageID, label);
                        // skip or lenient?
                    }
                }
                hasSpatialCrs = true;
            }
        }

        // go through each dimension, and identify their type

        for (String label : axisLabels) {
            log.trace("{}:{} axis", coverageID, label);
            int index = axisLabels.indexOf(label);

            String uom = uomLabels.get(index);

            /*
             * spatial axis
             */
            if (spatialAxisLabels.contains(label)) {
                // guard
                if (!hasSpatialCrs) {
                    log.error("Internal coverage parsing error: '{}' deemed as spatial.", label);
                    return null;
                }

                DimensionSpatial dim = new DimensionSpatial();
                dim.setType(TypeEnum.SPATIAL); // FIXME this should be implicit

//              int gridDim = Integer.parseInt(gridDims[index]) + 1;
                int epsgCode = axisLabel2EpsgCode.get(label);
                dim.setReferenceSystem(epsgCode);

                // axis type: easting/northing/z ?
                CSAxisOrientation csOrientation = spatialAxis2Orientation.get(label);
                AxisEnum orientation = CRSUtils.getAxisType(csOrientation);
                if (null == orientation) {
                    log.error("No orientation inferred from axis '{}'.", label);
                    return null;
                }
                log.trace(" axis '{}' -> {}", label, orientation);
                dim.setAxis(orientation);

                // set native extent
                List<BigDecimal> extent = Arrays.asList(
                        BigDecimal.valueOf(Double.parseDouble(minValues[index])),
                        BigDecimal.valueOf(Double.parseDouble(maxValues[index])));
                dim.setExtent(extent);

                // unit
                dim.setUnit(uom);

                cubeDimensions.put(label, dim);
                spatialDims.add(dim);
            }

            /*
             * temporal axis
             */
            // TODO import XML definition from SECORE and check:
            //   i) UoM is temporal?
            //   ii) type is TemporalCRS
            else if (TEMPORAL_AXIS_LABELS.contains(label)) {
                /*
                 * NOTE: temporal axis can either be a quoted timestamp (eg.
                 * "2022-12-03T09:00Z") or a time index whose meaning is encoded in the
                 * correspondent CRS, and which we recognize because of its label. (!)
                 */
                String minT = minValues[index].replaceAll("\"", "");
                String maxT = maxValues[index].replaceAll("\"", "");

                try {
                    OffsetDateTime.parse(minT);
                    hasTimeCrs = true;
                    timeAxisLabels.add(label);
                } catch (DateTimeParseException e) {
                    log.trace("Time axis is not expressed as timestamp: {}", minValues[index]); // nevermind
                }

                if (hasTimeCrs) { // "2022-12-03T09:00Z"
                    DimensionTemporal dim = new DimensionTemporal();
                    dim.setType(TypeEnum.TEMPORAL);
                    List<String> extent = Arrays.asList(minT, maxT);
                    dim.setExtent(extent);
                    cubeDimensions.put(label, dim);
                    timeDims.add(dim);
                } else { // time-encoded numeric coordinate:
                    // TODO manually derive time extent from CRS' time datum + direction + resolution
                    DimensionOther dim = new DimensionOther();
                    //dim.setType(TypeEnum.TEMPORAL); // <-- NO: otherwise marshalling creates a DimensionTemporal
                    dim.setType(TypeEnum.OTHER);    //           which is different (eg. it does not have unit)
                    List<BigDecimal> extent = new ArrayList<>(2);
                    try {
                        // integer extent
                        extent.add(BigDecimal.valueOf(Long.parseLong(minT)));
                        extent.add(BigDecimal.valueOf(Long.parseLong(maxT)));
                    } catch (NumberFormatException e) {
                        // decimal extent
                        extent.add(BigDecimal.valueOf(Double.parseDouble(minT)));
                        extent.add(BigDecimal.valueOf(Double.parseDouble(maxT)));
                    }
                    dim.setExtent(extent);
                    cubeDimensions.put(label, dim);
                }

                /*
                 * arbitrary non-spatial/non-temporal axis
                 */
            } else {
                // check STAC API on this: types of axes
                DimensionOther dim = new DimensionOther();
                dim.setType(TypeEnum.OTHER);

                //
                try {
                    // extent shall be numeric
                    // but in alternative it can have values
                    // https://github.com/stac-extensions/datacube#additional-dimension-object
                    List<BigDecimal> extent = new ArrayList<>(2);
                    try {
                        // integer extent
                        extent.add(BigDecimal.valueOf(Long.parseLong(minValues[index])));
                        extent.add(BigDecimal.valueOf(Long.parseLong(maxValues[index])));
                    } catch (NumberFormatException e) {
                        // decimal extent
                        extent.add(BigDecimal.valueOf(Double.parseDouble(minValues[index])));
                        extent.add(BigDecimal.valueOf(Double.parseDouble(maxValues[index])));
                    }
                    dim.setExtent(extent);

                    // unit
                    dim.setUnit(uom);

                    cubeDimensions.put(label, dim);

                } catch (NumberFormatException e) {
                    log.error("Unsupported extent for dimension '{}'.", label, e);
                    return null;
                }
            }
        }

        /*
         * Range Set: BANDS
         */
        List<Element> bandsMetadataList = null;
        List<Element> bandsListSwe = null;

        // hack to save some SWE metadata in eo:bands summary later on
        Map<String, String> band2SweDescr = new LinkedHashMap<>();

        DimensionBands dimensionBands = new DimensionBands();
        dimensionBands.setType(TypeEnum.BANDS);

        try {
            bandsListSwe = rootNode
                    .getChild("CoverageDescription", defaultNS)
                    .getChild("rangeType", gmlcovNS)
                    .getChild("DataRecord", sweNS)
                    .getChildren("field", sweNS);
        } catch (Exception e) {
            log.error("Error in parsing bands definition.", e);
            return null;
        }

        for (Element band : bandsListSwe) {
            String bandId = band.getAttributeValue("name");
            dimensionBands.addValuesItem(bandId);
            // TODO where does the Dimension "Bands" come from?
            // will try to put bands metadata in the eo:bands summaries.

            Element quantity = band.getChild("Quantity", sweNS);
            if (null == quantity) {
                log.warn("No SWE quantity element in range variable {}.", bandId);

            } else {
                String sweLabel = quantity.getChildText("label", sweNS);
                // ...

                String sweDescr = quantity.getChildText("description", sweNS);
                if (null != sweDescr) {
                    band2SweDescr.put(bandId, sweDescr);
                }
            }

            // uom code ...
            // nilValues ...
            // constraint ...
        }
        cubeDimensions.put(BANDS_DIM, dimensionBands);

        // ok cube:dimensions
        currentCollection.setCubeColonDimensions(cubeDimensions);

        /*
         * Overall spatio-temporal extent
         *
         * (spatial bbox shall be WGS84 lon/lat order:)
         *  https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md#spatial-extent-object
         */
        if (hasSpatialCrs) {

            final int ndims = spatialAxisLabels.size(); // either 2D or 3D

            List<Integer> indexes = spatialAxisLabels.stream()
                    .map(x -> axisLabels.indexOf(x))
                    .collect(Collectors.toList()); //Java 9: .toList());

            if (indexes.contains(NOT_FOUND)) {
                log.error("{}: wrong spatial axes label(s): {}. Expected full list: {}",
                        coverageID, spatialAxisLabels, axisLabels);
                return null;
            }

            double[] lowerCorner = indexes.stream()
                    .map(i -> Double.parseDouble(minValues[i]))
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            double[] upperCorner = indexes.stream()
                    .map(i -> Double.parseDouble(maxValues[i]))
                    .mapToDouble(Double::doubleValue)
                    .toArray();

            // TransformPoint requires 3D coords:
            // https://gdal.org/java/org/gdal/osr/CoordinateTransformation.html
            double[] llWgs84 = Arrays.copyOf(lowerCorner, 3);
            double[] urWgs84 = Arrays.copyOf(upperCorner, 3);

            toWgs84.TransformPoint(llWgs84);
            toWgs84.TransformPoint(urWgs84);

            log.trace("{}D WGS84 extent: {} -> {}", ndims,
                    Arrays.toString(llWgs84),
                    Arrays.toString(urWgs84));

            CollectionSpatialExtent spatialExtent = new CollectionSpatialExtent();
            List<List<BigDecimal>> bbox = new ArrayList<>();

            // overall bbox
            List<BigDecimal> overallBbox = new ArrayList<>();
            for (double[] corner : Arrays.asList(llWgs84, urWgs84)) {
                overallBbox.addAll(
                        Arrays.stream(corner)
                        .limit(ndims)
                        .boxed()
                        .map(c -> BigDecimal.valueOf(c))
                        .collect(Collectors.toList())
                       );
            }
            bbox.add(overallBbox);

            // subsequent sub-bboxes:
            // openEO API: "The first bounding box describes the overall spatial extent of the data.
            //              All subsequent bounding boxes describe more precise bounding boxes, e.g.
            //              to identify clusters of data."
            // ...

            spatialExtent.setBbox(bbox);
            collectionExtent.setSpatial(spatialExtent);
        }

        // NOTE: temporal extent is mandatory in STAC, even with purely spatial collections.
        // TODO what to put when there is no time axis in the original coverage?
        CollectionTemporalExtent temporalExtent = new CollectionTemporalExtent();
        List<List<OffsetDateTime>> intervals = new ArrayList<>();

        if (hasTimeCrs) {
            // 1+ time dimensions:
            for (DimensionTemporal dim : timeDims) {
                String minT = dim.getExtent().get(0);
                String maxT = dim.getExtent().get(1);

                List<OffsetDateTime> interval = new ArrayList<>();
                try {
                    //STAC requires format: https://www.rfc-editor.org/rfc/rfc3339#section-5.6
                    //We use: java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.
                    interval.add(OffsetDateTime.parse(minT));
                    interval.add(OffsetDateTime.parse(maxT));

                } catch (DateTimeParseException e) {
                    log.warn("Error parsing time extent: {}:{}", minT, maxT);
                    interval.add(null); // FIXME should we be lenient?
                    interval.add(null); //
                }

                log.debug("Time interval : " + interval);

                intervals.add(interval);
            }
        }
        temporalExtent.setInterval(intervals);
        collectionExtent.setTemporal(temporalExtent);

        // set the computed spatio-temporal extent:
        currentCollection.setExtent(collectionExtent);

        // FIXME should not be hardcoded here
        currentCollection.setVersion(DEFAULT_COLL_VERSION);

        /*
         * license
         */
        List<Link> links = new ArrayList<>();

        // licence link
        // FIXME: <About_Link> of eg. ADO_SM_anomalies_ERA5 is not link, but a sentence containing links
        String licenseLink = metadataElement.getChildText("License_Link", rasdamanNS);
        if (null != licenseLink) {
            Link link = new Link();
            try {
                link.setHref(new URI(licenseLink));
                link.setRel(LinkRelType.LICENCE.toString());
                link.setTitle("License Link");

                String linkType = metadataElement.getChildText("License_Link_Type", rasdamanNS);
                if (null != linkType) {
                    link.setType(linkType);
                }
                links.add(link);
            } catch (URISyntaxException e) {
                log.error("Error invalid licence of {}", coverageID, e);
                return null;
            }
        }

        // about link
        // FIXME
        String aboutLink = metadataElement.getChildText("About_Link", rasdamanNS);
        if (null != aboutLink) {
            Link link = new Link();
            try {
                link.setHref(new URI(aboutLink));
                link.setRel(LinkRelType.ABOUT.toString());
                link.setTitle("About Link");

                String linkType = metadataElement.getChildText("About_Link_Type", rasdamanNS);
                if (null != linkType) {
                    link.setType(linkType);
                }

                links.add(link);
            } catch (URISyntaxException e) {
                log.error("Error invalid licence of {}", coverageID, e);
                return null;
            }
        }
        
        // via lins
        String viaLink = metadataElement.getChildText("Via_Link", rasdamanNS);
        if (null != viaLink) {
            Link link = new Link();
            try {
                link.setHref(new URI(viaLink));
                link.setRel(LinkRelType.VIA.toString());
                link.setTitle("Via Link");
                
                String viaLinkTitle = metadataElement.getChildText("Via_Title", rasdamanNS);
                if (null != viaLinkTitle) {
                    link.setTitle(viaLinkTitle);
                }
                
                String viaLinkType = metadataElement.getChildText("Via_Type", rasdamanNS);
                if (null != viaLinkType) {
                    link.setType(viaLinkType);
                }

                links.add(link);
            } catch (URISyntaxException e) {
                log.error("Error invalid licence of {}", coverageID, e);
                return null;
            }
        }

        currentCollection.setLinks(links);

        /*
         * licence
         */
        String license = metadataElement.getChildText("License", gmlNS);
        if (license == null) {
            license = "No License Information Available";
        }
        currentCollection.setLicense(license);

        /*
         * Other metadata
         */
        String title = metadataElement.getChildText("Title", gmlNS);
        if (title == null) {
            title = "No Title Available";
        }
        currentCollection.setTitle(title);

        String citation = metadataElement.getChildText("Citation", gmlNS);
        currentCollection.setCitation(citation);

        String description = metadataElement.getChildText("Description", gmlNS);
        if (description == null) {
            description = "No Description Available";
        }
        currentCollection.setDescription(description);

        String tempStep = metadataElement.getChildText("Temporal_Step", gmlNS);
        if (null != tempStep) {
            if (!hasTimeCrs) {
                log.warn("Temporal step provided but time axis not found in coverage {}.", coverageID);

                // fetch other dimension with temporal type:
                List<DimensionOther> otherTimeDims = cubeDimensions.values().stream()
                        .filter(dim -> TypeEnum.TEMPORAL.equals(dim.getType()))
                        .filter(dim -> dim instanceof DimensionOther)
                        .map(dim -> (DimensionOther) dim)
                        .collect(Collectors.toList()); //Java 9: .toList());

                for (DimensionOther dim : otherTimeDims) {
                    dim.setStep(tempStep);
                }
            } else {
                DimensionTemporal dim = timeDims.get(0);
                dim.setStep(tempStep);

                if (timeDims.size() != 1) {
                    log.warn("Multiple time dimension. Assume temporal step is for '{}'", dim);
                }
            }
        }

        /*
         * keywords
         */
        List<String> keywords = new ArrayList<>();

        String keywordsText = metadataElement.getChildText("Keywords", gmlNS);
        if (null != keywordsText) {
            keywords.addAll(Arrays.asList(keywordsText.split(", ")));
            log.debug("Keywords : {}", keywords);
        } else {
            keywords.add("No keywords Available");
        }

        currentCollection.setKeywords(keywords);

        /*
         * STAC extensions
         */
        Set<String> stacExtensions = new HashSet<>();
        stacExtensions.addAll(STAC_EXTENSIONS);
        currentCollection.setStacExtensions(stacExtensions);

        /*
         * Data providers
         */
        List<Providers> providers = new ArrayList<>();

        // force THIS provider
        for (Providers p : extraProviders) {
            providers.add(p);
        }

        // import source providers
        int prvi = 1;
        boolean done = false;
        while (!done) {
            List<String> roles = new ArrayList<>();
            Providers provider = new Providers();

            String name = metadataElement.getChildText(String.format("Provider%d_Name", prvi), gmlNS);
            String role = metadataElement.getChildText(String.format("Provider%d_Roles", prvi), gmlNS);
            String link = metadataElement.getChildText(String.format("Provider%d_Link", prvi), gmlNS);

            if (null == name) {
                done = true; // no more providers
            } else {
                try {
                    provider.setName(name);
                    roles.add(role);
                    provider.setRoles(roles);
                    provider.setUrl(new URI(link));
                    providers.add(provider);
                } catch (URISyntaxException e) {
                    log.error("{}: Invalid provider link.", coverageID, e);
                    return null;
                }
            }
            ++prvi;
        }

        currentCollection.setProviders(providers);

        /*
         * summaries
         */
        CollectionSummaries summaries = new CollectionSummaries();

        Set<String> platforms = new LinkedHashSet<>();
        String platform = metadataElement.getChildText("Platform", gmlNS);
        if (null != platform) {
            platforms.add(platform);
        }

        CollectionSummaryStats cloudCover = new CollectionSummaryStats();
        JSONArray cloudCovArray = new JSONArray();

        /*
         * axes metadata
         */
        Element axesMetadata = metadataElement.getChild("axes", gmlNS);
        if (null != axesMetadata) {
            for (Element axis : axesMetadata.getChildren()) {
                String axisLabel = axis.getName();
                Dimension dim = cubeDimensions.get(axisLabel);

                if (null == dim) {
                    log.warn("Axis '{}' not found in domain set.", axisLabel);
                    continue; // skip axis metadata
                }

                // description -> description
                String descr = axis.getChildText("description");
                if (null != descr) {
                    dim.setDescription(descr);
                }

                // standard_name / long_name ?
                // ...

                // units -> unit
                String units = axis.getChildText("units");
                if (null != units && HasUnit.is(dim)) {
                    HasUnit unitDim = (HasUnit) dim;
                    unitDim.setUnit(units);
                    log.debug("Overwrite {} dimension unit of measure with '{}'.", axisLabel, units);
                }

                // other arbitrary fields ... ?
                // ...
            }
        }

        /*
         * slices detail
         */
        Element slicesEl = metadataElement.getChild("slices", gmlNS);
        if (null != slicesEl) {
            for (Element slice : slicesEl.getChildren()) {
                // spacecraft
                String spacecraft = slice.getChildText("DATATAKE_1_SPACECRAFT_NAME");
                if (null != spacecraft) {
                    platforms.add(spacecraft);
                }
                // cloud-coverage
                String cloudCovStr = slice.getChildText("CLOUD_COVERAGE_ASSESSMENT");
                if (null != cloudCovStr) {
                    try {
                        double cloudCov = Double.parseDouble(cloudCovStr);
                        cloudCovArray.put(cloudCov);
                    } catch (NumberFormatException e) {
                        log.error("Invalid cloud-coverage value found: '{}'", cloudCovStr, e);
                    }
                }
                // boundedBy
                // ... -> single slices' extent could be added to the list of cube's extents
            }
        }

        List<String> constellations = new ArrayList<>();
        String constellation = metadataElement.getChildText("Constellation", gmlNS);
        if (null != constellation) {
            constellations.add(constellation);
        }

        List<String> instruments = new ArrayList<>();
        String instrument = metadataElement.getChildText("Instruments", gmlNS);
        if (null != instrument) {
            instruments.add(instrument);
        }
        
        Integer rows = null;
        Integer columns = null;

        try {
            rows = Integer.parseInt(metadataElement.getChildText("Rows", rasdamanNS));
        }catch(Exception e) {
            log.warn("Error in parsing Rows: {}", e.getMessage());
        }

        try {
            columns = Integer.parseInt(metadataElement.getChildText("Columns", rasdamanNS));
        }catch(Exception e) {
            log.warn("Error in parsing Columns: {}", e.getMessage());
        }

        /*
         * cloud coverage
         */
        double maxCCValue = 0;
        double minCCValue = 0;
        boolean cloudCoverFlag = false;

        // FIXME optimize cloud-cover min/max extraction:

        if (cloudCovArray.length() > 0) {
            try {
                maxCCValue = cloudCovArray.getDouble(0);
                minCCValue = cloudCovArray.getDouble(0);
                cloudCoverFlag = true;

            } catch (JSONException e) {
                log.warn("Error in parsing cloud cover Extents :" + e.getMessage());
            }
        }

        if (cloudCoverFlag) {
            for (int i = 1; i < cloudCovArray.length(); i++) {
                double ccov = cloudCovArray.getDouble(i);
                if (ccov > maxCCValue) {
                    maxCCValue = ccov;
                }
                if (ccov < minCCValue) {
                    minCCValue = ccov;
                }
            }
        }

        cloudCover.setMin(minCCValue);
        cloudCover.setMax(maxCCValue);

        /*
         * bands
         */
        List<BandSummary> bandsSummaries = new ArrayList<>();
        Set<Double> gsd = new LinkedHashSet<>();
        boolean hasBandsMetadata = false;

        Element bandsMetadata = metadataElement.getChild("bands", gmlNS);
        if (null != bandsMetadata) {
            bandsMetadataList = bandsMetadata.getChildren();
            hasBandsMetadata = true;
        }

        if (hasBandsMetadata) {
            try {
                // TODO here parsing is lenient: is that ok?
                // TODO where to put metadata that are not in the eo:bands schema?
                // https://github.com/stac-extensions/eo
                for (Element band : bandsMetadataList) {

                    BandSummary bandsSummary = new BandSummary();
                    String bandWave = "0";
                    String bandCommonName = "No Band Common Name found";
                    String bandGSD = "0";
                    String bandId = "No Band Name found";

                    bandId = band.getName();
                    bandsSummary.setName(bandId);

                    // (hack)
                    // fetch SWE:description + metadata:long_name (arbitrary..) to describe the band
                    String sweDescr = band2SweDescr.get(bandId);
                    String longName = band.getChildText("long_name");
                    if (null != longName && longName.equals(sweDescr)) {
                        longName = null;
                    }
                    if (null != sweDescr || null != longName) {
                        String bandDescr = String.format("%s%s%s",
                                (null == longName) ? "" : String.format("[%s]", longName),
                                (null != sweDescr && null != longName) ? " - " : "",
                                (null == sweDescr) ? "" : sweDescr );
                        bandsSummary.setDescription(bandDescr);
                    }

                    if (!dimensionBands.containsValue(bandId)) {
                        log.warn("{} band has metadata found, but is not listed in coverage's range.", bandId);
                        continue; // skip metadata
                    }

                    bandGSD = band.getChildText("gsd");
                    if (null != bandGSD) {
                        try {
                            gsd.add(Double.parseDouble(bandGSD));
                            bandsSummary.setGsd(Double.parseDouble(bandGSD));
                        } catch (NumberFormatException e) {
                            log.warn("Error in parsing band gsd:" + e.getMessage());
                        }
                    }

                    bandCommonName = band.getChildText("common_name");
                    if (null != bandCommonName) {
                        bandsSummary.setCommonname(bandCommonName);
                    }

                    bandWave = band.getChildText("wavelength");
                    if (null != bandWave) {
                        try {
                            double w = Double.parseDouble(bandWave);
                            bandsSummary.setCenterwavelength(w);
                        } catch (NumberFormatException e) {
                            log.warn("Error in parsing band wave-lenght:" + e.getMessage());
                        }
                    }

                    bandsSummaries.add(bandsSummary);
                }
            } catch (Exception e) {
                log.warn("Error in parsing bands :" + e.getMessage());
            }
        } else {
            // simple summaries: just the band label
            // (but there is more data in SWE quantities that is ignored)
            for (Element band : bandsListSwe) {
                BandSummary bandsSummary = new BandSummary();
                String bandId = band.getAttributeValue("name");
                bandsSummary.setName(bandId);
                bandsSummaries.add(bandsSummary);
            }
        }

        summaries.setPlatform(platforms.stream().collect(Collectors.toList())); //Java 9: .toList());
        summaries.setConstellation(constellations);
        summaries.setInstruments(instruments);
        summaries.setCloudCover(cloudCover);
        summaries.setGsd(gsd.stream().collect(Collectors.toList())); //Java 9: .toList());
        summaries.setRows(rows);
        summaries.setColumns(columns);
//      summaries.setEpsg(epsg);
        summaries.setBands(bandsSummaries);
        currentCollection.setSummaries(summaries);

        Map<String, Asset> assets = new HashMap<String, Asset>();
        currentCollection.setAssets(assets);

        return currentCollection;
    }
}
