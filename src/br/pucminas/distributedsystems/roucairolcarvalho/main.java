package br.pucminas.distributedsystems.roucairolcarvalho;

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
        
        Node n1 = new Node(10000);
        Node n2 = new Node(10001);
        Node n3 = new Node(10002);
        
        n1.send("Teste 2");
    }
    
}
