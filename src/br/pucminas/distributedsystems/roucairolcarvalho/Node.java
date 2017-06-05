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
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Josué
 */
public final class Node implements Runnable {

    private Status status;
    private Integer OSN;
    private Integer HSN;
    private String printServerIp;
    private Integer printServerPort;
    private ArrayList<NodeReference> nodes;
    private Integer responseCounter;
    private final ArrayList<Payload> requestList;
    private final String nodeIp;
    private final Integer nodePortNumber;

    public Node() {
        this.status = Status.AVAILABLE;
        this.HSN = 0;
        this.nodes = new ArrayList<>();
        this.responseCounter = 0;
        this.nodeIp = "127.0.0.1";
        this.nodePortNumber = 10000;
        this.requestList = new ArrayList<>();
    }
    
    public Node(ArrayList<NodeReference> nodes, String nodeIp, 
            Integer nodePortNumber, String printServerIp, Integer printServerPort) {
        this.status = Status.AVAILABLE;
        this.HSN = 0;
        this.printServerPort = printServerPort;
        this.printServerIp = printServerIp;
        this.nodeIp = nodeIp;
        this.nodePortNumber = nodePortNumber;
        this.nodes = nodes;
        this.responseCounter = 0;
        this.requestList = new ArrayList<>();
        
        if (shouldRequest()) {
            System.out.printf("NODE %s:%d: will request\n", nodeIp, nodePortNumber);
            request();
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(nodePortNumber);
            System.out.println("SERVER: running on port " + nodePortNumber);

            // server loop
            while (true) {

                Socket connectionSocket = socket.accept();
                ObjectInputStream in = new ObjectInputStream(
                        connectionSocket.getInputStream());

                Payload request = (Payload) in.readObject();
                
                HSN = HSN > request.getClock() ? HSN : request.getClock();
                
                if (request.getType() == Type.REPLY) {
                    System.out.printf("SERVER: Received REPLY from %s:%d\n", 
                            request.getIp(), request.getPort());
                    responseCounter++;
                } else if (status == Status.AVAILABLE) {
                    Payload reply = new Payload(Type.REPLY, this);
                    send(reply, request.getIp(), request.getPort());
                } else if (status == Status.BUSY) {
                    requestList.add(request);
                } else if (status == Status.WAITING) {
                    if (OSN < request.getClock()) {
                        requestList.add(request);
                    } else {
                        Payload reply = new Payload(Type.REPLY, this);
                        send(reply, request.getIp(), request.getPort());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("SERVER:" + e);
        }
    }
    
    public void request() {
        status = Status.WAITING;
        
        // increment clock
        OSN = HSN + 1;
        Payload payload = new Payload(Type.REQUEST, this);
        
        // send request to all nodes
        nodes.stream().forEach((node) -> {
            send(payload, node.getIp(), node.getPort());
        });
        
        // waiting for nodes reply
        while(responseCounter < nodes.size() - 1) {}
        
        // receive reply of all nodes
        responseCounter = 0;
        status = Status.BUSY;
        
        // send a job to print server
        Payload jobPayload = new Payload(Type.REQUEST, this);
        send(jobPayload, printServerIp, printServerPort);
    }

    private void send(Payload msg, String ip, Integer port) {
        // try to connect to server
        try (Socket clientSocket = new Socket(ip, port)) {
            ObjectOutputStream out = new ObjectOutputStream(
                    clientSocket.getOutputStream());

            // send data
            out.writeObject(msg);

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("CLIENT:" + e);
        }
    }
    
    private boolean shouldRequest() {
        return (new Random().nextDouble() + 0.0) > 0.5;
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

    public Integer getPrintServerPort() {
        return printServerPort;
    }

    public void setPrintServerPort(Integer printServerPort) {
        this.printServerPort = printServerPort;
    }

    public String getServerIp() {
        return printServerIp;
    }

    public void setServerIp(String serverIp) {
        this.printServerIp = serverIp;
    }

    public ArrayList<NodeReference> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<NodeReference> nodes) {
        this.nodes = nodes;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public Integer getNodePortNumber() {
        return nodePortNumber;
    }
}
