/*
 * Copyright (C) 2019 The concurrency_in_practice Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cha7.section1;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * write some description here
 *
 * @author hercat
 * @date 2019-01-04 15:39
 */

public class ReaderThread extends Thread {
    private final Socket socket;
    private final InputStream inputStream;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
    }

    /**
     * override the implementation of interrupt
     * close the socket and handle standard interrupt request
     */
    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignore) {
            // do nothing here
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buff = new byte[1024];
            while (true) {
                int n = inputStream.read(buff);
                if (n < 0) {
                    break;
                } else if (n > 0) {
                    // process buff
                    System.out.println(buff);
                }
            }
        } catch (IOException e) {
            // allow thread exit
        }
    }
}
