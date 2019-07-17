package com.opuscapita.sftp.service;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.auth.model.User;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.session.ServerSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;

public class BlobFileSystem extends AbstractLoggingBean implements FileSystemFactory {

    private Path rootPath;

    public BlobFileSystem() {
        super();
    }

    @Override
    public FileSystem createFileSystem(Session session) throws IOException {
        String tenantId = this.computeTenatId(session);
        this.rootPath = new File("/upload/" + tenantId + "/" + session.getSessionId() + "/").getAbsoluteFile().toPath();
        if (!Files.exists(this.rootPath)) {
            new File(this.rootPath.toString()).mkdirs();
        }
        if (this.rootPath == null) {
            throw new InvalidPathException(tenantId, "Cannot resolve home directory");
        }
        return new RootedFileSystemProvider().newFileSystem(this.rootPath, Collections.emptyMap());
    }

    private String computeTenatId(Session session) {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey((ServerSession) session, AuthResponse.class);
        AuthResponse authResponse = session.getAttribute(authResponseAttributeKey);
        User user = authResponse.getUser();
        return (!user.getCustomerId().isEmpty() && user.getCustomerId() != null ? "c_" + user.getCustomerId() : "s_" + user.getSupplierId());
    }

    public Path getRootPath() {
        return this.rootPath;
    }
}
