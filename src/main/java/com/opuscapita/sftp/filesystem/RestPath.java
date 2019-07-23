package com.opuscapita.sftp.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

public class RestPath implements Path {

    private final RestFileSystem fs;

    private final byte[] normalizedPath;
    private volatile int[] offsets;

    private final String query;
    private final String reference;

    private RestPath(final RestFileSystem fs,
                     final String query, final String reference,
                     final byte... normalizedPath) {
        this.fs = fs;

        this.query = query;
        this.reference = reference;

        this.normalizedPath = normalizedPath;
    }

    RestPath(final RestFileSystem fs, final String path, final String query, final String reference) {
        this(Utils.nonNull(fs, () -> "null fs"), query, reference,
                getNormalizedPathBytes(Utils.nonNull(path, () -> "null path"), true));
    }

    @Override
    public RestFileSystem getFileSystem() {
        return fs;
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    public Path getRoot() {
        return new RestPath(fs, null, null);
    }

    @Override
    public Path getFileName() {
        System.out.println("getFileName");
        throw new UnsupportedOperationException("getFileName not implemented");
    }

    @Override
    public Path getParent() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getNameCount() {
        initOffsets();
        return offsets.length;
    }

    @Override
    public Path getName(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean startsWith(Path other) {
        // different FileSystems return false
        if (!this.getFileSystem().equals(Utils.nonNull(other, () -> "null path").getFileSystem())) {
            return false;
        }

        return startsWith(((RestPath) other).normalizedPath);
    }

    @Override
    public boolean startsWith(String other) {
        Utils.nonNull(other, () -> "null other");
        return startsWith(getNormalizedPathBytes(other, false));
    }

    private boolean startsWith(byte[] other) {
        final int olen = getLastIndexWithoutTrailingSlash(other);

        if (olen > normalizedPath.length) {
            return false;
        }

        int i;
        for (i = 0; i <= olen; i++) {
            if (normalizedPath[i] != other[i]) {
                return false;
            }
        }

        return i >= this.normalizedPath.length
                || this.normalizedPath[i] == RestUtils.HTTP_PATH_SEPARATOR_CHAR;
    }

    @Override
    public boolean endsWith(Path other) {
        if (!this.getFileSystem().equals(Utils.nonNull(other, () -> "null path").getFileSystem())) {
            return false;
        }

        return endsWith(((RestPath) other).normalizedPath, true);
    }

    @Override
    public boolean endsWith(String other) {
        Utils.nonNull(other, () -> "null other");
        return endsWith(getNormalizedPathBytes(other, false), false);
    }

    private boolean endsWith(byte[] other, boolean pathVersion) {
        int olast = getLastIndexWithoutTrailingSlash(other);
        int last = getLastIndexWithoutTrailingSlash(this.normalizedPath);

        if (olast == -1) {
            return last == -1;
        }
        if (last < olast) {
            return false;
        }

        for (; olast >= 0; olast--, last--) {
            if (other[olast] != this.normalizedPath[last]) {
                return false;
            }
        }

        if (last == -1) {
            return true;
        }

        if (pathVersion) {
            return true;
        } else {
            return this.normalizedPath[last] == RestUtils.HTTP_PATH_SEPARATOR_CHAR;
        }
    }

    @Override
    public Path normalize() {
        return Paths.get(this.toUri());
//        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Path resolve(Path other) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Path resolve(String other) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Path resolveSibling(Path other) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Path resolveSibling(String other) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Path relativize(Path other) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public URI toUri() {
        try {
            return new URI(fs.provider().getScheme(),
                    fs.getAuthority(),
                    new String(normalizedPath, RestUtils.HTTP_PATH_CHARSET),
                    query, reference);
        } catch (final URISyntaxException e) {
            throw new IOError(e);
        }
    }

    @Override
    public Path toAbsolutePath() {
        if (isAbsolute()) {
            return this;
        }
        throw new IllegalStateException("Should not appear a relative HTTP/S paths (unsupported)");
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Unsupported method.
     */
    @Override
    public File toFile() {
        throw new UnsupportedOperationException(this.getClass() + " cannot be converted to a File");
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events,
                             WatchEvent.Modifier... modifiers) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterator<Path> iterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int compareTo(Path other) {
        if (this == other) {
            return 0;
        }

        RestPath httpOther = (RestPath) other;
        // object comparison - should be from the same provider
        if (fs.provider() != httpOther.fs.provider()) {
            throw new ClassCastException();
        }

        // first check the authority (case insensitive)
        int comparison = fs.getAuthority().compareToIgnoreCase(httpOther.fs.getAuthority());
        if (comparison != 0) {
            return comparison;
        }

        final int len1 = normalizedPath.length;
        final int len2 = httpOther.normalizedPath.length;
        final int n = Math.min(len1, len2);
        for (int k = 0; k < n; k++) {
            comparison = Byte.compare(this.normalizedPath[k], httpOther.normalizedPath[k]);
            if (comparison != 0) {
                return comparison;
            }
        }
        comparison = len1 - len2;
        if (comparison != 0) {
            return comparison;
        }

        comparison = Comparator.nullsFirst(String::compareTo).compare(this.query, httpOther.query);
        if (comparison != 0) {
            return comparison;
        }

        return Comparator.nullsFirst(String::compareTo)
                .compare(this.reference, httpOther.reference);
    }

    @Override
    public boolean equals(final Object other) {
        try {
            return compareTo((Path) other) == 0;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int h = fs.hashCode();
        for (int i = 0; i < normalizedPath.length; i++) {
            h = 31 * h + (normalizedPath[i] & 0xff);
        }
        // this is safe for null query and reference
        h = 31 * h + Objects.hash(query, reference);
        return h;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(fs.provider().getScheme())
                .append("://")
                .append(fs.getAuthority())
                .append(new String(normalizedPath, RestUtils.HTTP_PATH_CHARSET));
        if (query != null) {
            sb.append('?').append(query);
        }
        if (reference != null) {
            sb.append('#').append(reference);
        }
        return sb.toString();
    }

    private void initOffsets() {
        if (offsets == null) {
            final int length = getLastIndexWithoutTrailingSlash(normalizedPath);
            int count = 0;
            int index = 0;
            for (; index < length; index++) {
                final byte c = normalizedPath[index];
                if (c == RestUtils.HTTP_PATH_SEPARATOR_CHAR) {
                    count++;
                    index++;
                }
            }
            // populate offsets
            final int[] result = new int[count];
            count = 0;
            for (index = 0; index < length; index++) {
                final byte c = normalizedPath[index];
                if (c == RestUtils.HTTP_PATH_SEPARATOR_CHAR) {
                    result[count++] = index++;
                }
            }
            // update in a thread-safe manner
            synchronized (this) {
                if (offsets == null) {
                    offsets = result;
                }
            }
        }
    }

    private static byte[] getNormalizedPathBytes(final String path, final boolean checkRelative) {

        if (checkRelative && !path.isEmpty() && !path.startsWith(RestUtils.HTTP_PATH_SEPARATOR_STRING)) {
            throw new InvalidPathException(path, "Relative HTTP/S path are not supported");
        }

        if (RestUtils.HTTP_PATH_SEPARATOR_STRING.equals(path) || path.isEmpty()) {
            return new byte[0];
        }
        final int len = path.length();

        char prevChar = 0;
        for (int i = 0; i < len; i++) {
            char c = path.charAt(i);
            if (isDoubleSeparator(prevChar, c)) {
                return getNormalizedPathBytes(path, len, i - 1);
            }
            prevChar = checkNotNull(path, c);
        }
        if (prevChar == RestUtils.HTTP_PATH_SEPARATOR_CHAR) {
            return getNormalizedPathBytes(path, len, len - 1);
        }

        return path.getBytes(RestUtils.HTTP_PATH_CHARSET);
    }

    private static byte[] getNormalizedPathBytes(final String path, final int len,
                                                 final int offset) {
        int lastOffset = len;
        while (lastOffset > 0
                && path.charAt(lastOffset - 1) == RestUtils.HTTP_PATH_SEPARATOR_CHAR) {
            lastOffset--;
        }
        if (lastOffset == 0) {
            return new byte[]{RestUtils.HTTP_PATH_SEPARATOR_CHAR};
        }

        try (final ByteArrayOutputStream os = new ByteArrayOutputStream(len)) {
            if (offset > 0) {
                os.write(path.substring(0, offset).getBytes(RestUtils.HTTP_PATH_CHARSET));
            }
            char prevChar = 0;
            for (int i = offset; i < len; i++) {
                char c = path.charAt(i);
                if (isDoubleSeparator(prevChar, c)) {
                    continue;
                }
                prevChar = checkNotNull(path, c);
                os.write(c);
            }

            return os.toByteArray();
        } catch (final IOException e) {
            throw new Utils.ShouldNotHappenException(e);
        }
    }

    private static boolean isDoubleSeparator(final char prevChar, final char c) {
        return c == RestUtils.HTTP_PATH_SEPARATOR_CHAR
                && prevChar == RestUtils.HTTP_PATH_SEPARATOR_CHAR;
    }

    private static char checkNotNull(final String path, char c) {
        if (c == '\u0000') {
            throw new InvalidPathException(path, "Null character not allowed in path");
        }
        return c;
    }

    private static int getLastIndexWithoutTrailingSlash(final byte[] path) {
        int len = path.length - 1;
        if (len > 0 && path[len] == RestUtils.HTTP_PATH_SEPARATOR_CHAR) {
            len--;
        }
        return len;
    }
}