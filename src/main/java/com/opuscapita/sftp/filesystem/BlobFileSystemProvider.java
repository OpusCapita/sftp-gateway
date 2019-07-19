package com.opuscapita.sftp.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlobFileSystemProvider extends FileSystemProvider {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Map<Path, BlobFileSystem> fileSystems = new HashMap();

    @Override
    public boolean deleteIfExists(Path path) throws IOException {
        log.info("Delete " + path.toString());
        //        return super.deleteIfExists(path);
        return false;
    }

    @Override
    public String getScheme() {

        return "blob";
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        return this.newFileSystem(Paths.get(uri), Paths.get(uri), env);
    }

    @Override
    public FileSystem getFileSystem(URI uri) {

        return null;
    }

    @Override
    public Path getPath(URI uri) {

        return null;
    }

    @Override
    public FileSystem newFileSystem(Path path, Map<String, ?> env) throws IOException {
        return this.newFileSystem(path, path, env);
    }


    protected FileSystem newFileSystem(Object src, Path path, Map<String, ?> env) throws IOException {
        BlobFileSystem blobFs = null;
        Path root = path;
        synchronized (this.fileSystems) {
            if (!this.fileSystems.containsKey(root)) {
                blobFs = new BlobFileSystem(this, path, env);
                this.fileSystems.put(root, blobFs);
            }
        }

        if (blobFs == null) {
            throw new FileSystemAlreadyExistsException("newFileSystem(" + src + ") already mapped " + root);
        } else {
            if (this.log.isTraceEnabled()) {
                this.log.trace("newFileSystem({}): {}", src, blobFs);
            }

            return blobFs;
        }
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return null;
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        log.info("createDirectory: " + dir.toString());
    }

    @Override
    public void delete(Path path) throws IOException {

    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {

    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {

    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return false;
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
        return null;
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {

    }
}
