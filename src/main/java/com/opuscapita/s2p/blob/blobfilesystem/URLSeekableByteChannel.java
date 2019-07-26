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
    private long position;
    private byte[] data;
    public URLSeekableByteChannel(byte[] data) {
        this.data = data;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int l = (int) Math.min(dst.remaining(), size() - position);
        dst.put(data, (int) position, l);
        position += l;
        return l;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        position = newPosition;
        return this;
    }

    @Override
    public long size() throws IOException {
        return data.length;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {
    }
}