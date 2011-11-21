/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.treewidth;

import com.treewidth.util.Combinations;
import org.jgrapht.Graph;
import com.treewidth.util.Permute;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    
    
    public int findDynamic()
    {
        HashMap<HashSet, Integer> processed = new HashMap<HashSet, Integer>();
        
        for( int i = 1; i <= graph.vertexSet().size(); i++ )
        {
            Combinations combs_gen = new Combinations(graph.vertexSet().size(), i);
                        System.out.println( "Processing combs of size:" + i +":" + combs_gen.getTotal() );

            while( combs_gen.hasNext() )
            {
                if( combs_gen.getNumLeft().mod(new BigInteger("1000")).equals(BigInteger.ZERO) )
                    System.out.println( combs_gen.getNumLeft() );
                int a[] = (int[])combs_gen.next();
                //Create graph containing these vertices
                SimpleGraph S = (SimpleGraph)graph.clone();
                HashSet vertices_to_remove = new HashSet( S.vertexSet() );
                for( int tmp : a )
                    vertices_to_remove.remove( ( Integer )(tmp+1) );
                S.removeAllVertices(vertices_to_remove);
                
                
                int tw = 1000;
                //For all vertices in our checklist
                for( Object v: S.vertexSet() )
                {
                    HashSet<Integer> Sminusv = new HashSet<Integer>( S.vertexSet() );
                    Sminusv.remove((Integer)v);
                    //Find TW(S-v)
                    Integer tw1 = processed.get(Sminusv);
                    //Find Q(S-v,v)
                    Integer tw2 = findQ( Sminusv, (Integer)v );
                    
                    if( tw1 != null && tw1 > tw2 )
                    {
                        if( tw1 < tw )
                            tw = tw1;
                    }
                    else
                    {
                        if( tw2 < tw )
                            tw = tw2;
                    }
                }
                processed.put( new HashSet(S.vertexSet()), tw);
            }
        }
        return (int)processed.get( new HashSet( graph.vertexSet() ) );
    }
    
    public int findBruteForce()
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
    
    private int findQ(Set S, Integer v)
    {
        if( S.size() == 1 )
            return 0;
        int Q = 0;
        HashSet<Integer> w_tocheck = new HashSet<Integer>( graph.vertexSet() );
        w_tocheck.removeAll(S);
        w_tocheck.remove(v);
        
        for( Integer w : w_tocheck )
        {
            SimpleGraph check = (SimpleGraph)graph.clone();
            for( Integer tmp : w_tocheck )
            {
                if( tmp != w )
                    check.removeVertex( tmp );
            }            
            if( DijkstraShortestPath.findPathBetween(check, v, w) != null )
                Q++;

        }
        return Q;
    }
}
