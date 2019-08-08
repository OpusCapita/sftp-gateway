package com.opuscapita.s2p.blob.blobfilesystem;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.opuscapita.s2p.blob.blobfilesystem.client.BlobFileSystemClient;
import com.opuscapita.s2p.blob.blobfilesystem.client.Mode;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import com.opuscapita.s2p.blob.blobfilesystem.utils.ValidateUtils;
import org.apache.sshd.client.subsystem.sftp.fs.SftpFileSystemChannel;
import sun.nio.ch.Util;

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
        ReadableByteChannel readableByteChannel = Channels.newChannel(client.fetchFile(path));
        return (int) transferFrom(readableByteChannel, 0, Long.MAX_VALUE);


//        return (int) doRead(Collections.singletonList(dst), -1);
//        ensureOpen(Collections.emptySet());
//        beginBlocking();
//        InputStream is = client.fetchFile(path);
//        byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
//        int n;
//        int totalRead = 0;
//        try {
//            while ((n = is.read(byteChunk)) > 0) {
//                dst.put(byteChunk);
//                position(position() + n);
//                totalRead += n;
//            }
//        } catch (IOException e) {
//            dst.clear();
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
//        endBlocking(true);
//        return totalRead;
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    private long doRead(List<ByteBuffer> buffers, long position) throws IOException {
        ensureOpen(READ_MODES);
        synchronized (lock) {
            boolean completed = false;
            boolean eof = false;
            long curPos = (position >= 0L) ? position : posTracker.get();
            InputStream is = client.fetchFile(path);
            try {
                long totalRead = 0;
                beginBlocking();
                loop:
                for (ByteBuffer buffer : buffers) {
                    int n;
                    while (buffer.remaining() > 0) {
                        ByteBuffer wrap = buffer;
                        if (!buffer.hasArray()) {
                            wrap = ByteBuffer.allocate(Math.min(BlobUtils.DEFAULT_COPY_SIZE, buffer.remaining()));
                        }
                        int read = is.read(wrap.array());
                        if (read > 0) {
                            if (wrap == buffer) {
                                wrap.position(wrap.position() + read);
                            } else {
                                buffer.put(wrap.array(), wrap.arrayOffset(), read);
                            }
                            curPos += read;
                            totalRead += read;
                        } else {
                            eof = read == -1;
                            break loop;
                        }
                    }
                }
                completed = true;
                if (totalRead > 0) {
                    return totalRead;
                }

                if (eof) {
                    return -1;
                } else {
                    return 0;
                }
            } finally {
                if (position < 0L) {
                    posTracker.set(curPos);
                }
                if (is != null) {
                    is.close();
                }
                endBlocking(completed);
            }
        }
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
        return 0;
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

//        if (!target.isOpen())
//            throw new ClosedChannelException();
//        if ((position < 0) || (count < 0))
//            throw new IllegalArgumentException();
//        long sz = size();
//        if (position > sz)
//            return 0;
//        int icount = (int) Math.min(count, Integer.MAX_VALUE);
//        if ((sz - position) < icount)
//            icount = (int) (sz - position);
//
//        long n;
//
//        // Attempt a direct transfer, if the kernel supports it
//        if ((n = transferToDirectly(position, icount, target)) >= 0)
//            return n;
//
//        // Attempt a mapped transfer, but only to trusted channel types
//        if ((n = transferToTrustedChannel(position, icount, target)) >= 0)
//            return n;
//
//        // Slow path for untrusted targets
//        return transferToArbitraryChannel(position, icount, target);
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {

        if ((position < 0) || (count < 0)) {
            throw new IllegalArgumentException("transferFrom(" + path + ") illegal position (" + position + ") or count (" + count + ")");
        }
        ensureOpen(WRITE_MODES);

        int copySize = 4096;
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


//        ensureOpen(Collections.emptySet());
//        if (!src.isOpen())
//            throw new ClosedChannelException();
//        if ((position < 0) || (count < 0))
//            throw new IllegalArgumentException();
//        if (position > size())
//            return 0;
//        if (src instanceof FileChannel)
//            return transferFromFileChannel((FileChannel) src,
//                    position, count);
//
//        return transferFromArbitraryChannel(src, position, count);
//        return 0;
    }

    private long transferFromFileChannel(FileChannel src,
                                         long position, long count)
            throws IOException {
        if (!src.isOpen())
            throw new NonReadableChannelException();
        synchronized (src.lock()) {
            long pos = src.position();
            long max = Math.min(count, src.size() - pos);

            long remaining = max;
            long p = pos;
            while (remaining > 0L) {
                long size = Math.min(remaining, 8192);
                // ## Bug: Closing this channel will not terminate the write
                MappedByteBuffer bb = src.map(MapMode.READ_ONLY, p, size);
                try {
                    long n = write(bb, position);
                    assert n > 0;
                    p += n;
                    position += n;
                    remaining -= n;
                } catch (IOException ioe) {
                    // Only throw exception if no bytes have been written
                    if (remaining == max)
                        throw ioe;
                    break;
                } finally {
                }
            }
            long nwritten = max - remaining;
            src.position(pos + nwritten);
            return nwritten;
        }
    }

    private long transferFromArbitraryChannel(ReadableByteChannel src,
                                              long position, long count)
            throws IOException {
        // Untrusted target: Use a newly-erased buffer
        int c = (int) Math.min(count, 8192);
        ByteBuffer bb = Util.getTemporaryDirectBuffer(c);
        long tw = 0;                    // Total bytes written
        long pos = position;
        try {
//            Util.erase(bb);
            while (tw < count) {
                bb.limit((int) Math.min((count - tw), (long) 8192));
                // ## Bug: Will block reading src if this channel
                // ##      is asynchronously closed
                int nr = src.read(bb);
                if (nr <= 0)
                    break;
                bb.flip();
                int nw = write(bb, pos);
                tw += nw;
                if (nw != nr)
                    break;
                pos += nw;
                bb.clear();
            }
            return tw;
        } catch (IOException x) {
            if (tw > 0)
                return tw;
            throw x;
        } finally {
            Util.releaseTemporaryDirectBuffer(bb);
        }
    }


    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        return 0;
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        client.putFile(path, new ByteBufferBackedInputStream(src));
        return 0;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        client.putFile(path, new ByteBufferBackedInputStream(src));
        return 0;
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected long doWrite(List<ByteBuffer> buffers, long position) throws IOException {
        ensureOpen(WRITE_MODES);
        synchronized (lock) {
            boolean completed = false;
            long curPos = (position >= 0L) ? position : posTracker.get();
            try {
                long totalWritten = 0L;
                beginBlocking();
                for (ByteBuffer buffer : buffers) {
                    while (buffer.remaining() > 0) {
                        ByteBuffer wrap = buffer;
                        if (!buffer.hasArray()) {
                            wrap = ByteBuffer.allocate(Math.min(BlobUtils.DEFAULT_COPY_SIZE, buffer.remaining()));
                            buffer.get(wrap.array(), wrap.arrayOffset(), wrap.remaining());
                        }
                        int written = wrap.remaining();
//                        client.putFile();
//                        sftp.write(handle, curPos, wrap.array(), wrap.arrayOffset() + wrap.position(), written);
                        if (wrap == buffer) {
                            wrap.position(wrap.position() + written);
                        }
                        curPos += written;
                        totalWritten += written;
                    }
                }
                completed = true;
                return totalWritten;
            } finally {
                if (position < 0L) {
                    posTracker.set(curPos);
                }
                endBlocking(completed);
            }
        }
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
