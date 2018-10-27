package com.shark.unblockingqueue;

import java.util.concurrent.TimeoutException;

/**
 * Created by qinghualiu on 2018/10/22.
 */
public interface Waitor {

    void signal();

    long wait(long next, ProducerSequence put, ProcessorHandler handler) throws TimeoutException;

}
