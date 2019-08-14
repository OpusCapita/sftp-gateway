package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.client.BlobFileSystemClient;
import com.opuscapita.s2p.blob.blobfilesystem.client.Mode;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import com.opuscapita.s2p.blob.blobfilesystem.utils.ValidateUtils;

import java.io.IOException;
import java.io.InputStream;
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
        InputStream is = client.fetchFileAsInputStream(path);
        ReadableByteChannel readableByteChannel = Channels.newChannel(is);

        int totalRead = 0;
        try {
            int read = 0;
            while ((read = readableByteChannel.read(dst)) > 0) {
                totalRead += read;
            }
        } finally {
            endBlocking(true);
            readableByteChannel.close();
            return totalRead;
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
//        if ((position < 0) || (count < 0)) {
//            throw new IllegalArgumentException("transferTo(" + path + ") illegal position (" + position + ") or count (" + count + ")");
//        }
//        ensureOpen(READ_MODES);
//        synchronized (lock) {
//            boolean completed = false;
//            boolean eof = false;
//            long curPos = position;
//            try {
//                beginBlocking();
//
//                int bufSize = (int) Math.min(count, Short.MAX_VALUE + 1);
//                byte[] buffer = new byte[bufSize];
//                long totalRead = 0L;
//                while (totalRead < count) {
//                    int read = sftp.read(handle, curPos, buffer, 0, buffer.length);
//                    if (read > 0) {
//                        ByteBuffer wrap = ByteBuffer.wrap(buffer);
//                        while (wrap.remaining() > 0) {
//                            target.write(wrap);
//                        }
//                        curPos += read;
//                        totalRead += read;
//                    } else {
//                        eof = read == -1;
//                    }
//                }
//                completed = true;
//                return totalRead > 0 ? totalRead : eof ? -1 : 0;
//            } finally {
//                endBlocking(completed);
//            }
//        }
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        if ((position < 0) || (count < 0)) {
            throw new IllegalArgumentException("transferFrom(" + path + ") illegal position (" + position + ") or count (" + count + ")");
        }
        ensureOpen(WRITE_MODES);

        int copySize = 8192;
        boolean completed = false;
        long curPos = (position >= 0L) ? position : posTracker.get();
        long totalRead = 0L;
        byte[] buffer = new byte[(int) Math.min(copySize, count)];

        synchronized (lock) {
            try {
                beginBlocking();

                while (totalRead < count) {
                    ByteBuffer wrap = ByteBuffer.wrap(buffer, 0, (int) Math.min(buffer.length, count - totalRead));
                    int read = src.read(wrap);
                    if (read > 0) {
//                        sftp.write(handle, curPos, buffer, 0, read);
                        curPos += read;
                        totalRead += read;
                    } else {
                        break;
                    }
                }
                completed = true;
                return totalRead;
            } finally {
                endBlocking(completed);
            }
        }
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
