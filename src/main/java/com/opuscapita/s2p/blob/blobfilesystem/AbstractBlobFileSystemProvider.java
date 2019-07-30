package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.file.BlobFileAttributeView;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobFileAttributes;
import org.apache.sshd.common.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBlobFileSystemProvider extends FileSystemProvider {

    private final Map<String, BlobFileSystem> fileSystems = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public abstract String getScheme();

    @Override
    public BlobFileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        synchronized (fileSystems) {
            String schemeSpecificPart = uri.toString();
            BlobFileSystem fileSystem = fileSystems.get(schemeSpecificPart);
            if (fileSystem != null) {
                throw new FileSystemAlreadyExistsException(schemeSpecificPart);
            }
            fileSystem = new BlobFileSystem(this, env);
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
            String schemeSpecificPart = uri.toString();
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
        int i = str.indexOf("/");
        if (i == -1) {
            throw new IllegalArgumentException("URI: " + uri + " does not contain path info");
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
        log.info("createDirectory unsupported");
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
        System.out.println("getFileStore");
        return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        BlobPath p = toBlobPath(path);
        boolean w = false;
        boolean x = false;
        if (GenericUtils.length(modes) > 0) {
            for (AccessMode mode : modes) {
                switch (mode) {
                    case READ:
                        break;
                    case WRITE:
                        w = true;
                        break;
                    case EXECUTE:
                        x = true;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported mode: " + mode);
                }
            }
        }

        BlobFileSystem fs = p.getFileSystem();
        BasicFileAttributes attrs = fs.readAttributes(p, BlobFileAttributes.class);
        if ((attrs == null) && !(p.isAbsolute() && p.getNameCount() == 0)) {
            throw new NoSuchFileException(path.toString());
        }

        if (x || (w && fs.isReadOnly())) {
            throw new AccessDeniedException("Filesystem is read-only: " + path.toString());
        }
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        if (isSupportedFileAttributeView(path, type)) {
            if (PosixFileAttributes.class.isAssignableFrom(type)) {
                return type.cast(new BlobFileAttributeView(this, (BlobPath) path, options));
            }
        }

        throw new UnsupportedOperationException("getFileAttributeView(" + path + ") view not supported: " + type.getSimpleName());
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return ((BlobPath) path).getFileSystem().readAttributes((BlobPath) path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        String view;
        String attrs;
        int i = attributes.indexOf(':');
        if (i == -1) {
            view = "basic";
            attrs = attributes;
        } else {
            view = attributes.substring(0, i++);
            attrs = attributes.substring(i);
        }

        return readAttributes(path, view, attrs, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Helper Functions
     */

    public BlobPath toBlobPath(Path path) {
        Objects.requireNonNull(path, "No path provided");
        if (!(path instanceof BlobPath)) {
            throw new ProviderMismatchException("Path is not HTTP / HTTPS: " + path);
        }
        return (BlobPath) path;
    }

    public boolean isSupportedFileAttributeView(Path path, Class<? extends FileAttributeView> type) {
        return isSupportedFileAttributeView(toBlobPath(path).getFileSystem(), type);
    }

    public boolean isSupportedFileAttributeView(BlobFileSystem fs, Class<? extends FileAttributeView> type) {
        Collection<String> views = fs.supportedFileAttributeViews();
        if ((type == null) || GenericUtils.isEmpty(views)) {
            return false;
        } else if (PosixFileAttributeView.class.isAssignableFrom(type)) {
            return views.contains("posix");
        } else if (AclFileAttributeView.class.isAssignableFrom(type)) {
            return views.contains("acl");   // must come before owner view
        } else if (FileOwnerAttributeView.class.isAssignableFrom(type)) {
            return views.contains("owner");
        } else if (BasicFileAttributeView.class.isAssignableFrom(type)) {
            return views.contains("basic"); // must be last
        } else {
            return false;
        }
    }

    public Map<String, Object> readAttributes(Path path, String view, String attrs, LinkOption... options) throws IOException {
        BlobPath p = toBlobPath(path);
        BlobFileSystem fs = p.getFileSystem();
        Collection<String> views = fs.supportedFileAttributeViews();
        if (GenericUtils.isEmpty(views) || (!views.contains(view))) {
            throw new UnsupportedOperationException("readAttributes(" + path + ")[" + view + ":" + attrs + "] view not supported: " + views);
        }

        if ("basic".equalsIgnoreCase(view) || "posix".equalsIgnoreCase(view) || "owner".equalsIgnoreCase(view)) {
            return readPosixViewAttributes(p, view, attrs, options);
        } else {
            return readCustomViewAttributes(p, view, attrs, options);
        }
    }

    protected Map<String, Object> readCustomViewAttributes(BlobPath path, String view, String attrs, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("readCustomViewAttributes(" + path + ")[" + view + ":" + attrs + "] view not supported");
    }

    protected NavigableMap<String, Object> readPosixViewAttributes(
            BlobPath path, String view, String attrs, LinkOption... options)
            throws IOException {
        PosixFileAttributes v = readAttributes(path, PosixFileAttributes.class, options);
        if ("*".equals(attrs)) {
            attrs = "lastModifiedTime,lastAccessTime,creationTime,size,isRegularFile,isDirectory,isSymbolicLink,isOther,fileKey,owner,permissions";
        }

        NavigableMap<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String[] attrValues = GenericUtils.split(attrs, ',');
        for (String attr : attrValues) {
            switch (attr) {
                case "lastModifiedTime":
                    map.put(attr, v.lastModifiedTime());
                    break;
                case "lastAccessTime":
                    map.put(attr, v.lastAccessTime());
                    break;
                case "creationTime":
                    map.put(attr, v.creationTime());
                    break;
                case "size":
                    map.put(attr, v.size());
                    break;
                case "isRegularFile":
                    map.put(attr, v.isRegularFile());
                    break;
                case "isDirectory":
                    map.put(attr, v.isDirectory());
                    break;
                case "isSymbolicLink":
                    map.put(attr, v.isSymbolicLink());
                    break;
                case "isOther":
                    map.put(attr, v.isOther());
                    break;
                case "fileKey":
                    map.put(attr, v.fileKey());
                    break;
                case "owner":
                    map.put(attr, v.owner());
                    break;
                case "permissions":
                    map.put(attr, v.permissions());
                    break;
                case "group":
                    map.put(attr, v.group());
                    break;
                default:
            }
        }
        return map;
    }

}