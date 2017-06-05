package br.pucminas.distributedsystems.roucairolcarvalho;

import java.util.ArrayList;

/**
 *
 * @author Josué
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("br.pucminas.distributedsystems.roucairolcarvalho.Main.main()");
        
        PrintServer printServer = new PrintServer("127.0.0.1", 9999);
        
        ArrayList<NodeReference> nodes = new ArrayList<>();
        nodes.add(new NodeReference("127.0.0.1", 10000));
        
        dispatch(nodes, printServer);
    }
    
    private static void dispatch(ArrayList<NodeReference> nodes, 
            PrintServer server) {
        new Thread(server).start();
        nodes.forEach((n) -> {
            new Thread(new Node(nodes, n.getIp(), n.getPort(), 
                    server.getServerIp(), server.getServerPortNumber())).start();
        });
    }
    
}
