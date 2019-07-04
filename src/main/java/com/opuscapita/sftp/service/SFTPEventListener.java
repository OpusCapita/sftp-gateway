package com.opuscapita.sftp.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.Handle;
import org.apache.sshd.server.subsystem.sftp.SftpEventListener;

public class SFTPEventListener implements SftpEventListener {
    private Log log = LogFactory.getLog(SFTPEventListener.class);

    public void opening(ServerSession session, String remoteHandle, Handle localHandle) {
        log.info("opening");
    }

    public void writing(
            ServerSession session, String remoteHandle, FileHandle localHandle,
            long offset, byte[] data, int dataOffset, int dataLen) {
        log.info("writing");
    }
}
