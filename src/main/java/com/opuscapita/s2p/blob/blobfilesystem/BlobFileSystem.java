package com.opuscapita.s2p.blob.blobfilesystem;

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


public class BlobFileSystem extends FileSystem {

    private Logger log = LoggerFactory.getLogger(BlobFileSystem.class);

    private final AbstractBlobFileSystemProvider provider;

    private final String authority;

    private final String rootPath;

    BlobFileSystem(AbstractBlobFileSystemProvider provider, String authority, String rootPath) {
        this.provider = Utils.nonNull(provider, () -> "FileSystemProvider is null");
        this.authority = Utils.nonNull(authority, () -> "Authority is null");
        this.rootPath = Utils.nonNull(rootPath, () -> "RootPath is null");
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
        return BlobUtils.HTTP_PATH_SEPARATOR_STRING;
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.singleton(new BlobPath(this, "", null, null));
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
    public BlobPath getPath(String first, String... more) {
        String path = Utils.nonNull(first, () -> "null first")
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


    public BlobPath getPath(URI uri) {
        return new BlobPath(this, uri.getPath(), uri.getQuery(), uri.getFragment());
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
        } else if (other instanceof BlobFileSystem) {
            final BlobFileSystem ofs = (BlobFileSystem) other;
            return provider() == ofs.provider() && getAuthority()
                    .equalsIgnoreCase(ofs.getAuthority());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * provider.hashCode() + getAuthority().toLowerCase().hashCode();
    }

    public String getRootPath() {
        return rootPath;
    }
}
