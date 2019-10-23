package com.opuscapita.bouncer.model;

import lombok.Data;

import java.util.Objects;

@Data
public class RetryConfig {

    private int retryTimeout = 1000;
    private int retryCount = 30;

    public RetryConfig(int _retryTimeout, int _retryCount) {
        this.setRetryCount(Objects.requireNonNull(_retryCount));
        this.setRetryTimeout(Objects.requireNonNull(_retryTimeout));
    }
}
