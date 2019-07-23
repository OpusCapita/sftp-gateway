package com.opuscapita.sftp.filesystem;

public class RestHttpFileSystemProvider extends AbstractRestFileSystemProvider {

    public static final String SCHEME = "http";

    @Override
    public final String getScheme() {
        return SCHEME;
    }
}
