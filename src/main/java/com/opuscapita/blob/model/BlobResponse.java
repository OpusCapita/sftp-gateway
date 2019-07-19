package com.opuscapita.blob.model;

import lombok.Data;
import org.apache.sshd.common.subsystem.sftp.SftpConstants;
import org.apache.sshd.common.subsystem.sftp.SftpHelper;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.common.util.buffer.Buffer;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

@Data
public class BlobResponse {

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
//        Collection<PosixFilePermission> permissionCollections = new LinkedHashSet<>();
//        permissionCollections.add(PosixFilePermission.GROUP_EXECUTE);
//        permissionCollections.add(PosixFilePermission.GROUP_WRITE);
//        permissionCollections.add(PosixFilePermission.GROUP_READ);
//
//        permissionCollections.add(PosixFilePermission.OWNER_EXECUTE);
//        permissionCollections.add(PosixFilePermission.OWNER_WRITE);
//        permissionCollections.add(PosixFilePermission.OWNER_READ);
//
//        permissionCollections.add(PosixFilePermission.OTHERS_EXECUTE);
//        permissionCollections.add(PosixFilePermission.OTHERS_WRITE);
//        permissionCollections.add(PosixFilePermission.OTHERS_READ);
//        attrs.put("permissions", permissionCollections);
        return attrs;
    }

    public <T extends Buffer> T writeAttrs(T buffer, int version) {
        Map<String, ?> attributes = this.getAttributes();
        ValidateUtils.checkTrue(version == SftpConstants.SFTP_V3, "Illegal version: %d", version);

        boolean isReg = SftpHelper.getBool((Boolean) attributes.get("isRegularFile"));
        boolean isDir = SftpHelper.getBool((Boolean) attributes.get("isDirectory"));
        boolean isLnk = SftpHelper.getBool((Boolean) attributes.get("isSymbolicLink"));
        @SuppressWarnings("unchecked")
        Collection<PosixFilePermission> perms = (Collection<PosixFilePermission>) attributes.get("permissions");
        Number size = (Number) attributes.get("size");
        FileTime lastModifiedTime = (FileTime) attributes.get("lastModifiedTime");
        FileTime lastAccessTime = (FileTime) attributes.get("lastAccessTime");
        Map<?, ?> extensions = (Map<?, ?>) attributes.get("extended");
        int flags = ((isReg || isLnk) && (size != null) ? SftpConstants.SSH_FILEXFER_ATTR_SIZE : 0)
                | (attributes.containsKey("uid") && attributes.containsKey("gid") ? SftpConstants.SSH_FILEXFER_ATTR_UIDGID : 0)
                | ((perms != null) ? SftpConstants.SSH_FILEXFER_ATTR_PERMISSIONS : 0)
                | (((lastModifiedTime != null) && (lastAccessTime != null)) ? SftpConstants.SSH_FILEXFER_ATTR_ACMODTIME : 0)
                | ((extensions != null) ? SftpConstants.SSH_FILEXFER_ATTR_EXTENDED : 0);
        buffer.putInt(flags);
        if ((flags & SftpConstants.SSH_FILEXFER_ATTR_SIZE) != 0) {
            buffer.putLong(size.longValue());
        }
        if ((flags & SftpConstants.SSH_FILEXFER_ATTR_UIDGID) != 0) {
            buffer.putInt(((Number) attributes.get("uid")).intValue());
            buffer.putInt(((Number) attributes.get("gid")).intValue());
        }
        if ((flags & SftpConstants.SSH_FILEXFER_ATTR_PERMISSIONS) != 0) {
            buffer.putInt(SftpHelper.attributesToPermissions(isReg, isDir, isLnk, perms));
        }
        if ((flags & SftpConstants.SSH_FILEXFER_ATTR_ACMODTIME) != 0) {
            buffer = SftpHelper.writeTime(buffer, version, flags, lastAccessTime);
            buffer = SftpHelper.writeTime(buffer, version, flags, lastModifiedTime);
        }
        if ((flags & SftpConstants.SSH_FILEXFER_ATTR_EXTENDED) != 0) {
            buffer = SftpHelper.writeExtensions(buffer, extensions);
        }

        return buffer;
    }
}
