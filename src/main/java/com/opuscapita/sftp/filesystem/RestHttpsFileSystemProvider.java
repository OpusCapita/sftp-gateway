package com.opuscapita.sftp.filesystem;

public class RestHttpsFileSystemProvider extends AbstractRestFileSystemProvider {
    public static final String SCHEME = "https";


    @Override
    public final String getScheme() {
        return SCHEME;
    }
}
