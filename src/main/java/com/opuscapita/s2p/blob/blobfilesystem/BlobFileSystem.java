package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.file.BlobFile;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobFileAttributes;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import com.opuscapita.s2p.blob.blobfilesystem.utils.JsonReader;
import org.apache.sshd.client.subsystem.sftp.fs.SftpFileSystem;
import org.apache.sshd.common.util.GenericUtils;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;


public class BlobFileSystem extends FileSystem {
    private final AbstractBlobFileSystemProvider fileSystemProvider;
    private final String endpoint;
    private final ConcurrentMap<String, Object> contents = new ConcurrentHashMap<>();
    private final String refresh_token;
    private final String token_type;
    private final String access_token;
    private final String id_token;
    private final String tenant_id;
    private final RestTemplate restTemplate;
    private final Set<String> supportedViews = Collections.unmodifiableNavigableSet(
            GenericUtils.asSortedSet(String.CASE_INSENSITIVE_ORDER, "basic", "posix", "owner"));;

    public BlobFileSystem(AbstractBlobFileSystemProvider fileSystemProvider, Map<String, ?> env) throws IOException {

        String refresh_token = "";
        String token_type = "";
        String access_token = "";
        String id_token = "";
        String tenant_id = "";
        if (env != null) {
            refresh_token = (String) env.get("refresh_token");
            token_type = (String) env.get("token_type");
            access_token = (String) env.get("access_token");
            id_token = (String) env.get("id_token");
            tenant_id = (String) env.get("tenant_id");
        }
        String endpoint = "http://blob:3012/api/" + tenant_id + "/files/public";
        this.fileSystemProvider = fileSystemProvider;
        this.restTemplate = new RestTemplate();
        this.access_token = access_token;
        this.tenant_id = tenant_id;
        this.id_token = id_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.endpoint = endpoint;
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
        return new PathMatcher() {
            @Override
            public boolean matches(Path path) {
                return regex.matcher(path.toString()).matches();
            }
        };
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
        Object content = loadContent(path.toAbsolutePath().toString());
        if (content instanceof List) {
            throw new IOException("Is a directory");
        }
        String base64 = ((Map<String, String>) content).get("content");
        byte[] data = DatatypeConverter.parseBase64Binary(base64);
        return new ByteArrayInputStream(data);
    }

    public DirectoryStream<Path> newDirectoryStream(final Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        final Object content = loadContent(dir.toAbsolutePath().toString());
        System.out.println("test");
        if (content instanceof BlobFile) {
            throw new IOException("Is a file");
        }
        final Iterator<Map<String, Object>> delegate = ((List<Map<String, Object>>) content).iterator();
        System.out.println("test");
        return new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return new Iterator<Path>() {
                    final Iterator<Map<String, Object>> delegate = ((List<Map<String, Object>>) content).iterator();

                    @Override
                    public boolean hasNext() {
                        return delegate.hasNext();
                    }

                    @Override
                    public BlobPath next() {
                        Map<String, Object> val = delegate.next();
                        return new BlobPath(BlobFileSystem.this, ((String) val.get("path")).getBytes(StandardCharsets.UTF_8));
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public void close() throws IOException {
                System.out.println("");
            }
        };
    }

    public <A extends BasicFileAttributes> SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>[] attrs) throws IOException {
        Object content = loadContent(path.toAbsolutePath().toString());
        if (content instanceof List) {
            throw new IOException("Is a directory");
        }
        String base64 = ((Map<String, String>) content).get("content");
        final byte[] data = DatatypeConverter.parseBase64Binary(base64);
        return new URLSeekableByteChannel(data);
    }

    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (type.isAssignableFrom(PosixFileAttributes.class)) {
            return type.cast(this.provider().getFileAttributeView(path, PosixFileAttributeView.class, options).readAttributes());
        }

        throw new UnsupportedOperationException("readAttributes(" + path + ")[" + type.getSimpleName() + "] N/A");
    }


    public <A extends BasicFileAttributes> A readAttributes(BlobPath path, Class<A> clazz, LinkOption... options) throws IOException {
//        if (clazz != BasicFileAttributes.class && clazz != PosixFileAttributes.class) {
//            throw new UnsupportedOperationException();
//        }

        BlobPath absolute = path.toAbsolutePath();
        BlobPath parent = absolute.getParent();
//        Object desc = loadContent(absolute.toString());
        Object desc = contents.get(absolute.toString());
        if (desc == null && parent != null) {
            Object parentContent = contents.get(parent.toString());
            if (parentContent != null) {
                for (Map<String, ?> child : (List<Map<String, ?>>) parentContent) {
                    if (child.get("path").equals(absolute.toString().substring(1))) {
                        desc = child;
                        break;
                    }
                }
            }
        }
        if (desc == null) {
            desc = loadContent(absolute.toString());
        }
        String type;
        long size;
        if (desc instanceof List || desc instanceof BlobFile[]) {
            type = "directory";
            size = 0;
        } else {
            type = (String) ((Map) desc).get("type");
            size = ((Number) ((Map) desc).get("size")).longValue();
        }
        A fileAttributes = (A) new BlobFileAttributes(type, size);
//        if (clazz.isAssignableFrom(PosixFileAttributes.class)) {
//            fileAttributes = clazz.cast(this.provider().getFileAttributeView(path, PosixFileAttributeView.class, options).readAttributes());
//        }
        return fileAttributes;
    }

    private Object loadContent(String path) throws IOException {
        Object content = contents.get(path);
        if (content == null) {
            URL _url = new URL(endpoint + path);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//            headers.set("X-User-Id-Token", this.id_token);
//            HttpEntity<String> entity = new HttpEntity<>("body", headers);
//            content = restTemplate.exchange(endpoint + path, HttpMethod.GET, entity, BlobFile[].class);
            HttpURLConnection uc = (HttpURLConnection) _url.openConnection();
            try {
                uc.setRequestProperty("X-User-Id-Token", this.id_token);
                try (Reader r = new InputStreamReader(wrapStream(uc, uc.getInputStream()), StandardCharsets.UTF_8)) {
                    content = JsonReader.read(r);
                    contents.putIfAbsent(path, content);
                }
            } finally {
                uc.disconnect();
            }
//            contents.putIfAbsent(path, ((ResponseEntity<BlobFile[]>) content).getBody());
        }
//        BlobFile[] cArray = ((ResponseEntity<BlobFile[]>) content).getBody();
        return content;
    }

    private InputStream wrapStream(HttpURLConnection uc, InputStream in) throws IOException {
        String encoding = uc.getContentEncoding();
        if (encoding == null || in == null) {
            return in;
        }
        throw new UnsupportedOperationException("Unexpected Content-Encoding: " + encoding);
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
                                // escape not needed
                                break;
                            case 'Q':
                            case 'E':
                                // extra escape needed
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
}
