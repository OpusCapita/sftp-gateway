package com.opuscapita.s2p.blob.blobfilesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BlobDirEntry extends AbstractLoggingBean implements Serializable {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

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
                return GenericUtils.safeCompare(o1.getName(), o2.getName(), true);
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
                return GenericUtils.safeCompare(o1.getName(), o2.getName(), false);
            }
        }
    };

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String longFilename;
    @Setter
    @Getter
    private String location;
    @Setter
    @Getter
    private String path;
    @Setter
    @Getter
    private Integer size;
    @Setter
    @Getter
    private Boolean isFile;
    @Setter
    @Getter
    private Boolean isDirectory;
    @Setter
    @Getter
    private Date lastModified;
    @Setter
    @Getter
    private String contentType;
    @Setter
    @Getter
    private String extension;

    public BlobDirEntry(String filename, String longFilename) {
        this.name = filename;
        this.longFilename = longFilename;
        this.isDirectory = false;
        this.isFile = false;
        this.lastModified = Date.from(Instant.now());
        this.location = BlobUtils.HTTP_PATH_SEPARATOR_STRING;
        this.path = BlobUtils.HTTP_PATH_SEPARATOR_STRING;
        this.size = 0;
        this.contentType = "application/json";
        this.extension = "";
    }

    public BlobDirEntry() {

    }

    public static BlobDirEntry fromJson(String jsonString) {
        return gson.fromJson(jsonString, BlobDirEntry.class);
    }


    @Override
    public String toString() {
        return getName();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("filename", getName());
        map.put("longFilename", getLongFilename());
        map.put("isDirectory", getIsDirectory());
        map.put("isFile", getIsFile());
        map.put("lastModified", getLastModified());
        map.put("path", getPath());
        map.put("size", getSize());
        map.put("contentType", getContentType());
        map.put("extension", getExtension());

        return map;
    }
}
