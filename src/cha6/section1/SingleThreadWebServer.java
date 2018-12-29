package cha6.section1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            // receive new connection
            Socket connection = socket.accept();
            // 单线程串行处理任务
            handleConnection(connection);
        }
    }

    public static void handleConnection(Socket connection) {
        // handle connection
    }
}
