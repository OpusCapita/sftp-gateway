package com.opuscapita.s2p.blob.blobfilesystem.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sshd.common.util.GenericUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class BlobDirEntry implements Serializable {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static final Comparator<BlobDirEntry> BY_CASE_SENSITIVE_FILENAME = (o1, o2) -> {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        } else {
            return GenericUtils.safeCompare(o1.getName(), o2.getName(), true);
        }
    };

    public static final Comparator<BlobDirEntry> BY_CASE_INSENSITIVE_FILENAME = (o1, o2) -> {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        } else {
            return GenericUtils.safeCompare(o1.getName(), o2.getName(), false);
        }
    };

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

    private BlobDirEntry(String filename, String longFilename) {
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
        if (this.isDirectory() && !this.getName().equals("/")) {
            return this.getName() + "/";
        }
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        BlobDirEntry other = (BlobDirEntry) Objects.requireNonNull(obj);
        return (this.getName().equals(other.getName())
                && this.getLocation().equals(other.getLocation())
                && this.isDirectory() == other.isDirectory()
                && this.isFile() == other.isFile());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("filename", getName());
        map.put("longFilename", getLongFilename());
        map.put("isDirectory", isDirectory());
        map.put("isFile", isFile());
        map.put("lastModified", getLastModified());
        map.put("path", getPath());
        map.put("size", getSize());
        map.put("contentType", getContentType());
        map.put("extension", getExtension());

        return map;
    }

    public boolean isDirectory() {
        return this.isDirectory.booleanValue();
    }

    public boolean isFile() {
        return this.isFile.booleanValue();
    }

    public BlobDirEntry getChildByName(String name) {
        return this.getChildByName(name, false);
    }

    public BlobDirEntry getChildByName(String name, boolean createIfNotExists) {
        BlobDirEntry namedEntry = null;
        for (BlobDirEntry entry : this.getChildren()) {
            if (name.startsWith(entry.getName())) {
                namedEntry = entry;
                break;
            }
        }
        if (namedEntry == null && createIfNotExists) {
            namedEntry = new BlobDirEntry(name, name);
            namedEntry.setPath(this.getPath() + BlobUtils.HTTP_PATH_SEPARATOR_STRING + name);
        }
        return namedEntry;
    }

    public boolean hasChild(BlobDirEntry entry) {
        return this.getChildren().contains(entry);
    }
}
