package com.opuscapita.sftp.service.commands;

import com.opuscapita.auth.model.AuthResponse;
import com.opuscapita.blob.BlobService;
import com.opuscapita.blob.model.BlobResponse;
import com.opuscapita.sftp.utils.SFTPHelper;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.subsystem.sftp.SftpConstants;
import org.apache.sshd.common.subsystem.sftp.SftpException;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.*;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OCSftpSubsystem extends SftpSubsystem {
    private BlobService blobService;

    public OCSftpSubsystem(
            CloseableExecutorService executorService,
            UnsupportedAttributePolicy policy,
            SftpFileSystemAccessor accessor,
            SftpErrorStatusDataHandler errorStatusDataHandler,
            BlobService _blobService
    ) {
        super(executorService, policy, accessor, errorStatusDataHandler);
        this.blobService = _blobService;
    }

    @Override
    protected void doRealPath(Buffer buffer, int id) throws IOException {
        if (!this.getServerSession().isAuthenticated()) {
            throw new IOException();
        }
        this.blobService.setAuthResponse(this.getAuthResponse());
        super.doRealPath(buffer, id);
    }

    @Override
    protected int doReadDir(
            int id,
            String handle,
            DirectoryHandle dir,
            Buffer buffer,
            int maxSize,
            LinkOption... options
    ) throws IOException {

//        return super.doReadDir(id, handle, dir, buffer, maxSize, options);


        if (!this.getServerSession().isAuthenticated()) {
            throw new IOException();
        }
        int nb = 0;
        List<BlobResponse> responseList = new LinkedList<>();
        try {
            responseList = this.blobService.listFiles("/public/");
        } catch (Throwable e) {
            log.error(e.getMessage());
        }

        Map<String, Path> entries = new TreeMap<>(Comparator.naturalOrder());
        for (BlobResponse response : responseList) {
            if (buffer.wpos() >= maxSize) {
                break;
            }
            Path f = Paths.get(response.getPath());
            entries.put(response.getName(), f);

            buffer.putString(getShortName(f));
            writeAttrs(buffer, response.getAttributes());
            nb++;
        }

//        SftpEventListener listener = getSftpEventListenerProxy();
//        listener.read(getServerSession(), handle, dir, entries);
        return nb;
    }

    @Override
    protected String doOpen(int id, String path, int pflags, int access, Map<String, Object> attrs) throws IOException {
        ServerSession session = getServerSession();
        if (log.isDebugEnabled()) {
            log.debug("doOpen({})[id={}] SSH_FXP_OPEN (path={}, access=0x{}, pflags=0x{}, attrs={})",
                    session, id, path, Integer.toHexString(access), Integer.toHexString(pflags), attrs);
        }

        Path file = resolveFile(path);
        int curHandleCount = handles.size();
        int maxHandleCount = session.getIntProperty(MAX_OPEN_HANDLES_PER_SESSION, DEFAULT_MAX_OPEN_HANDLES);
        if (curHandleCount > maxHandleCount) {
            throw signalOpenFailure(id, path, file, false,
                    new SftpException(SftpConstants.SSH_FX_NO_SPACE_ON_FILESYSTEM,
                            "Too many open handles: current=" + curHandleCount + ", max.=" + maxHandleCount));
        }

        String handle;
        try {
            synchronized (handles) {
                handle = generateFileHandle(file);
                FileHandle fileHandle = new FileHandle(this, file, handle, pflags, access, attrs);
                handles.put(handle, fileHandle);
            }
        } catch (IOException e) {
            throw signalOpenFailure(id, path, file, false, e);
        }

        return handle;
//        return super.doOpen(id, path, pflags, access, attrs);
    }

    private AuthResponse getAuthResponse() {
        AttributeRepository.AttributeKey<AuthResponse> authResponseAttributeKey = SFTPHelper.findAttributeKey(getServerSession(), AuthResponse.class);
        AuthResponse authResponse = getServerSession().getAttribute(authResponseAttributeKey);
        return authResponse;
    }

}
