package cha6.section2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskExecutorWebServer {
    /**
     * MAX amount of threads
     */
    private static final int NTHREADS = 100;

    /**
     * fixed nums of threads,
     * amount of core threads is equals amount of max threads
     * hence there is no non-core thread
     */
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = () -> {
                // handle connection
            };
            // submit task into thread pool
            exec.execute(task);
        }
    }
}
