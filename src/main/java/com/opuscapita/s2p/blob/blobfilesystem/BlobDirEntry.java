package com.opuscapita.s2p.blob.blobfilesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class BlobDirEntry implements Serializable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
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

//    @Getter
//    @Setter
//    private BlobDirEntry parent;
    @Getter
    private List<BlobDirEntry> children;

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
        this.children = new ArrayList<>();
    }

    public BlobDirEntry() {
        this.children = new ArrayList<>();
        this.name = BlobUtils.HTTP_PATH_SEPARATOR_STRING;
        this.longFilename = BlobUtils.HTTP_PATH_SEPARATOR_STRING;
        this.isDirectory = true;
        this.isFile = false;
        this.lastModified = Date.from(Instant.now());
        this.location = BlobUtils.HTTP_PATH_SEPARATOR_STRING;
        this.path = BlobUtils.HTTP_PATH_SEPARATOR_STRING;
        this.size = 0;
        this.contentType = "application/json";
        this.extension = "";
    }

    public static BlobDirEntry fromJson(String jsonString) {
        return gson.fromJson(jsonString, BlobDirEntry.class);
    }


    @Override
    public String toString() {
        if (this.getIsDirectory() && !this.getName().equals("/")) {
            return this.getName() + "/";
        }
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        BlobDirEntry other = (BlobDirEntry) obj;
        return (this.getName().equals(other.getName())
                && this.getLocation().equals(other.getLocation())
                && this.getIsDirectory().equals(other.getIsDirectory())
                && this.getIsFile().equals(other.isFile));
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

    public BlobDirEntry getChildByName(String name) {
        return this.getChildByName(name, false);
    }

    public BlobDirEntry getChildByName(String name, boolean createIfNotExists) {
        BlobDirEntry namedEntry = null;
        for (BlobDirEntry entry : this.getChildren()) {
            if (entry.getName().equals(name)) {
                namedEntry = entry;
                break;
            }
        }
        if (namedEntry == null && createIfNotExists) {
            namedEntry = new BlobDirEntry(name, name);
            namedEntry.setPath(this.getPath() + BlobUtils.HTTP_PATH_SEPARATOR_STRING + name);
//            namedEntry.setLocation(getParent() != null ? getParent().getPath() : BlobUtils.HTTP_PATH_SEPARATOR_STRING);
        }
        return namedEntry;
    }

    public boolean hasChild(BlobDirEntry entry) {
        return this.getChildren().contains(entry);
    }
}
