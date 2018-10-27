package com.shark.unblockingqueue;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by qinghualiu on 2018/10/22.
 */
public class ComsumerSequence {

    private volatile AtomicLong value;

    public ComsumerSequence(long initialValue) {
        this.value = new AtomicLong(initialValue);
    }

    public long get(){
        return value.get();
    }

    public void set(long val){
        value.set(val);
    }

    public boolean compareAndSet(long newVal, long oldVal){
        return value.compareAndSet(oldVal, newVal);
    }
}
