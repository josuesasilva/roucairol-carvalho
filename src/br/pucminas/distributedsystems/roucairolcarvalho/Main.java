package br.pucminas.distributedsystems.roucairolcarvalho;

import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

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
        nodes.add(new NodeReference("127.0.0.1", 10001));
        nodes.add(new NodeReference("127.0.0.1", 10002));
        
        
        dispatch(nodes, printServer, true);
    }
    
    private static void dispatch(@NotNull ArrayList<NodeReference> nodes, 
            @NotNull PrintServer server, @NotNull boolean startServer) {
        if (startServer) {
            new Thread(server).start();
            loading();
        }
        
        ArrayList<Node> startedNodes = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        
        ConnectionManager manager = () -> {
            if (counter.incrementAndGet() == nodes.size()) {
                startedNodes.forEach((n) -> {
                   n.shouldRequest(); 
                });
            }
        };
        
        nodes.forEach((n) -> {
            Node node = new Node(nodes, n.getIp(), n.getPort(), 
                    server.getServerIp(), server.getServerPortNumber(), manager);
            startedNodes.add(node);
            new Thread(node).start();
        });
        
        
    }
    
    private static void loading() {
        long s = new Date().getTime();
        String msg = "\u001B[32mStarting server";
        while((new Date().getTime() - s) < 4000) {
            System.out.print(msg);
            try {
                Thread.sleep(1000);
                System.out.print("\b");
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
            if (msg.contains("...")) msg = "\u001B[32mStarting server";
            else msg+=".";
        }
        
        System.out.println("");
    }
    
}
