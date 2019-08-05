package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.client.BlobFileSystemClient;
import org.apache.sshd.common.util.ValidateUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.Set;

public class BlobFileChannel extends FileChannel {

    private final String path;
    private final boolean closeOnExit;
    private final BlobFileSystemClient client;

    public BlobFileChannel(String path, BlobFileSystemClient client, boolean closeOnExit, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        this.path = ValidateUtils.checkNotNullAndNotEmpty(path, "No remote file path specified");
        this.client = Objects.requireNonNull(client, "No SFTP client instance");
        this.closeOnExit = closeOnExit;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return 0;
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return 0;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return 0;
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        return 0;
    }

    @Override
    public long position() throws IOException {
        return 0;
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        return null;
    }

    @Override
    public long size() throws IOException {
        return 0;
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        return null;
    }

    @Override
    public void force(boolean metaData) throws IOException {

    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        return 0;
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        return 0;
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        return 0;
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        return 0;
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        return null;
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        return null;
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        return null;
    }

    @Override
    protected void implCloseChannel() throws IOException {

    }
}
