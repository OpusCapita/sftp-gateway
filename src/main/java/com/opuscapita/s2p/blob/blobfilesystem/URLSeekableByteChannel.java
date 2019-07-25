package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import com.opuscapita.s2p.blob.blobfilesystem.utils.Utils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class URLSeekableByteChannel extends AbstractLoggingBean implements SeekableByteChannel {

    private final URL url;

    private long position = 0;

    private long size = -1;

    private ReadableByteChannel channel = null;
    private InputStream backedStream = null;


    URLSeekableByteChannel(URL url) throws IOException {
        this.url = Utils.nonNull(url, () -> "null URL");
        instantiateChannel(this.position);
    }

    @Override
    public synchronized int read(ByteBuffer dst) throws IOException {
        final int read = channel.read(dst);
        this.position += read;
        return read;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new NonWritableChannelException();
    }

    @Override
    public synchronized long position() throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
        return position;
    }

    @Override
    public synchronized URLSeekableByteChannel position(long newPosition) throws IOException {
        if (newPosition < 0) {
            throw new IllegalArgumentException("Cannot seek a negative position");
        }
        if (!isOpen()) {
            throw new ClosedChannelException();
        }

        if (this.position < newPosition) {
            long bytesToSkip = newPosition - this.position;
            long skipped = backedStream.skip(bytesToSkip);
            log.debug("Skipped {} bytes out of {} for setting position to {} (previously on {})",
                    bytesToSkip, skipped, newPosition, position);
        } else if (this.position > newPosition) {
            close();
            instantiateChannel(newPosition);
        }

        // updates to the new position
        this.position = newPosition;

        return this;
    }

    @Override
    public synchronized long size() throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
        if (size == -1) {
            URLConnection connection = url.openConnection();
            connection.connect();
            try {
                size = connection.getContentLengthLong();
                if (size == -1) {
                    throw new IOException("Unable to retrieve content length for " + url);
                }
            } finally {
                BlobUtils.disconnect(connection);
            }
        }
        return size;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new NonWritableChannelException();
    }

    @Override
    public synchronized boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public synchronized void close() throws IOException {
        channel.close();
    }

    private synchronized void instantiateChannel(final long position) throws IOException {
        final URLConnection connection = url.openConnection();
        if (position > 0) {
            BlobUtils.setRangeRequest(connection, position, -1);
        }

        backedStream = connection.getInputStream();
        channel = Channels.newChannel(backedStream);
    }
}