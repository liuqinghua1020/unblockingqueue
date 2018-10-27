package com.shark.unblockingqueue;

/**
 * Created by qinghualiu on 2018/10/22.
 */
public class ProcessorThread<T> implements Runnable,ProcessorHandler {

    private volatile boolean interrupt;

    private volatile boolean running;

    private ArrayUnblockingQueue arrayUnblockingQueue;

    private Processor processor;

    private ProducerSequence put;

    private ComsumerSequence get;

    private Waitor waitor;

    private long cacheSenquence = ArrayUnblockingQueue.INITIAL_VALUE;


    public ProcessorThread(ArrayUnblockingQueue arrayUnblockingQueue){
        this.arrayUnblockingQueue = arrayUnblockingQueue;
        this.processor = arrayUnblockingQueue.getProccessor();
        this.put = arrayUnblockingQueue.getPut();
        this.get = arrayUnblockingQueue.getGet();
        this.waitor = arrayUnblockingQueue.getWaitor();
    }

    @Override
    public boolean isInterrupt() {
        return interrupt;
    }

    @Override
    public void run() {
        running = true;
        long avaiable = ArrayUnblockingQueue.INITIAL_VALUE;
        long nextSeq = ArrayUnblockingQueue.INITIAL_VALUE;
        long cursor = ArrayUnblockingQueue.INITIAL_VALUE;
        boolean processing = true;
        //消费消息
        while(running){
            //更新消费者进度，以免多线程影响
            if(processing) {
                processing = false;
                do {
                    cursor = get.get();
                    nextSeq = cursor + 1;
                } while (!get.compareAndSet(nextSeq, cursor));
            }

            //一旦上面更新了get的进度，则表明当前消费者线程可以消费 nextSeq 的消息了

            //如果上一次的还没有消费完，继续消费
            if(nextSeq < avaiable){
                //真正消费消息
                T data = (T) arrayUnblockingQueue.elementAt(nextSeq);
                try {
                    processor.process(data);
                }catch (Exception e){
                    processor.onThrowable(data, e);
                }

                processing = true;
            }else {

                //跟生产者做对比，确认可消费的消息记录索引
                try {
                    //等待可用的序列消息
                    avaiable = waitor.wait(nextSeq, put, this);
                } catch (Exception e) {
                    //TODO
                    processing = true;
                }
            }


        }

    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
