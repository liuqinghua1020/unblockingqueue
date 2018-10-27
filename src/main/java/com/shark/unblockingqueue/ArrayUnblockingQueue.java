package com.shark.unblockingqueue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by qinghualiu on 2018/10/22.
 */
public class ArrayUnblockingQueue<T> {

    public final static long INITIAL_VALUE = -1;
    private int indexMask =0;
    private String threadName =null;
    private int bufferSize = 0;
    private Waitor waitor = null;
    private Processor<T> proccessor;
    private final ProducerSequence put = new ProducerSequence(INITIAL_VALUE);
    private final ComsumerSequence get = new ComsumerSequence(INITIAL_VALUE);

    private volatile Elements<T> elements;

    private ProcessorThread processorThread;
    private Thread thread;


    public ArrayUnblockingQueue(String threadName, int bufferSize,
                                Processor<T> proccessor,
                                Waitor waitor){

        this.threadName = threadName;

        this.bufferSize = bufferSize;

        this.indexMask = bufferSize - 1;

        this.waitor = waitor;

        this.proccessor = proccessor;

        this.elements = new Elements<T>(bufferSize, this.indexMask);
        for(int i=0;i<bufferSize;i++){
            this.elements.addElement(i, new Element());
        }

    }


    public void start(){
        //启动消费者线程
        processorThread = new ProcessorThread(this);
        thread = new Thread(processorThread, threadName);
        thread.start();
    }

    public void put(T data){
        //往ringBuffer中放元素
        do {
            long cursor = put.get();
            //1.在elements中申请一个位置，cursor+1
            long nextSeq = cursor + 1;
            //2.环圈
            long wrapPoint = nextSeq - bufferSize;
            //TODO 考虑在这里做一次缓存，不用每一次都获取自 get
            //3.消费者消费进度做比较
            long lastComsumerCursor = get.get();
            while (wrapPoint > lastComsumerCursor) {
                //TODO stop
                LockSupport.parkNanos(10);

                //after stop
                lastComsumerCursor = get.get();
            }

            //4 生产者多线程竞争比较
            if (put.compareAndSet(nextSeq, cursor)) {//竞争成功，则
                this.elements.setData(nextSeq, data);
                waitor.signal();
                break;
            }
        }while(true);

    }

    public boolean tryPut(T data){
        return false;
    }

    public void stop(){
        //停止消费者线程
        processorThread.setRunning(false);
    }

    public void stop(long timeout, TimeUnit timeUnit){
        //TODO nop
    }

    public T elementAt(long sequence){
        return this.elements.elementAt(sequence).getValue();
    }


    public Processor<T> getProccessor() {
        return proccessor;
    }

    public ProducerSequence getPut() {
        return put;
    }

    public ComsumerSequence getGet(){
        return get;
    }

    public Waitor getWaitor(){
        return waitor;
    }
}
