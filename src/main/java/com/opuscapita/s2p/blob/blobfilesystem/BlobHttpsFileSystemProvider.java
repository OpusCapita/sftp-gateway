package com.opuscapita.s2p.blob.blobfilesystem;

import org.springframework.stereotype.Service;

@Service
public class BlobHttpsFileSystemProvider extends AbstractBlobFileSystemProvider {
    private static final String SCHEME = "https";

    @Override
    public final String getScheme() {
        return SCHEME;
    }
}
