package com.opuscapita.s2p.blob.blobfilesystem.file;

import lombok.Data;
import org.apache.sshd.common.subsystem.sftp.SftpConstants;
import org.apache.sshd.common.subsystem.sftp.SftpHelper;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.common.util.buffer.Buffer;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

@Data
public class BlobFile {

    private String name;
    private String location;
    private String path;
    private Integer size;
    private Boolean isFile;
    private Boolean isDirectory;
    private Date lastModified;

    public NavigableMap<String, Object> getAttributes() {
        NavigableMap<String, Object> attrs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        attrs.put("isDirectory", this.getIsDirectory());
        attrs.put("isRegularFile", this.getIsFile());
        attrs.put("size", this.getSize());
        attrs.put("lastModifiedTime", this.getLastModified());
        attrs.put("lastAccessTime", this.getLastModified());
        Collection<PosixFilePermission> permissionCollections = new LinkedHashSet<>();
        permissionCollections.add(PosixFilePermission.GROUP_EXECUTE);
        permissionCollections.add(PosixFilePermission.GROUP_WRITE);
        permissionCollections.add(PosixFilePermission.GROUP_READ);

        permissionCollections.add(PosixFilePermission.OWNER_EXECUTE);
        permissionCollections.add(PosixFilePermission.OWNER_WRITE);
        permissionCollections.add(PosixFilePermission.OWNER_READ);

        permissionCollections.add(PosixFilePermission.OTHERS_EXECUTE);
        permissionCollections.add(PosixFilePermission.OTHERS_WRITE);
        permissionCollections.add(PosixFilePermission.OTHERS_READ);
        attrs.put("permissions", permissionCollections);
        return attrs;
    }
}
