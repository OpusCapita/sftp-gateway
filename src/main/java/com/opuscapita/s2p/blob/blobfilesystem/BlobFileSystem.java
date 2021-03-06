package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.client.BlobFileSystemClient;
import com.opuscapita.s2p.blob.blobfilesystem.client.Exception.BlobException;
import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobDirEntry;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobDirectoryStream;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobPosixFileAttributes;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import lombok.Getter;
import org.apache.sshd.common.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;


public class BlobFileSystem extends FileSystem {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AbstractBlobFileSystemProvider fileSystemProvider;

    @Getter
    private final String access;
    private final ConcurrentMap<String, Map<String, BlobDirEntry>> contents = new ConcurrentHashMap<>();
    private final BlobDirEntry contentTree = new BlobDirEntry();
    @Getter
    private final String id_token;
    @Getter
    private final BlobPath defaultDir;

    @Getter
    private final BlobFileSystemClient delegate;

    private final Set<String> supportedViews = Collections.unmodifiableNavigableSet(
            GenericUtils.asSortedSet(String.CASE_INSENSITIVE_ORDER, "basic", "posix", "owner"));


    public BlobFileSystem(
            AbstractBlobFileSystemProvider fileSystemProvider,
            BlobConfiguration configuration,
            Map<String, ?> env
    ) throws IOException {

        String id_token = "";
        String tenant_id = "";
        if (env != null) {
            id_token = (String) env.get("id_token");
            tenant_id = (String) env.get("tenant_id");
        }
        this.access = configuration.getAccess();
        this.defaultDir = new BlobPath(BlobFileSystem.this, "/".getBytes());
        this.fileSystemProvider = fileSystemProvider;
        this.id_token = id_token;
        this.delegate = new BlobFileSystemClient(new RestTemplateBuilder(), configuration, tenant_id, id_token);
        this.loadContent(new BlobPath(this, "/".getBytes()), true);
    }

    @Override
    public FileSystemProvider provider() {
        return this.fileSystemProvider;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return BlobUtils.HTTP_PATH_SEPARATOR_STRING;
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.singleton(new BlobPath(this, new byte[]{'/'}));
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return null;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return supportedViews;
    }

    @Override
    public Path getPath(String first, String... more) {
        String path;
        if (more.length == 0) {
            path = first;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(first);
            for (String segment : more) {
                if (segment.length() > 0) {
                    if (sb.length() > 0) {
                        sb.append('/');
                    }
                    sb.append(segment);
                }
            }
            path = sb.toString();
        }
        return new BlobPath(this, path.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        int colonIndex = syntaxAndPattern.indexOf('/');
        if (colonIndex <= 0 || colonIndex == syntaxAndPattern.length() - 1) {
            throw new IllegalArgumentException("syntaxAndPattern was \"" + syntaxAndPattern + "\"");
        }

        String syntax = syntaxAndPattern.substring(0, colonIndex);
        String pattern = syntaxAndPattern.substring(colonIndex + 1);
        String expr;
        switch (syntax) {
            case "glob":
                expr = globToRegex(pattern);
                break;
            case "regex":
                expr = pattern;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported syntax \'" + syntax + "\'");
        }
        final Pattern regex = Pattern.compile(expr);
        return path -> regex.matcher(path.toString()).matches();
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("getUserPrincipalLookupService is not implemented");
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("newWatchService is not implemented");
    }


    public InputStream newInputStream(Path path, OpenOption[] options) throws IOException {
        Object content = loadContent(((BlobPath) path).toAbsolutePath());
        if (content instanceof List) {
            throw new IOException("Is a directory");
        }
        String base64 = ((Map<String, String>) content).get("content");
        byte[] data = DatatypeConverter.parseBase64Binary(base64);
        return new ByteArrayInputStream(data);
    }

    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return new BlobDirectoryStream((BlobPath) dir);
    }

    public <A extends BasicFileAttributes> A readAttributes(BlobPath path, Class<A> type, LinkOption... options) throws IOException {
        BlobDirEntry desc = getBlobDirEntry(path, false);
        if (desc == null) {
            desc = (BlobDirEntry) loadContent(path);
        }

        return (A) new BlobPosixFileAttributes(desc.toMap());
    }

    public Object loadContent(BlobPath path) throws IOException {
        return this.loadContent(path, false);
    }

    public Object loadContent(BlobPath path, boolean force) throws IOException {
        Map<String, BlobDirEntry> content;
        BlobDirEntry c = null;
        if (!force) {
            c = this.getBlobDirEntry(path, false);
        }

        if (c == null || c.getChildren().isEmpty() || c.isDirectory()) {
            try {
                content = this.delegate.listFiles(path);
                if (content.isEmpty() && !force && Objects.requireNonNull(getBlobDirEntry(path.getParent(), false)).getChildByName(path.getFileName().toString()) == null) {
                    throw new NoSuchFileException("Directory doesn't exists");
                }
                for (BlobDirEntry entry : content.values()) {
                    this.addBlobDirEntry(path, entry);
                }
            } catch (BlobException e) {
                try {
                    BlobDirEntry entry = this.delegate.listFile(path);
                    this.addBlobDirEntry(path, entry);
                } catch (BlobException e2) {
                    throw new NoSuchFileException(e2.getMessage());
                }
            } catch (NoSuchFileException fileNotFound) {
                throw new NoSuchFileException(fileNotFound.getMessage());
            }
        }

        return c;
    }

    /**
     * Helper Functions
     */

    private String globToRegex(String pattern) {
        StringBuilder sb = new StringBuilder(pattern.length());
        int inGroup = 0;
        int inClass = 0;
        int firstIndexInClass = -1;
        char[] arr = pattern.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            switch (ch) {
                case '\\':
                    if (++i >= arr.length) {
                        sb.append('\\');
                    } else {
                        char next = arr[i];
                        switch (next) {
                            case ',':
                                break;
                            case 'Q':
                            case 'E':
                                sb.append('\\');
                            default:
                                sb.append('\\');
                        }
                        sb.append(next);
                    }
                    break;
                case '*':
                    if (inClass == 0)
                        sb.append(".*");
                    else
                        sb.append('*');
                    break;
                case '?':
                    if (inClass == 0)
                        sb.append('.');
                    else
                        sb.append('?');
                    break;
                case '[':
                    inClass++;
                    firstIndexInClass = i + 1;
                    sb.append('[');
                    break;
                case ']':
                    inClass--;
                    sb.append(']');
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
                        sb.append('\\');
                    sb.append(ch);
                    break;
                case '!':
                    if (firstIndexInClass == i)
                        sb.append('^');
                    else
                        sb.append('!');
                    break;
                case '{':
                    inGroup++;
                    sb.append('(');
                    break;
                case '}':
                    inGroup--;
                    sb.append(')');
                    break;
                case ',':
                    if (inGroup > 0)
                        sb.append('|');
                    else
                        sb.append(',');
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }

    void delete(BlobPath path) throws IOException, BlobException {
        BlobDirEntry entry = getBlobDirEntry(path, false);
        this.delegate.delete(path, Objects.requireNonNull(entry).isDirectory());
        Objects.requireNonNull(getBlobDirEntry(path.getParent(), false)).getChildren().remove(entry);
        this.loadContent(path.getParent(), true);
    }

    void createDirectory(BlobPath path, boolean createMissing) throws BlobException {
        BlobDirEntry entry;
        try {
            entry = this.delegate.createDirectory(path, createMissing);
            this.addBlobDirEntry(path.getParent(), entry);
        } catch (BlobException | IOException e) {
            throw new BlobException(e.getMessage());
        }
    }


    void move(BlobPath src, BlobPath dst) {
        try {
            if (this.delegate.move(src, dst)) {
                BlobDirEntry entry = this.getBlobDirEntry(src, false);
                if (entry != null) {
                    entry.setPath(dst.toString());
                    entry.setName(dst.getFileName().toString());
                }
            }
        } catch (BlobException e) {
            log.warn("Moving the File {} to {} went wrong", src, dst);
        }
    }

    public void copy(BlobPath src, BlobPath dst) {
        try {
            if (this.delegate.copy(src, dst)) {
                BlobDirEntry srcEntry = this.getBlobDirEntry(src, false);
                BlobDirEntry dstEntry = this.getBlobDirEntry(dst, true);
                if (srcEntry != null) {
                    dstEntry.setPath(dst.toString());
                    dstEntry.setName(dst.getFileName().toString());
                }
            }
        } catch (BlobException e) {
            log.warn("Moving the File {} to {} went wrong", src, dst);
        }
    }

    /**
     * ContentTree Helper Functions
     */
    private BlobDirEntry getBlobDirEntry(BlobPath path, boolean createIfNotExist) {
        BlobDirEntry currentEntry = this.contentTree;
        int level = 0;
        List<String> pathPart = Arrays.asList(path.toString().split(BlobUtils.HTTP_PATH_SEPARATOR_STRING));
        if (!pathPart.isEmpty() && pathPart.get(0).isEmpty()) {
            level = 1;
        }
        while (level < pathPart.size() && (currentEntry = currentEntry.getChildByName(pathPart.get(level), createIfNotExist)) != null) {
            level++;
        }
        return currentEntry;
    }

    private void addBlobDirEntry(BlobPath path, BlobDirEntry entry) throws IOException {
        BlobDirEntry parentEntry = getBlobDirEntry(path, true);
        if (parentEntry.hasChild(entry)) {
            parentEntry.getChildren().remove(entry);
        }
        parentEntry.getChildren().add(entry);
    }
}
