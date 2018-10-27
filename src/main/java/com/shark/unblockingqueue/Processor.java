package com.shark.unblockingqueue;

/**
 * Created by qinghualiu on 2018/10/23.
 */
public interface Processor<T> {

    void process(T data);

    void onTimeout(long currentTime);

    void onThrowable(T data, Throwable throwable);

}
