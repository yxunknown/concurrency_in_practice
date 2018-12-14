package multithread;

public class StopThread extends Thread {
    private long i = 0;

    /*
    volatile: 生命一个变量为内存可见，
    当一个现场读取从主内存取读取一个变量到本地内存区进行修改后，通常不会及时的把修改的变量更新到主内存区
    这样会造成其他现场读到的变量为该现场的缓存
     */
    private volatile boolean run = true;

    public StopThread(String name) {
        setName(name);
    }

    @Override
    public void run() {
        long s = System.currentTimeMillis();
        while (run) {
            System.out.println("i is: " + i);
            i++;
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("stop");
        System.out.println("Using time: " + ( e - s) / 1000.0);
    }

    public void cancel() {
        this.run = false;
    }
}
