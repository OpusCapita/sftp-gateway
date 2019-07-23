package com.opuscapita.sftp.filesystem;

import org.springframework.stereotype.Service;

@Service
public class RestHttpFileSystemProvider extends AbstractRestFileSystemProvider {

    public static final String SCHEME = "http";

    @Override
    public final String getScheme() {
        return SCHEME;
    }
}
