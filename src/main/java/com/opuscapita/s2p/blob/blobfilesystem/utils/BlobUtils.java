package com.opuscapita.s2p.blob.blobfilesystem.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BlobUtils {

    public static final String HTTP_PATH_SEPARATOR_STRING = "/";
    public static final char HTTP_PATH_SEPARATOR_CHAR = '/';
    public static final Charset HTTP_PATH_CHARSET = Charset.forName("UTF-8");


    private static final String HEAD_REQUEST_METHOD = "HEAD";
    private static final String RANGE_REQUEST_PROPERTY_KEY = "Range";
    private static final String RANGE_REQUEST_PROPERTY_VALUE_START = "bytes=";
    private static final String RANGE_REQUEST_PROPERTY_VALUE_SEPARATOR = "-";


    private static final Logger log = LoggerFactory.getLogger(BlobUtils.class);

    private BlobUtils() {
    }

    public static void disconnect(URLConnection connection) {
        Utils.nonNull(connection, () -> "null URL connection");
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
        }
    }

    public static boolean exists(URL url) throws IOException {
        Utils.nonNull(url, () -> "null url");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod(HEAD_REQUEST_METHOD);
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (final UnknownHostException e) {
            return false;
        } finally {
            conn.disconnect();
        }
    }

    public static void setRangeRequest(final URLConnection connection, final long start,
                                       final long end) {
        Utils.nonNull(connection, () -> "Null URLConnection");
        String request = RANGE_REQUEST_PROPERTY_VALUE_START
                + start
                + RANGE_REQUEST_PROPERTY_VALUE_SEPARATOR;
        if (end != -1) {
            request += end;
        }

        if (start < 0 || end < -1 || (end != -1 && end < start)) {
            throw new IllegalArgumentException("Invalid request: " + request);
        }

        log.debug("Request '{}' {} for {}", RANGE_REQUEST_PROPERTY_KEY, request, connection);
        connection.setRequestProperty(RANGE_REQUEST_PROPERTY_KEY, request);
    }

    public static Map<String, Object> getDefaultAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "/");
        attributes.put("extension", "");
        attributes.put("localhost", "/");
        attributes.put("path", "/");
        attributes.put("size", 0);
        attributes.put("isFile", false);
        attributes.put("isDirectory", true);
        attributes.put("contentType", null);
        attributes.put("checksum", null);
        return attributes;
    }
}
