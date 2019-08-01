package com.opuscapita.sftp.service.commands;

import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ObjectBuilder;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerManager;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@ComponentScan
public class OCSftpSubsystemFactory extends SftpSubsystemFactory {


    @Autowired
    public OCSftpSubsystemFactory() {
        super();
    }

    @Service
    public static class Builder extends AbstractSftpEventListenerManager implements ObjectBuilder<OCSftpSubsystemFactory> {

        @Autowired
        private OCSftpSubsystemFactory ocFactory;

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
//            OCSftpSubsystemFactory ocFactory = new OCSftpSubsystemFactory();
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
