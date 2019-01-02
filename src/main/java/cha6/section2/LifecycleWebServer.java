package cha6.section2;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class LifecycleWebServer {

    /**
     * 创建FixedThreadPool类型线程池
     * 大小为20
     */
    private final ExecutorService exec = new ThreadPoolExecutor(
        20,
        20,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1024),
        new ThreadFactoryBuilder().setNameFormat("HttpRequestConnection-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (!exec.isShutdown()) {
            try {
                // 线程池被关闭后拒绝接受新任务
                final Socket conn = socket.accept();
                exec.execute(() -> {
                    // handle conn
                });
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown()) {
                    System.out.println("task is rejected");
                }
            }
        }
    }

    /**
     * 平滑关闭线程池
     */
    public void shutdown() {
        exec.shutdown();
    }
}
