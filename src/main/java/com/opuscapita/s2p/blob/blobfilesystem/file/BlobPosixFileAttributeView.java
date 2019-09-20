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
package com.opuscapita.s2p.blob.blobfilesystem.file;

import com.opuscapita.s2p.blob.blobfilesystem.AbstractBlobFileSystemProvider;
import com.opuscapita.s2p.blob.blobfilesystem.BlobFileSystem;
import com.opuscapita.s2p.blob.blobfilesystem.BlobPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.Set;

public class BlobPosixFileAttributeView extends AbstractBlobFileAttributeView implements PosixFileAttributeView {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Path path;
    private final AbstractBlobFileSystemProvider provider;
    private final LinkOption[] options;

    public BlobPosixFileAttributeView(AbstractBlobFileSystemProvider provider, Path path, LinkOption... options) {
        super(provider, path, options);
        this.path = path;
        this.provider = provider;
        this.options = options;
    }

    @Override
    public String name() {
        return "posix";
    }

    @Override
    public PosixFileAttributes readAttributes() throws IOException {
        return ((BlobFileSystem) path.getFileSystem()).readAttributes((BlobPath) path, BlobPosixFileAttributes.class);
    }

    @Override
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
//        SftpClient.Attributes attrs = new SftpClient.Attributes();
//        if (lastModifiedTime != null) {
//            attrs.modifyTime(lastModifiedTime);
//        }
//        if (lastAccessTime != null) {
//            attrs.accessTime(lastAccessTime);
//        }
//        if (createTime != null) {
//            attrs.createTime(createTime);
//        }
    }

    @Override
    public void setPermissions(Set<PosixFilePermission> perms) throws IOException {
        provider.setAttribute(path, "permissions", perms, options);
    }

    @Override
    public void setGroup(GroupPrincipal group) throws IOException {
        provider.setAttribute(path, "group", group, options);
    }

    @Override
    public UserPrincipal getOwner() throws IOException {
        return readAttributes().owner();
    }

    @Override
    public void setOwner(UserPrincipal owner) throws IOException {
        provider.setAttribute(path, "owner", owner, options);
    }

    @Override
    public String toString() {
        return this.path.toString();
    }
}