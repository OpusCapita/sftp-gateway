package com.opuscapita.s2p.blob.blobfilesystem.utils;

import java.util.function.Supplier;

public class Utils {

    private Utils() {}

    public static <T> T nonNull(T o, Supplier<String> msg) {
        if (o == null) {
            throw new IllegalArgumentException(msg.get());
        }
        return o;
    }

    public static class ShouldNotHappenException extends RuntimeException {

        public ShouldNotHappenException(final Throwable e) {
            super("Should not happen", e);
        }
    }
}
