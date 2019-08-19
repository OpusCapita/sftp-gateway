package com.opuscapita.s2p.blob.blobfilesystem.client;

import com.opuscapita.s2p.blob.blobfilesystem.utils.BlobUtils;

import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public interface Mode {
    enum OpenMode {
        Read,
        Write,
        Append,
        Create,
        Truncate,
        Exclusive;

        public static final Set<OpenOption> SUPPORTED_OPTIONS =
                Collections.unmodifiableSet(
                        EnumSet.of(
                                StandardOpenOption.READ, StandardOpenOption.APPEND,
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                                StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW,
                                StandardOpenOption.SPARSE));

        public static Set<OpenMode> fromOpenOptions(Collection<? extends OpenOption> options) {
            if (BlobUtils.isEmpty(options)) {
                return Collections.emptySet();
            }

            Set<OpenMode> modes = EnumSet.noneOf(OpenMode.class);
            for (OpenOption option : options) {
                if (option == StandardOpenOption.READ) {
                    modes.add(Read);
                } else if (option == StandardOpenOption.APPEND) {
                    modes.add(Append);
                } else if (option == StandardOpenOption.CREATE) {
                    modes.add(Create);
                } else if (option == StandardOpenOption.TRUNCATE_EXISTING) {
                    modes.add(Truncate);
                } else if (option == StandardOpenOption.WRITE) {
                    modes.add(Write);
                } else if (option == StandardOpenOption.CREATE_NEW) {
                    modes.add(Create);
                    modes.add(Exclusive);
                } else if (option == StandardOpenOption.SPARSE) {
                    continue;
                } else {
                    throw new IllegalArgumentException("Unsupported open option: " + option);
                }
            }

            return modes;
        }
    }
}
