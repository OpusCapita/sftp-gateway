package com.opuscapita.s2p.blob.blobfilesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractBlobFileSystemProvider extends FileSystemProvider {

    private final Map<String, BlobFileSystem> fileSystems = new ConcurrentHashMap<>();

    @Override
    public abstract String getScheme();


    @Override
    public BlobFileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        synchronized (fileSystems) {
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            int i = schemeSpecificPart.indexOf("!/");
            if (i >= 0) {
                schemeSpecificPart = schemeSpecificPart.substring(0, i);
            }
            BlobFileSystem fileSystem = fileSystems.get(schemeSpecificPart);
            if (fileSystem != null) {
                throw new FileSystemAlreadyExistsException(schemeSpecificPart);
            }
            fileSystem = new BlobFileSystem(this, schemeSpecificPart, env);
            fileSystems.put(schemeSpecificPart, fileSystem);
            return fileSystem;
        }
    }

    @Override
    public BlobFileSystem getFileSystem(URI uri) {
        return getFileSystem(uri, false);
    }

    public BlobFileSystem getFileSystem(URI uri, boolean create) {
        synchronized (fileSystems) {
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            int i = schemeSpecificPart.indexOf("!/");
            if (i >= 0) {
                schemeSpecificPart = schemeSpecificPart.substring(0, i);
            }
            BlobFileSystem fileSystem = fileSystems.get(schemeSpecificPart);
            if (fileSystem == null) {
                if (create) {
                    try {
                        fileSystem = newFileSystem(uri, null);
                    } catch (IOException e) {
                        throw (FileSystemNotFoundException) new FileSystemNotFoundException(schemeSpecificPart).initCause(e);
                    }
                } else {
                    throw new FileSystemNotFoundException(schemeSpecificPart);
                }
            }
            return fileSystem;
        }
    }

    @Override
    public Path getPath(URI uri) {
        String str = uri.getSchemeSpecificPart();
        int i = str.indexOf("!/");
        if (i == -1) {
            throw new IllegalArgumentException("URI: " + uri + " does not contain path info ex. github:apache/karaf#master!/");
        }
        return getFileSystem(uri, true).getPath(str.substring(i + 1));
    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... options) throws IOException {
        if (!(path instanceof BlobPath)) {
            throw new ProviderMismatchException();
        }
        return ((BlobPath) path).getFileSystem().newInputStream(path, options);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        if (!(dir instanceof BlobPath)) {
            throw new ProviderMismatchException();
        }
        return ((BlobPath) dir).getFileSystem().newDirectoryStream(dir, filter);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if (!(path instanceof BlobPath)) {
            throw new ProviderMismatchException();
        }
        return ((BlobPath) path).getFileSystem().newByteChannel(path, options, attrs);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return path.toAbsolutePath().equals(path2.toAbsolutePath());
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {

    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (!(path instanceof BlobPath)) {
            throw new ProviderMismatchException();
        }
        return ((BlobPath) path).getFileSystem().readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }
}