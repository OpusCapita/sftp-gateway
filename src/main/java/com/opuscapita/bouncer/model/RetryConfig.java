package com.opuscapita.bouncer.model;

import lombok.Data;

import java.util.Objects;

@Data
public class RetryConfig {

    private int retryTimeout;
    private int retryCount;

    public RetryConfig(int _retryTimeout, int _retryCount) {
        this.setRetryCount(Objects.requireNonNull(_retryCount));
        this.setRetryTimeout(Objects.requireNonNull(_retryTimeout));
    }

    public RetryConfig() {
        this.setRetryCount(30);
        this.setRetryTimeout(1000);
    }
}
