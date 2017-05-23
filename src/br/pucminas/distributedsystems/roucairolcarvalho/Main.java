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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josue_000
 */
public class Main {
    
    public static void main(String[] args) {
        
        PrintServer server = new PrintServer(9999);
        Node n1 = new Node(10000);
        Node n2 = new Node(10001);
        Node n3 = new Node(10002);
        
        new Thread(server).start();
        new Thread(n1).start();
        new Thread(n2).start();
        new Thread(n3).start();
        
        try {
            n1.send("teste", 9999);
            n2.send("teste", 9999);
            n3.send("teste", 9999);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
