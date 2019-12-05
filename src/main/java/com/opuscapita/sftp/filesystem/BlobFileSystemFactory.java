package com.opuscapita.sftp.filesystem;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.auth.model.User;
import com.opuscapita.s2p.blob.blobfilesystem.BlobHttpFileSystemProvider;
import com.opuscapita.s2p.blob.blobfilesystem.config.BlobConfiguration;
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
import java.util.HashMap;
import java.util.Map;

public class BlobFileSystemFactory extends AbstractLoggingBean implements FileSystemFactory {

    private final BlobConfiguration blobConfiguration;

    public BlobFileSystemFactory(BlobConfiguration configuration) {
        super();
        this.blobConfiguration = configuration;
    }

    @Override
    public FileSystem createFileSystem(Session session) throws IOException {
        try {
            Map<String, Object> env = new HashMap<>();
            AuthResponse authResponse = this.authResponse(session);
            env.put("tenant_id", this.computeTenatId(session));
            env.put("refresh_token", authResponse.getRefresh_token());
            env.put("token_type", authResponse.getToken_type());
            env.put("access_token", authResponse.getAccess_token());
            env.put("id_token", authResponse.getId_token());
            env.put("config", blobConfiguration);
            return new BlobHttpFileSystemProvider().newFileSystem(this.computeRootUri(session), env);
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
        return (user.getBusinessPartner().isIscustomer() ? "c_" + user.getBusinessPartner().getId() : "s_" + user.getBusinessPartner().getId());
    }

    private AuthResponse authResponse(Session session) {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey((ServerSession) session, AuthResponse.class);
        return session.getAttribute(authResponseAttributeKey);
    }
}
