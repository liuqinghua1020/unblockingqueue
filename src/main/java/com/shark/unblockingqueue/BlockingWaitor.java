package com.shark.unblockingqueue;

/**
 * Created by qinghualiu on 2018/10/25.
 */
public class BlockingWaitor implements Waitor {


    public Object lock = new Object();

    @Override
    public void signal() {
        synchronized (lock){
            lock.notifyAll();
        }
    }

    @Override
    public long wait(long next, ProducerSequence put, ProcessorHandler handler) {
        long cursorSeq = put.get();
        if(cursorSeq < next){
            while(cursorSeq < next){
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                cursorSeq = put.get();
            }
        }
        return cursorSeq;
    }
}
