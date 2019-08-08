package com.opuscapita.s2p.blob.blobfilesystem.utils;

import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlobUtils {

    public static final String HTTP_PATH_SEPARATOR_STRING = "/";
    public static final char HTTP_PATH_SEPARATOR_CHAR = '/';
    public static final Charset HTTP_PATH_CHARSET = Charset.forName("UTF-8");

    public static final byte[] EMPTY_BYTE_ARRAY = {};
    public static final char[] EMPTY_CHAR_ARRAY = {};
    public static final String[] EMPTY_STRING_ARRAY = {};
    public static final Object[] EMPTY_OBJECT_ARRAY = {};
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};

    private static final String HEAD_REQUEST_METHOD = "HEAD";
    private static final String RANGE_REQUEST_PROPERTY_KEY = "Range";
    private static final String RANGE_REQUEST_PROPERTY_VALUE_START = "bytes=";
    private static final String RANGE_REQUEST_PROPERTY_VALUE_SEPARATOR = "-";
    public static final int DEFAULT_COPY_SIZE = 8192;


    private static final Logger log = LoggerFactory.getLogger(BlobUtils.class);

    private BlobUtils() {
    }

    public static boolean isEmpty(CharSequence cs) {
        return length(cs) <= 0;
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static int size(Collection<?> c) {
        return c == null ? 0 : c.size();
    }

    public static boolean isEmpty(Collection<?> c) {
        return (c == null) || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static int size(Map<?, ?> m) {
        return m == null ? 0 : m.size();
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return (m == null) || m.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> m) {
        return !isEmpty(m);
    }

    public static int length(char[] chars) {
        return (chars == null) ? 0 : chars.length;
    }

    public static boolean isEmpty(char[] chars) {
        return length(chars) <= 0;
    }

    public static Map<String, Object> getDefaultAttributes(BlobPath path) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("name", "/");
        attributes.put("extension", "");
        attributes.put("path", "/");
        attributes.put("size", 0);
        attributes.put("isFile", false);
        attributes.put("isDirectory", true);
        attributes.put("contentType", null);
        attributes.put("checksum", null);
        if (path != null) {
            attributes.put("name", path.toString());
            attributes.put("path", path.toString());
        }
        return attributes;
    }
}
