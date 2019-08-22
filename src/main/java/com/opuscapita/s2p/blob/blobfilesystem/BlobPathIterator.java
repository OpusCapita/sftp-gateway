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

import java.nio.file.Path;
import java.util.Iterator;

public class BlobPathIterator implements Iterator<Path> {
    private final BlobPath p;
    private final Iterator<BlobDirEntry> it;
    private boolean dotIgnored;
    private boolean dotdotIgnored;
    private BlobDirEntry curEntry;

    public BlobPathIterator(BlobPath path, Iterable<BlobDirEntry> iter) {
        this(path, (iter == null) ? null : iter.iterator());
    }

    public BlobPathIterator(BlobPath path, Iterator<BlobDirEntry> iter) {
        p = path;
        it = iter;
        curEntry = nextEntry();
    }

    @Override
    public boolean hasNext() {
        return curEntry != null;
    }

    @Override
    public Path next() {
        BlobDirEntry entry = curEntry;
        curEntry = nextEntry();
        return p.resolve(entry.getName());
    }

    private BlobDirEntry nextEntry() {
        while ((it != null) && it.hasNext()) {
            BlobDirEntry entry = it.next();
            String name = entry.getName();
            if (".".equals(name) && (!dotIgnored)) {
                dotIgnored = true;
            } else if ("..".equals(name) && (!dotdotIgnored)) {
                dotdotIgnored = true;
            } else {
                return entry;
            }
        }

        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("newDirectoryStream(" + p + ") Iterator#remove() N/A");
    }
}