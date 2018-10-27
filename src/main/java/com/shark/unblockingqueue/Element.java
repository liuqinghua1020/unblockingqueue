package com.shark.unblockingqueue;

/**
 * Created by qinghualiu on 2018/10/22.
 */
public class Element<T> {
    volatile T value;

    public void setValue(T value){
        this.value = value;
    }

    public T getValue(){
        return value;
    }

}
