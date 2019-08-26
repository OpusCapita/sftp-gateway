package com.opuscapita.s2p.blob.blobfilesystem.file;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;

public class BlobFileAttributes implements BasicFileAttributes {

    private final Map<String, Object> attributes;

    public BlobFileAttributes(Map<String, Object> _attributes) {
        this.attributes = _attributes;
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
        return (boolean) this.attributes.get("isFile");
    }

    @Override
    public boolean isDirectory() {
        return (boolean) this.attributes.get("isDirectory");
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
        return (int) this.attributes.get("size");
    }

    @Override
    public Object fileKey() {
        return null;
    }

    @Override
    public String toString() {
        return (String) this.attributes.get("path");
    }
}
