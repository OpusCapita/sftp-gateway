package com.opuscapita.s2p.blob.blobfilesystem.file;

import javax.security.auth.Subject;
import java.nio.file.attribute.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlobFileAttributes implements PosixFileAttributes {

    private final String type;
    private final long size;

    public BlobFileAttributes(String type, long size) {
        this.type = type;
        this.size = size;
    }

    public BlobFileAttributes(Map<String, Object> attributes) {
        System.out.println(attributes);
        if((boolean)attributes.get("isDirectory")) {
            this.type = "directory";
        } else {
            this.type = "file";
        }

        this.size = (long) attributes.get("size");
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

    @Override
    public UserPrincipal owner() {
        return new UserPrincipal() {

            @Override
            public String getName() {
                return "SFTP User";
            }

            @Override
            public boolean implies(Subject subject) {
                return true;
            }
        };
    }

    @Override
    public GroupPrincipal group() {
        return null;
    }

    @Override
    public Set<PosixFilePermission> permissions() {
        Set<PosixFilePermission> permissionSet = new HashSet<PosixFilePermission>();
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
        permissionSet.add(PosixFilePermission.GROUP_WRITE);
        permissionSet.add(PosixFilePermission.GROUP_READ);
        return permissionSet;
    }
}
