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

import javax.security.auth.Subject;
import java.nio.file.attribute.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlobPosixFileAttributes implements PosixFileAttributes {

    private final Map<String, Object> attributes;

    public BlobPosixFileAttributes(Map<String, Object> _attributes) {
        this.attributes = _attributes;
    }

    @Override
    public FileTime lastModifiedTime() {
        return FileTime.fromMillis(System.currentTimeMillis());
    }

    @Override
    public FileTime lastAccessTime() {
        return FileTime.fromMillis(System.currentTimeMillis());
    }

    @Override
    public FileTime creationTime() {
        return FileTime.fromMillis(System.currentTimeMillis());
    }

    @Override
    public boolean isRegularFile() {
        return (boolean) this.attributes.get("isFile");
    }

    @Override
    public boolean isDirectory() {
        return (boolean) this.attributes.get("isDirectory");
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public long size() {
        return (int) this.attributes.get("size");
    }

    @Override
    public Object fileKey() {
        return null;
    }

    @Override
    public UserPrincipal owner() {
        return new UserPrincipal() {

            @Override
            public String getName() {
                return "SFTP User";
            }

            @Override
            public boolean implies(Subject subject) {
                return true;
            }
        };
    }

    @Override
    public GroupPrincipal group() {
        return () -> "SFTP User Group";
    }

    @Override
    public Set<PosixFilePermission> permissions() {
        Set<PosixFilePermission> permissionSet = new HashSet<PosixFilePermission>();
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
        permissionSet.add(PosixFilePermission.GROUP_WRITE);
        permissionSet.add(PosixFilePermission.GROUP_READ);
        return permissionSet;
    }

    @Override
    public String toString() {
        return (String) this.attributes.get("path");
    }

}