package com.opuscapita.sftp.filesystem;

import org.springframework.stereotype.Service;

@Service
public class RestHttpsFileSystemProvider extends AbstractRestFileSystemProvider {
    public static final String SCHEME = "https";

    @Override
    public final String getScheme() {
        return SCHEME;
    }
}
