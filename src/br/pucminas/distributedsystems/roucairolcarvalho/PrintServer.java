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
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


/**
 *
 * @author Josue
 */
public class PrintServer implements Runnable {

    private final Integer serverPortNumber;
    private final String serverIp;
    private long lastTimestamp;
    
    public PrintServer(String ip, Integer portNumber) {
        this.serverIp = ip;
        this.serverPortNumber = portNumber;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(serverPortNumber);
            
            // Server loop
            while (true) {

                // Request Handle
                Socket connectionSocket = socket.accept();
                ObjectInputStream in = new ObjectInputStream(
                        connectionSocket.getInputStream());

                Payload clientSentence = (Payload) in.readObject();
                
                lastTimestamp = new Date().getTime();
                System.out.printf("\u001B[35mPRINT SERVER: receive a job %s\n", 
                        clientSentence);
                
                for (int i = 0; i < 10; i++) {
                    System.out.println(lastTimestamp++);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.err.println("\u001B[35mPRINT SERVER: dispatch " + e);
                    }
                }
                System.out.printf("\u001B[35mPRINT SERVER: finish job\n");
                
                done(clientSentence.getIp(), clientSentence.getPort());
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("\u001B[35mPRINT SERVER:" + e);
            System.exit(1);
        }
        System.out.printf("\u001B[35mPRINT SERVER: running on %s:%d ", serverIp, 
                serverPortNumber);
    }
    
    private void done(String ip, Integer port) {
        try (Socket clientSocket = new Socket(ip, port)) {
            ObjectOutputStream out = new ObjectOutputStream(
                    clientSocket.getOutputStream());
            out.writeObject(new Payload(Type.DONE));
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("\u001B[35mPRINT SERVER:" + e);
        }
    }

    public Integer getServerPortNumber() {
        return serverPortNumber;
    }

    public String getServerIp() {
        return serverIp;
    }
}
