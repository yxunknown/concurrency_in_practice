package multithread;

import java.util.concurrent.TimeUnit;

public class StopThreadTest {

    private volatile Boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        StopThreadTest test = new StopThreadTest();
        StopThread st = new StopThread("stop");
        st.start();
        TimeUnit.MILLISECONDS.sleep(1000);
        st.cancel();
    }
}
