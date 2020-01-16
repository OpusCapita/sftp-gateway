package com.opuscapita.sftp.config;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

@Data
@Configuration
public class SFTPConfiguration {
    @Value(value = "${sftp.service-name}")
    private String serviceName;
    @Value(value = "${sftp.server.port}")
    private int port;
    @Value(value = "${sftp.server.welcome}")
    private String welcome;
    @Value(value = "${sftp.server.host-key}")
    private String hostKey;

    public Path hostKeyFile() throws IOException {
        File f = File.createTempFile("hostKey", "pem");
        FileUtils.writeStringToFile(f, this.getHostKey(), Charset.defaultCharset(), false);
        return f.toPath();
    }
}
