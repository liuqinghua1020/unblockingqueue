import com.shark.unblockingqueue.ArrayUnblockingQueue;
import com.shark.unblockingqueue.BlockingWaitor;
import com.shark.unblockingqueue.Processor;
import com.shark.unblockingqueue.Waitor;
import org.junit.Test;

/**
 * Created by qinghualiu on 2018/10/26.
 */
public class Test1 {

    @Test
    public void start() throws InterruptedException {

        final String threadName = "consumer";
        int bufferSize = 16;
        Processor<String> processor = new Processor<String>() {
            public void process(String data) {
                System.out.println(Thread.currentThread() + " comsumer value : " + data);
            }

            public void onTimeout(long currentTime) {
            }

            public void onThrowable(String data, Throwable throwable) {
                System.out.println(Thread.currentThread() + " exception is " + throwable.getMessage());
            }
        };


        Waitor waitor = new BlockingWaitor();

        ArrayUnblockingQueue<String> arrayUnblockingQueue = new ArrayUnblockingQueue<String>(threadName,
                bufferSize,
                processor,
                waitor);

        arrayUnblockingQueue.start();

        System.out.println("shark");
        for(int i=0;i<100000;i++){
            if(i % 50 == 0){
                Thread.sleep(500);
            }
            arrayUnblockingQueue.put(i+"");
        }

    }

}
