/*
 * The MIT License
 *
 * Copyright 2017 Josué.
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

/**
 *
 * @author Josué
 */
public final class Node implements Runnable {

    private final Integer portNumber;

    private Status status;
    private Integer OSN;
    private Integer HSN;

    public Node(Integer portNumber) {
        this.portNumber = portNumber;
        this.status = Status.AVAILABLE;
        this.HSN = 0;
    }

    @Override
    public void run() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(portNumber);
            System.out.println("SERVER: running on port " + portNumber);

            // Server loop
            while (true) {

                // Request Handle
                Socket connectionSocket = welcomeSocket.accept();

                ObjectInputStream in = new ObjectInputStream(
                        connectionSocket.getInputStream());

                Object clientSentence = in.readObject();
                System.out.printf("SERVER: Received %s on port %d\n",
                        clientSentence, portNumber);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("SERVER:" + e);
        }
    }

    public void send(String msg, Integer port) throws ClassNotFoundException {
        // Try to connect to server
        try (Socket clientSocket = new Socket("localhost", port)) {
            ObjectOutputStream out = new ObjectOutputStream(
                    clientSocket.getOutputStream());

            // Send data
            out.writeObject(msg);

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("CLIENT:" + e);
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getOSN() {
        return OSN;
    }

    public void setOSN(Integer OSN) {
        this.OSN = OSN;
    }

    public Integer getHSN() {
        return HSN;
    }

    public void setHSN(Integer HSN) {
        this.HSN = HSN;
    }

}
