package com.opuscapita.s2p.blob.blobfilesystem.file;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;

public class BlobFileAttributes implements BasicFileAttributes {

    private final String type;
    private final long size;

    public BlobFileAttributes(String type, long size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public FileTime lastModifiedTime() {
        return FileTime.fromMillis(System.currentTimeMillis());
    }

    @Override
    public FileTime lastAccessTime() {
        return FileTime.fromMillis(System.currentTimeMillis());
    }

    @Override
    public FileTime creationTime() {
        return FileTime.fromMillis(System.currentTimeMillis());
    }

    @Override
    public boolean isRegularFile() {
        return "file".equals(type);
    }

    @Override
    public boolean isDirectory() {
        return "directory".equals(type);
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public Object fileKey() {
        return null;
    }

}
