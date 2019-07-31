package com.opuscapita.s2p.blob.blobfilesystem;

import org.apache.sshd.client.subsystem.sftp.SftpClient;
import org.apache.sshd.common.util.GenericUtils;

import java.util.Comparator;

public class BlobDirEntry {
    public static final Comparator<BlobDirEntry> BY_CASE_SENSITIVE_FILENAME = new Comparator<BlobDirEntry>() {
        @Override
        public int compare(BlobDirEntry o1, BlobDirEntry o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                return GenericUtils.safeCompare(o1.getFilename(), o2.getFilename(), true);
            }
        }
    };

    public static final Comparator<BlobDirEntry> BY_CASE_INSENSITIVE_FILENAME = new Comparator<BlobDirEntry>() {
        @Override
        public int compare(BlobDirEntry o1, BlobDirEntry o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                return GenericUtils.safeCompare(o1.getFilename(), o2.getFilename(), false);
            }
        }
    };

    private final String filename;
    private final String longFilename;

    public BlobDirEntry(String filename, String longFilename) {
        this.filename = filename;
        this.longFilename = longFilename;
    }

    public String getFilename() {
        return filename;
    }

    public String getLongFilename() {
        return longFilename;
    }

    @Override
    public String toString() {
        return getFilename() + "[" + getLongFilename() + "]";
    }
}
