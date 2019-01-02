package cha6.section1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPerTaskWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            // 为每个连接新建立一个线程，下一个连接不用等待当前连接处理结束，从而提高每个连接的响应性
            // 任务可以并行处理，能同时处理多个连接
            // 任务处理的代码必须是线程安全的
            Runnable task = () -> {
                // handle connection
                // thread-safe code here
            };
            new Thread(task).start();
        }

    }
}
