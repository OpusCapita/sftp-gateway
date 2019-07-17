package com.opuscapita.sftp.service;

import com.opuscapita.sftp.service.commands.OCSftpSubsystem;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ObjectBuilder;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerManager;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

public class OCSftpSubsystemFactory extends SftpSubsystemFactory {

    public OCSftpSubsystemFactory() {
        super();
    }

    public static class Builder extends AbstractSftpEventListenerManager implements ObjectBuilder<OCSftpSubsystemFactory> {

        Builder() {
            super();
        }

        @Override
        public OCSftpSubsystemFactory get() {
            return this.build();
        }

        @Override
        public OCSftpSubsystemFactory build() {
            SftpSubsystemFactory factory = new SftpSubsystemFactory.Builder().build();
            OCSftpSubsystemFactory ocFactory = new OCSftpSubsystemFactory();
            ocFactory.setExecutorService(factory.getExecutorService());
            ocFactory.setUnsupportedAttributePolicy(factory.getUnsupportedAttributePolicy());
            ocFactory.setFileSystemAccessor(factory.getFileSystemAccessor());
            ocFactory.setErrorStatusDataHandler(factory.getErrorStatusDataHandler());
            GenericUtils.forEach(getRegisteredListeners(), ocFactory::addSftpEventListener);
            return ocFactory;
        }
    }


    @Override
    public Command create() {
        OCSftpSubsystem subsystem =
                new OCSftpSubsystem(getExecutorService(),
                        getUnsupportedAttributePolicy(), getFileSystemAccessor(),
                        getErrorStatusDataHandler());
        GenericUtils.forEach(getRegisteredListeners(), subsystem::addSftpEventListener);
        return subsystem;
    }
}
