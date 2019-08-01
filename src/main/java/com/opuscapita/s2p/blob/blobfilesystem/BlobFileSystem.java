package com.opuscapita.s2p.blob.blobfilesystem;

import com.opuscapita.s2p.blob.blobfilesystem.client.BlobFileSystemClient;
import com.opuscapita.s2p.blob.blobfilesystem.client.Exception.BlobException;
import com.opuscapita.s2p.blob.blobfilesystem.file.BlobFileAttributes;
import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;
import lombok.Getter;
import org.apache.sshd.common.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;


public class BlobFileSystem extends FileSystem {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AbstractBlobFileSystemProvider fileSystemProvider;
    //    @Getter
//    private final String endpoint;
    @Getter
    private final String access;
    private final ConcurrentMap<String, List<BlobDirEntry>> contents = new ConcurrentHashMap<>();
    private final String refresh_token;
    private final String token_type;
    private final String access_token;
    private final String id_token;
    private final String tenant_id;
    //    private final RestTemplate restTemplate;
    @Getter
    private final BlobPath defaultDir;

    private final BlobFileSystemClient delegate;

    private final Set<String> supportedViews = Collections.unmodifiableNavigableSet(
            GenericUtils.asSortedSet(String.CASE_INSENSITIVE_ORDER, "basic", "posix", "owner"));

//    private final Set<String> supportedViews = Collections.unmodifiableNavigableSet(
//            GenericUtils.asSortedSet(String.CASE_INSENSITIVE_ORDER, "posix"));


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
        this.access = "public";
        String endpoint = "http://blob:3012/api/" + tenant_id + "/files" + "/" + this.access; // + "/onboarding/eInvoiceSupplierOnboarding";
        this.defaultDir = new BlobPath(BlobFileSystem.this, endpoint.getBytes());
        this.fileSystemProvider = fileSystemProvider;
//        this.restTemplate = new RestTemplate();
        this.access_token = access_token;
        this.tenant_id = tenant_id;
        this.id_token = id_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
//        this.endpoint = endpoint;
        this.delegate = new BlobFileSystemClient(new RestTemplateBuilder(), new URL(endpoint));
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
//        return Collections.singleton(new BlobPath(BlobFileSystem.this, "/", Collections.emptyList()));
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
//        return new BlobPath(BlobFileSystem.this, "/", Collections.emptyList()).create(first, more);
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

    public <A extends BasicFileAttributes> SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>[] attrs) throws IOException {
        Object content = loadContent(((BlobPath) path).toAbsolutePath());
        if (content instanceof List) {
            throw new IOException("Is a directory");
        }
        String base64 = ((Map<String, String>) content).get("content");
        final byte[] data = DatatypeConverter.parseBase64Binary(base64);
        return new URLSeekableByteChannel(data);
    }


    public <A extends BasicFileAttributes> A readAttributes(BlobPath path, Class<A> clazz, LinkOption... options) throws IOException {
        BlobPath absolute = path.toAbsolutePath();
        BlobPath parent = absolute.getParent();
        Object desc = contents.get(absolute.toString());
//        if (desc == null && parent != null) {
//            Object parentContent = contents.get(parent.toString());
//            if (parentContent != null) {
//                for (Map<String, ?> child : (List<Map<String, ?>>) parentContent) {
//                    if (child.get("path").equals(absolute.toString().substring(1))) {
//                        desc = child;
//                        break;
//                    }
//                }
//            }
//        }
        if (desc == null) {
            desc = loadContent(absolute);
        }

        BlobFileAttributes fileAttributes;
        if (desc instanceof List) {
            fileAttributes = new BlobFileAttributes(BlobUtils.getDefaultAttributes());
        } else {
            fileAttributes = new BlobFileAttributes(((BlobDirEntry) desc).toMap());
        }

        return (A) fileAttributes;
    }

    public Object loadContent(BlobPath path) throws IOException {
        List<BlobDirEntry> content = contents.get(path.toString());
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-User-Id-Token", this.id_token);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        HttpEntity<String> entity = new HttpEntity<>("body", headers);
//        ResponseEntity<BlobDirEntry[]> responseEntity = null;
        if (content == null) {
//            URL _url = new URL(endpoint + path.toAbsolutePath());
//            HttpURLConnection uc = (HttpURLConnection) _url.openConnection();
//            responseEntity = restTemplate.exchange(_url.toString(), HttpMethod.GET, entity, BlobDirEntry[].class);
//            if (responseEntity.getStatusCode().isError()) {
//                _url = new URL(_url.toString() + "/");
//                responseEntity = restTemplate.exchange(_url.toString(), HttpMethod.GET, entity, BlobDirEntry[].class);
//            }
            try {
                content = this.delegate.listFiles(path, this.id_token);
                contents.putIfAbsent(path.toString(), content);
            } catch (BlobException e) {
                log.error("Can't load Directory: " + path.toString());
            }
//            try {
//                uc.setRequestProperty("X-User-Id-Token", this.id_token);
//                log.info("Get Data from Path: " + _url.getPath());
//                try (Reader r = new InputStreamReader(wrapStream(uc, uc.getInputStream()), StandardCharsets.UTF_8)) {
//                    content = JsonReader.read(r);
//                    contents.putIfAbsent(path.toString(), content);
//                }
//            } finally {
//                uc.disconnect();
//            }
        }
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
