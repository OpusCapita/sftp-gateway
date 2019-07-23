package com.opuscapita.sftp.filesystem;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.auth.model.User;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

public class RestFileSystemFactory extends AbstractLoggingBean implements FileSystemFactory {

    private URI rootUri;

    public RestFileSystemFactory() {
        super();
    }

    @Override
    public FileSystem createFileSystem(Session session) throws IOException {

        String tenantId = this.computeTenatId(session);
        try {
            this.rootUri = new URI("http://jsonplaceholder.typicode.com/todos/");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        RestHttpFileSystemProvider httpFsProvider = new RestHttpFileSystemProvider();
        RestHttpsFileSystemProvider httpsFsProvider = new RestHttpsFileSystemProvider();

//        FileSystemProvider.installedProviders().add(httpFsProvider);
//        FileSystemProvider.installedProviders().add(httpsFsProvider);

        return httpFsProvider.newFileSystem(this.rootUri, Collections.emptyMap());

    }

    private String computeTenatId(Session session) {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey((ServerSession) session, AuthResponse.class);
        AuthResponse authResponse = session.getAttribute(authResponseAttributeKey);
        User user = authResponse.getUser();
        return (!user.getCustomerId().isEmpty() && user.getCustomerId() != null ? "c_" + user.getCustomerId() : "s_" + user.getSupplierId());
    }
}
