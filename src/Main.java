
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author srikanthraju
 */
import com.treewidth.TreeWidth;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

public class Main {
    public static void main(String[] args)
    {
        SimpleGraph<Integer, DefaultEdge> g =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        // add the vertices
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        g.addVertex(4);
        g.addVertex(5);

        //Im mukund yoyoyoyo!
        //lskdjflksdjflskjdflskdjflkdsf
        //Mukund is too cool~

        // add edges to create a circuit
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(4, 5);
        g.addEdge(5, 1);


        
        TreeWidth tw = new TreeWidth( g );
        System.out.println( tw.find() );

        System.out.println( g.toString() );
    }
}
