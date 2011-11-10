/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.treewidth;

import org.jgrapht.Graph;
import com.treewidth.util.Permute;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author srikanthraju
 */
public class TreeWidth {
    SimpleGraph<Integer, DefaultEdge> graph;
    
    public TreeWidth( SimpleGraph<Integer, DefaultEdge> graph )
    {
        this.graph = graph;
    }
    public int find()
    {
        int tw = 100; long i = 0;
        Permute permuter = new Permute( graph.vertexSet().toArray() );
        while( permuter.hasNext() )
        {
            if( i % 100 == 0 )
            System.out.println( i );
            Object[] permutation = (Object[])permuter.next();
            int k = findmaxQ( permutation );
            if( k < tw )
                tw = k;
            i++;
        }
        return tw;
    }
    
    private int findmaxQ( Object[] vertices )
    {
        int maxq = 0;

        for( int i = 0; i < vertices.length; i++)
        {
            int v = (Integer)vertices[i];
            int maxconnect = vertices.length-i;
            
            int Q = 0;
            //Find Q
            if(maxconnect > maxq) {
                
            for( int j = i + 1; j < vertices.length; j++ )
            {
                int w = (Integer)vertices[j];
                SimpleGraph check = (SimpleGraph)graph.clone();
                for( int tmp = i + 1; tmp < vertices.length; tmp ++ )
                {
                    if( (Integer)vertices[tmp] != w )
                        check.removeVertex( (Integer)vertices[tmp] );
                }
                if( DijkstraShortestPath.findPathBetween(check, v, w) != null )
                    Q++;

            }
            if( Q > maxq)
                maxq = Q;           
            }
        }
        return maxq;
    }
}
