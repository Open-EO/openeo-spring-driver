package org.openeo.spring.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openeo.spring.json.OffsetDateTimeSerializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utility class used to un/marshall
 * Java objects from/to JSON documents.
 */
public class JSONMarshaller {

    private static final Logger log = LogManager.getLogger(JSONMarshaller.class);

    /** Default indentation factor when serializing to JSON. */
    private static final int DEFAULT_INDENT = 4;

    /** Characters set assumed in remote catalogs. */
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * JSON<->Java mapper: this should be used whenever STAC de/serialization is involved.
     */
    public static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.registerModule(new JavaTimeModule());        
        MAPPER.registerModule(new SimpleModule()
                .addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()));
    }

    /**
     * Parses the content of a document to JSON.
     *
     * @param url     the URL of the JSON document
     * @param charset the characters encoding of the remote document
     * @return the global {@code JSONObject} object representing the unmarshalled document;
     *         {@code null} if the document cannot not be fetch or is invalid.
     */
    public static JSONObject readJsonFromUrl(String url, Charset charset) {

        JSONObject json = null;

        log.debug("Trying to read JSON from '{}'", url);

        try (InputStream is = new URL(url).openStream()) {

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(is, charset)
                    );
            String jsonText = JSONMarshaller.readAll(rd);
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
    public static JSONObject readJsonFromUrl(String url) {
        return readJsonFromUrl(url, UTF8);
    }

    /**
     * See {@link ObjectMapper#readValue(String, Class)}.
     */
    public static <T> T readValue(String content, Class<T> valueType)
            throws JsonProcessingException, JsonMappingException {
        return MAPPER.readValue(content, valueType);
    }

    /**
     * See {@link ObjectMapper#readValue(InputStream, Class)}.
     */
    public static <T> T readValue(InputStream is, Class<T> valueType)
            throws IOException {
        return MAPPER.readValue(is, valueType);
    }

    /**
     * See {@link ObjectMapper#readValue(File, Class)}.
     */
    public static <T> T readValue(File content, Class<T> valueType)
            throws IOException {
        return MAPPER.readValue(content, valueType);
    }

    /**
     * Override method with default indentation factor.
     * @throws IOException
     * @see #syncWriteToFile(JSONObject, int)
     */
    public static boolean syncWriteToFile(final JSONObject stacObj, File jsonOut) throws IOException {
        return JSONMarshaller.syncWriteToFile(stacObj, jsonOut, DEFAULT_INDENT);
    }

    /**
     * See {@link ObjectMapper#writeValue(File, Object)}.
     * @return {@code true} on successful JSON serialization, {@code false} otherwise.
     * @throws IOException
     */
    // FIXME "merge" with #syncWriteToFile(final JSONObject stacObj, File jsonOut, int indent) ?
    public static boolean syncWriteToFile(final Object obj, File jsonOut) throws IOException {
        boolean ok = true;
        try {
            MAPPER.writeValue(jsonOut, obj);
        } catch (JsonGenerationException | JsonMappingException e) {
            ok = false;
        }
        return ok;
    }
    /**
     * Serializes a STAC object to JSON (synchronously).
     *
     * @param stacObj  the Java object to be serialized
     * @param jsonOut  the file where to store the STAC JSON document (will be created if it does not exist)
     * @param indent   the JSON indentation factor
     * @return {@code true} on success; {@code false} when serialization is not possible
     * @throws IOException in case the given target {@code jsonOut} file is not writable.
     */
    public static boolean syncWriteToFile(final JSONObject stacObj, File jsonOut, int indent) throws IOException {

        boolean ok = true;

        try {
            log.debug("Converting JSON object to string...");
            final String odcSTACMetdataStr = stacObj.toString(indent);

            if (!jsonOut.exists()) {
                log.debug("Creating file {}...", jsonOut.getName());
                jsonOut.createNewFile();
            }

            try (FileWriter file = new FileWriter(jsonOut)) {
                file.write(odcSTACMetdataStr);
                log.info("JSON object serialized: {}.", jsonOut.getName());
            }
        } catch (IOException e) {
            log.error("Error while serializing JSON object to disk.", e);
            throw e;
        } catch (JSONException e) {
            log.error("Cannot serialize JSON object.", e);
            ok = false;
        }

        return ok;
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
