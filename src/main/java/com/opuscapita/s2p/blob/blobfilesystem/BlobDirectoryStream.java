/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opuscapita.s2p.blob.blobfilesystem;

import org.apache.sshd.client.subsystem.sftp.fs.SftpPath;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implements a remote {@link DirectoryStream}
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class BlobDirectoryStream implements DirectoryStream<Path> {
//    private final Iterable<BlobDirEntry> iter;
    private final Iterator<Map<String, Object>> iter;
    private final BlobPath p;

    /**
     * @param path The remote {@link SftpPath}
     * @throws IOException If failed to initialize the directory access handle
     */
    public BlobDirectoryStream(BlobPath path) throws IOException {
        BlobFileSystem fs = path.getFileSystem();
        p = path;
        iter = ((List<Map<String, Object>>) path.getFileSystem().loadContent(path)).iterator();;
    }

    @Override
    public Iterator<Path> iterator() {
        return new BlobPathIterator(p, iter);
    }

    @Override
    public void close() throws IOException {
    }
}