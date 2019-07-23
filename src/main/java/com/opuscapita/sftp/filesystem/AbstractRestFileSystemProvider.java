package com.opuscapita.sftp.filesystem;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractRestFileSystemProvider extends FileSystemProvider {

    private final Map<String, RestFileSystem> fileSystems = new ConcurrentHashMap<>();

    @Override
    public abstract String getScheme();

    private URI checkUri(URI uri) {

        Utils.nonNull(uri, () -> "URI is null");
        Utils.nonNull(uri.getAuthority(),
                () -> String.format("%s requires URI with authority: invalid %s", this, uri));
        if (!getScheme().equalsIgnoreCase(uri.getScheme())) {
            throw new ProviderMismatchException(String.format("Invalid scheme for %s: %s",
                    this, uri.getScheme()));
        }
        return uri;
    }

    @Override
    public RestFileSystem newFileSystem(URI uri, Map<String, ?> env)
            throws IOException {
        checkUri(uri);

        if (fileSystems.containsKey(uri.getAuthority())) {
            throw new FileSystemAlreadyExistsException("URI: " + uri);
        }

        fileSystems.computeIfAbsent(uri.getAuthority(), (auth) -> new RestFileSystem(this, auth));

        return this.getFileSystem(uri);
    }

    @Override
    public RestFileSystem getFileSystem(URI uri) {
        RestFileSystem fs = fileSystems.get(checkUri(uri).getAuthority());
        if (fs == null) {
            throw new FileSystemNotFoundException("URI: " + uri);
        }
        return fs;
    }

    @Override
    public RestPath getPath(URI uri) {
        checkUri(uri);
        return fileSystems
                .computeIfAbsent(uri.getAuthority(), (auth) -> new RestFileSystem(this, auth))
                .getPath(uri);
    }

    @Override
    public final SeekableByteChannel newByteChannel(Path path,
                                                    Set<? extends OpenOption> options, FileAttribute<?>... attrs)
            throws IOException {
        Utils.nonNull(path, () -> "null path");
        Utils.nonNull(options, () -> "null options");
        if (options.isEmpty() ||
                (options.size() == 1 && options.contains(StandardOpenOption.READ))) {
            URL url = checkUri(path.toUri()).toURL();
            if (!RestUtils.exists(url)) {
                throw new NoSuchFileException(url.toString());
            }
            return new URLSeekableByteChannel(url);
        }
        throw new UnsupportedOperationException(
                String.format("Only %s is supported for %s, but %s options(s) are provided",
                        StandardOpenOption.READ, this, options));
    }

    @Override
    public final DirectoryStream<Path> newDirectoryStream(Path dir,
                                                          DirectoryStream.Filter<? super Path> filter) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Unsupported method.
     */
    @Override
    public final void createDirectory(Path dir, FileAttribute<?>... attrs)
            throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() +
                " is read-only: cannot create directory");
    }

    /**
     * Unsupported method.
     */
    @Override
    public final void delete(Path path) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() +
                " is read-only: cannot delete directory");
    }

    @Override
    public final void copy(Path source, Path target, CopyOption... options)
            throws IOException {
        throw new UnsupportedOperationException("Copy Function is not implemented");
    }

    /**
     * Unsupported method.
     */
    @Override
    public final void move(Path source, Path target, CopyOption... options)
            throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() +
                " is read-only: cannot move paths");
    }

    @Override
    public final boolean isSameFile(Path path, Path path2) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final boolean isHidden(Path path) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final FileStore getFileStore(Path path) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final void checkAccess(Path path, AccessMode... modes) throws IOException {
        Utils.nonNull(path, () -> "null path");
        final URI uri = checkUri(path.toUri());
        if (!RestUtils.exists(uri.toURL())) {
            throw new NoSuchFileException(uri.toString());
        }
        for (AccessMode access : modes) {
            switch (access) {
                case READ:
                    break;
                case WRITE:
                case EXECUTE:
                    throw new AccessDeniedException(uri.toString());
                default:
                    throw new UnsupportedOperationException("Unsupported access mode: " + access);
            }
        }
    }

    @Override
    public final <V extends FileAttributeView> V getFileAttributeView(Path path,
                                                                      Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final <A extends BasicFileAttributes> A readAttributes(Path path,
                                                                  Class<A> type, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final Map<String, Object> readAttributes(Path path, String attributes,
                                                    LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final void setAttribute(Path path, String attribute, Object value,
                                   LinkOption... options) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() +
                " is read-only: cannot set attributes to paths");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}