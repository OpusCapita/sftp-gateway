package com.opuscapita.sftp.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;


public class RestFileSystem extends FileSystem {

    private Logger log = LoggerFactory.getLogger(RestFileSystem.class);

    private final AbstractRestFileSystemProvider provider;

    private final String authority;

    RestFileSystem(AbstractRestFileSystemProvider provider, String authority) {
        this.provider = Utils.nonNull(provider, () -> "FileSystemProvider is null");
        this.authority = Utils.nonNull(authority, () -> "Authority is null");
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public void close() {
        log.warn("{} is always open (no closed)", this.getClass());
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getSeparator() {
        return RestUtils.HTTP_PATH_SEPARATOR_STRING;
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.singleton(new RestPath(this, "", null, null));
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        log.info("getFileStores");
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        log.info("supportedFileAttributeViews");
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public RestPath getPath(String first, String... more) {
        final String path = Utils.nonNull(first, () -> "null first")
                + String.join(getSeparator(), Utils.nonNull(more, () -> "null more"));

        if (!path.isEmpty() && !path.startsWith(getSeparator())) {
            throw new InvalidPathException(path, "Relative paths are not supported", 0);
        }

        try {
            return getPath(new URI(path));
        } catch (URISyntaxException e) {
            throw new InvalidPathException(e.getInput(), e.getReason(), e.getIndex());
        }
    }


    public RestPath getPath(final URI uri) {
        return new RestPath(this, uri.getPath(), uri.getQuery(), uri.getFragment());
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        log.info("getPathMatcher");
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        log.info("getUserPrincipalLookupService");
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public WatchService newWatchService() throws IOException {
        log.info("newWatchService");
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        return String.format("%s[%s]@%s", this.getClass().getSimpleName(), provider, hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof RestFileSystem) {
            final RestFileSystem ofs = (RestFileSystem) other;
            return provider() == ofs.provider() && getAuthority()
                    .equalsIgnoreCase(ofs.getAuthority());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * provider.hashCode() + getAuthority().toLowerCase().hashCode();
    }
}
