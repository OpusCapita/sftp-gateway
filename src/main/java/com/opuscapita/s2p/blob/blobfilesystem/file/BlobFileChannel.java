package com.opuscapita.s2p.blob.blobfilesystem.file;

import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import com.opuscapita.s2p.blob.blobfilesystem.client.BlobFileSystemClient;
import com.opuscapita.s2p.blob.blobfilesystem.client.Mode;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import com.opuscapita.s2p.blob.blobfilesystem.utils.ValidateUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


public class BlobFileChannel extends FileChannel {

    public static final Set<Mode.OpenMode> READ_MODES =
            Collections.unmodifiableSet(EnumSet.of(Mode.OpenMode.Read));

    public static final Set<Mode.OpenMode> WRITE_MODES =
            Collections.unmodifiableSet(
                    EnumSet.of(
                            Mode.OpenMode.Write,
                            Mode.OpenMode.Append,
                            Mode.OpenMode.Create,
                            Mode.OpenMode.Truncate
                    )
            );

    private final BlobPath path;
    private final Collection<Mode.OpenMode> modes;
    private final boolean closeOnExit;
    private final BlobFileSystemClient client;
    private final AtomicLong posTracker = new AtomicLong(0L);
    private final AtomicReference<Thread> blockingThreadHolder = new AtomicReference<>(null);
    private final Object lock = new Object();
    private long size = 0;

    public BlobFileChannel(
            BlobPath path,
            BlobFileSystemClient client,
            boolean closeOnExit,
            Set<? extends OpenOption> options,
            Collection<Mode.OpenMode> modes,
            FileAttribute<?>... attrs
    ) throws IOException {
        super();
        this.path = ValidateUtils.checkNotNullAndNotEmpty(path, "No remote file path specified");
        this.client = Objects.requireNonNull(client, "No SFTP client instance");
        this.closeOnExit = closeOnExit;
        this.modes = modes;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen(Collections.emptySet());
        beginBlocking();
        try {
            return client.fetchFile(path, dst);
        } finally {
            endBlocking(true);
        }
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }


    @Override
    public long position() throws IOException {
        ensureOpen(Collections.emptySet());
        return posTracker.get();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        if (newPosition < 0L) {
            throw new IllegalArgumentException("position(" + path + ") illegal file channel position: " + newPosition);
        }

        ensureOpen(Collections.emptySet());
        posTracker.set(newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void force(boolean metaData) throws IOException {
        ensureOpen(Collections.emptySet());
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
        throw new UnsupportedOperationException();
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureOpen(Collections.emptySet());
        beginBlocking();
        try {
            size = client.putFile(path, src);
            return (int) size();
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            src.flip();
            endBlocking(true);
        }
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void implCloseChannel() throws IOException {
        this.client.closeHttpUrlConnection();
        path.getFileSystem().loadContent(path.getParent(), true);
    }

    private void ensureOpen(Collection<Mode.OpenMode> reqModes) throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }

        if (BlobUtils.size(reqModes) > 0) {
            for (Mode.OpenMode m : reqModes) {
                if (this.modes.contains(m)) {
                    return;
                }
            }

            throw new IOException("ensureOpen(" + path + ") current channel modes (" + this.modes + ") do contain any of the required: " + reqModes);
        }
    }


    private void beginBlocking() {
        begin();
        blockingThreadHolder.set(Thread.currentThread());
    }

    private void endBlocking(boolean completed) throws AsynchronousCloseException {
        blockingThreadHolder.set(null);
        end(completed);
    }
}
