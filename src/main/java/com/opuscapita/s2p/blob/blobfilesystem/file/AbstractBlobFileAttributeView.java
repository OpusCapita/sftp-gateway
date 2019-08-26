package com.opuscapita.s2p.blob.blobfilesystem.file;

import com.opuscapita.s2p.blob.blobfilesystem.AbstractBlobFileSystemProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttributeView;
import java.util.Objects;

public abstract class AbstractBlobFileAttributeView implements FileAttributeView {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final AbstractBlobFileSystemProvider provider;
    protected final Path path;
    protected final LinkOption[] options;

    protected AbstractBlobFileAttributeView(AbstractBlobFileSystemProvider provider, Path path, LinkOption... options) {
        this.provider = Objects.requireNonNull(provider, "No file system provider instance");
        this.path = Objects.requireNonNull(path, "No path");
        this.options = options;
    }

    @Override
    public String name() {
        return "view";
    }

    public final AbstractBlobFileSystemProvider provider() {
        return provider;
    }

    public final Path getPath() {
        return path;
    }
}

