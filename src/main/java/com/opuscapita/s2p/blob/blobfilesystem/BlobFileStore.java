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

import org.apache.sshd.common.util.GenericUtils;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.Collection;

public class BlobFileStore extends FileStore {
    private final BlobFileSystem fs;
    private final String name;

    public BlobFileStore(String name, BlobFileSystem fs) {
        this.name = name;
        this.fs = fs;
    }

    public final BlobFileSystem getFileSystem() {
        return fs;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return fs.provider().getScheme();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public long getTotalSpace() throws IOException {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUsableSpace() throws IOException {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
        BlobFileSystem blobFileSystem = getFileSystem();
        AbstractBlobFileSystemProvider provider = (AbstractBlobFileSystemProvider) blobFileSystem.provider();
        return provider.isSupportedFileAttributeView(blobFileSystem, type);
    }

    @Override
    public boolean supportsFileAttributeView(String name) {
        if (GenericUtils.isEmpty(name)) {
            return false;   // debug breakpoint
        }

        FileSystem sftpFs = getFileSystem();
        Collection<String> views = sftpFs.supportedFileAttributeViews();
        return !GenericUtils.isEmpty(views) && views.contains(name);
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        return null;    // no special views supported
    }

    @Override
    public Object getAttribute(String attribute) throws IOException {
        return null;    // no special attributes supported
    }
}
