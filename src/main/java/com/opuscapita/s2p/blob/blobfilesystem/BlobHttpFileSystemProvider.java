package com.opuscapita.s2p.blob.blobfilesystem;

import org.springframework.stereotype.Service;

@Service
public class BlobHttpFileSystemProvider extends AbstractBlobFileSystemProvider {

    private static final String SCHEME = "http";

    @Override
    public final String getScheme() {
        return SCHEME;
    }
}
