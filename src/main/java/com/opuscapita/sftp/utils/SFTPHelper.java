package com.opuscapita.sftp.utils;

import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.server.session.ServerSession;

public class SFTPHelper {
    private SFTPHelper() {

    }

    public static AttributeRepository.AttributeKey findAttributeKey(ServerSession session, Class instanceOf) {
        for (AttributeRepository.AttributeKey attributeKey : session.attributeKeys()) {
            if (instanceOf.isInstance(session.getAttribute(attributeKey))) {
                return attributeKey;
            }
        }
        return null;
    }
}
