package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

public class RequestNeverRetryer implements Retryer {

    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e;
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
