package com.opuscapita.sftp.filesystem;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.auth.model.User;
import com.opuscapita.blob.config.BlobConfiguration;
import com.opuscapita.s2p.blob.blobfilesystem.BlobHttpFileSystemProvider;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.util.Collections;

public class BlobFileSystemFactory extends AbstractLoggingBean implements FileSystemFactory {

    private final BlobConfiguration blobConfiguration;

    public BlobFileSystemFactory(BlobConfiguration configuration) {
        super();
        this.blobConfiguration = configuration;
    }

    @Override
    public FileSystem createFileSystem(Session session) throws IOException {
        try {
            return new BlobHttpFileSystemProvider().newFileSystem(this.computeRootUri(session), Collections.emptyMap());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private URI computeRootUri(Session session) throws URISyntaxException {
        final String tenantId = this.computeTenatId(session);
        return new URI(
                this.blobConfiguration.getMethod() + "://"
                        + this.blobConfiguration.getUrl() + ":"
                        + this.blobConfiguration.getPort() + "/api/"
                        + tenantId + "/"
                        + this.blobConfiguration.getType() + "/"
                        + this.blobConfiguration.getAccess() + "/"
        );
    }

    private String computeTenatId(Session session) {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey((ServerSession) session, AuthResponse.class);
        AuthResponse authResponse = session.getAttribute(authResponseAttributeKey);
        User user = authResponse.getUser();
        return (!user.getCustomerId().isEmpty() && user.getCustomerId() != null ? "c_" + user.getCustomerId() : "s_" + user.getSupplierId());
    }
}
