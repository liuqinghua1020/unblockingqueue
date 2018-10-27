package benchmark;

import com.shark.unblockingqueue.ArrayUnblockingQueue;
import com.shark.unblockingqueue.BlockingWaitor;
import com.shark.unblockingqueue.Processor;
import com.shark.unblockingqueue.Waitor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by qinghualiu on 2018/10/27.
 */
public class BlockingQueueBench {

    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 10L;


    public void blockingQueueTest(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(BUFFER_SIZE);

        long start = System.currentTimeMillis();
        Thread thread = new Thread(()->{
            long seqence = 0;
            while(true){
                try {
                    String value = blockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seqence ++;

                if(seqence == ITERATIONS-2){
                    countDownLatch.countDown();
                    break;
                }
            }


        }, "comsuer-thread");
        thread.start();

        for(int i=0;i<ITERATIONS;i++){
            try {
                blockingQueue.put("shark");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("blocking queue spend time is " + (end - start));
    }

    public void unBlockingQueueTest(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final String threadName = "consumer";
        Processor<String> processor = new Processor<String>() {
            private long sequence = 0;
            public void process(String data) {
                if(sequence == ITERATIONS-2){
                    countDownLatch.countDown();
                }else{
                    sequence++;
                }
            }

            public void onTimeout(long currentTime) {
            }

            public void onThrowable(String data, Throwable throwable) {
                throwable.printStackTrace();
                countDownLatch.countDown();
            }
        };


        Waitor waitor = new BlockingWaitor();

        ArrayUnblockingQueue<String> arrayUnblockingQueue = new ArrayUnblockingQueue<String>(threadName,
                BUFFER_SIZE,
                processor,
                waitor);

        long start = System.currentTimeMillis();
        arrayUnblockingQueue.start();

        for(int i=0;i<ITERATIONS;i++){

            arrayUnblockingQueue.put("shark");

        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("unblocking queue spend time is " + (end - start));

    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueueBench blockingQueueBench = new BlockingQueueBench();
        blockingQueueBench.blockingQueueTest();
        blockingQueueBench.unBlockingQueueTest();
    }

}
