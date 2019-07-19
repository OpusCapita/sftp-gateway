package com.opuscapita.sftp.filesystem;

import org.apache.sshd.common.file.util.BasePath;
import org.apache.sshd.common.util.GenericUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

public class BlobFileSystem extends FileSystem {
    private final Path rootPath;
    private final FileSystem rootFs;
    private final FileSystemProvider fileSystemProvider;

    public BlobFileSystem(BlobFileSystemProvider fileSystemProvider, Path root, Map<String, ?> env) {
        super();
        this.fileSystemProvider = fileSystemProvider;
        this.rootPath = Objects.requireNonNull(root, "No root path");
//        this.rootFs = root.getFileSystem();
        this.rootFs = this;
    }


    public String getScheme() {
        return "blob";
    }

    @Override
    public FileSystemProvider provider() {

        return this.fileSystemProvider;
    }

    @Override
    public void close() throws IOException {
        System.out.println("close");
    }

    @Override
    public boolean isOpen() {

        return false;
    }

    @Override
    public boolean isReadOnly() {

        return false;
    }

    @Override
    public String getSeparator() {

        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {

        return null;
    }

    @Override
    public Iterable<FileStore> getFileStores() {

        return null;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {

        return null;
    }

    @Override
    public Path getPath(String first, String... more) {
        StringBuilder sb = new StringBuilder();
        if (!GenericUtils.isEmpty(first)) {
            this.appendDedupSep(sb, first.replace('\\', '/'));
        }

        if (GenericUtils.length(more) > 0) {
            String[] var4 = more;
            int var5 = more.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String segment = var4[var6];
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '/') {
                    sb.append('/');
                }

                this.appendDedupSep(sb, segment.replace('\\', '/'));
            }
        }

        if (sb.length() > 1 && sb.charAt(sb.length() - 1) == '/') {
            sb.setLength(sb.length() - 1);
        }

        String path = sb.toString();
        String root = null;
        if (path.startsWith("/")) {
            root = "/";
            path = path.substring(1);
        }

        String[] names = GenericUtils.split(path, '/');
        Path p = this.create(root, names);

        return p;
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {

        return null;
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {

        return null;
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return null;
    }

    private void appendDedupSep(StringBuilder sb, CharSequence s) {
        for(int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if (ch != '/' || sb.length() == 0 || sb.charAt(sb.length() - 1) != '/') {
                sb.append(ch);
            }
        }

    }

    protected Path create(String root, String... names) {
        return Paths.get(root, names);
    }
}
