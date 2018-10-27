package com.shark.unblockingqueue;

import java.util.concurrent.TimeoutException;

/**
 * Created by qinghualiu on 2018/10/26.
 */
public class TimeoutWaitor implements Waitor {

    private static final long TIME_OUT = 1000;

    private Object lock = new Object();
    private volatile boolean isTimeout = true;

    @Override
    public void signal() {
        synchronized (lock){
            isTimeout = false;
            lock.notifyAll();
        }
    }

    @Override
    public long wait(long next, ProducerSequence put, ProcessorHandler handler) throws TimeoutException {
        long cursorSeq = put.get();
        long timeout = TIME_OUT;
        if(cursorSeq < next){
            while(cursorSeq < next){
                synchronized (lock){
                    try {
                        lock.wait(timeout);
                    } catch (InterruptedException e) {
                    }

                    if(isTimeout){
                        throw new TimeoutException();
                    }
                }
                cursorSeq = put.get();
            }
        }
        return cursorSeq;
    }
}
