package com.opuscapita.s2p.blob.blobfilesystem.file;

import com.opuscapita.s2p.blob.blobfilesystem.AbstractBlobFileSystemProvider;
import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Objects;


public class BlobFileAttributeView extends AbstractLoggingBean implements BasicFileAttributeView {
    protected final AbstractBlobFileSystemProvider provider;
    protected final BlobPath path;
    protected final LinkOption[] options;

    public BlobFileAttributeView(AbstractBlobFileSystemProvider provider, BlobPath path, LinkOption... options) {
        this.provider = Objects.requireNonNull(provider, "No file system provider instance");
        this.path = Objects.requireNonNull(path, "No path");
        this.options = options;
    }

    @Override
    public String name() {
        return "blob";
    }

    @Override
    public BlobFileAttributes readAttributes() throws IOException {

        return new BlobFileAttributes(BlobUtils.getDefaultAttributes());
    }

    @Override
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
    }

    public final AbstractBlobFileSystemProvider provider() {
        return provider;
    }

    public final Path getPath() {
        return path;
    }
}
