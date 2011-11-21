
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author srikanthraju
 */
import com.treewidth.TreeWidth;
import java.io.BufferedReader;
import java.util.Scanner;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

public class Main {
    public static void main(String[] args)
    {
        SimpleGraph<Integer, DefaultEdge> g = takeInput();

        System.out.println( "Input is done. Processing" );
        TreeWidth tw = new TreeWidth( g );
        long t1 = System.nanoTime();

        System.out.println( tw.findDynamic() );
        long t2 = System.nanoTime();
        System.out.println("Execution time: " + ((t2 - t1) * 1e-6) + " milliseconds");


        System.out.println( g.toString() );
    }
    
    
    public static SimpleGraph< Integer, DefaultEdge > takeInput()
    {
        Scanner s = new Scanner( System.in );
        int nvertices = s.nextInt();
        int nedges = s.nextInt();
        
        SimpleGraph<Integer, DefaultEdge> g =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        for( int i = 1; i <= nvertices; i++ )
            g.addVertex(i);

        while( s.hasNext())
        {
            String line = s.nextLine();
            if( line.length() == 0 )
                continue;
            if( line.matches("end") )
                break;
            Scanner t = new Scanner( line );
            int base_v = t.nextInt();
            while( t.hasNextInt() )
            {
                int v = t.nextInt();
                g.addEdge(base_v, v);
            }
        }
        return g;
    }
}
