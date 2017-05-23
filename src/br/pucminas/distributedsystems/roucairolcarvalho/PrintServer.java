/*
 * The MIT License
 *
 * Copyright 2017 josue_000.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.pucminas.distributedsystems.roucairolcarvalho;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josue
 */
public class PrintServer implements Runnable {

    private final Integer portNumber;
    private long lastTimestamp;

    public PrintServer(int portNumber) {
        this.portNumber = portNumber;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(portNumber);
            
            // Server loop
            while (true) {

                // Request Handle
                Socket connectionSocket = socket.accept();
                ObjectInputStream in = new ObjectInputStream(
                        connectionSocket.getInputStream());

                Object clientSentence = in.readObject();
                
                System.out.printf("PRINT SERVER: receive a job\n");
                
                lastTimestamp = new Date().getTime();
                
                for (int i = 0; i < 10; i++) {
                    System.out.println(lastTimestamp++);
                    Thread.sleep(500);
                }
                
                System.out.printf("PRINT SERVER: finish job\n");
            }
            
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("PRINT SERVER:" + e);
        }
        System.out.println("PRINT SERVER: running on port " + portNumber);
    }
    
}
