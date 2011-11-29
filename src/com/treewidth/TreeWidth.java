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
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author srikanthraju
 */
public class TreeWidth {
    SimpleGraph<Integer, DefaultEdge> graph;
    NeighborIndex nindex;
    int upper_bound = 6;
    int upper_bound_hit = 0;
    
    public TreeWidth( SimpleGraph<Integer, DefaultEdge> graph )
    {
        this.graph = graph;
    }
    
        public int findTreeWidthByRecursion(Set<Integer> L, Set<Integer> S) {
        
    	System.out.println("Recursing again..");
    	System.out.println("Printing L and S");
    	System.out.println("L:	" + L.toString());
    	System.out.println("S:	" + S.toString());
    	System.out.println("Size of S:	" + S.size());
    	
    	if(S.size() == 1) {
    		Set<Integer> tempSet = new HashSet<Integer>();
    		tempSet.addAll(L);
    		tempSet.addAll(S);
    		
    		Set<Integer> vertexSet = graph.vertexSet();
    		
    		SimpleGraph check = (SimpleGraph)graph.clone();
    		int Q = 0;
    		for( Integer vert : vertexSet )
            {
    			for(Integer tmp: tempSet) {
    				if(vert != tmp) {
    					check.removeVertex(vert);
    				}
    			}
            }
    		for(Integer i: L) {
    			if(DijkstraShortestPath.findPathBetween(check, i, S.toArray()[0]) != null) {
    				Q++;
    			}
    		}
    		return Q;
    	}

    	// Set Opt to infinity
    	int Opt = 100000;
    	int initialDivide = S.size()/2;
    	System.out.println("Size of division:	" + initialDivide);
    	// Generate all the combinations of size initialDivide from the graph's vertex set
    	Combinations combs_gen = new Combinations(graph.vertexSet().size(), initialDivide);
        // Iterate through them and find recursive tree width
    	while( combs_gen.hasNext() )
        {
    	    if( combs_gen.getNumLeft().mod(new BigInteger("1000")).equals(BigInteger.ZERO) )
                System.out.println( combs_gen.getNumLeft() );
            int a[] = (int[])combs_gen.next();
            System.out.println("Find below the current S'");
            System.out.print("[");
            for(int s1: a) {
            	System.out.print((s1+1) + " ");
            }
            System.out.println("]");
            System.out.println();
            // Put a in a set
            Set<Integer> Sdash = new LinkedHashSet();
            for(int a1: a) {
            	Sdash.add((Integer)(a1+1));
            }
          System.out.println("Sanity Check - Printing Sdash");
          System.out.println(Sdash.toString());
          System.out.println("Printing L");
          System.out.println(L.toString());
            
          // Prepare L U Sdash
            Set<Integer> LUnionSdash = new LinkedHashSet<Integer>();
            LUnionSdash.addAll(L);
            LUnionSdash.addAll(Sdash);
            // Sanity Check. Printing LUnionSdash
            System.out.println("Printing LUnionSdash:	" + LUnionSdash.toString());
            // Prepare S-Sdash
            Set<Integer> SminusSdash = new LinkedHashSet<Integer>();
//            Set<Integer> Sclone = new HashSet<Integer>();
//            for(Integer in: S)
//            	Sclone.add(in);
//            
//          Sclone.removeAll(Sdash);
//          SminusSdash.addAll(Sclone);
            S.removeAll(Sdash);
            SminusSdash.addAll(S);
            // Sanity Check. Printing SminusSdash
            System.out.println("Printing SminusSdash:	" + SminusSdash.toString());
            // Check previous statement.. in order ??
           
            int v1 = findTreeWidthByRecursion(L, Sdash);
            int v2 = findTreeWidthByRecursion(LUnionSdash, SminusSdash);
            Opt = min(Opt, max(v1, v2));
        }
    	return Opt;
    }
    private int min(int opt, int max) {
    	return (opt < max ? opt:max);
	}
	private int max(int v1, int v2) {
    	return (v1 > v2 ? v1:v2);
	}

    
    
    public int findDynamic()
    {
        HashMap<HashSet, Integer> processed = new HashMap<HashSet, Integer>();
        nindex = new NeighborIndex( graph );
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
                {
                    vertices_to_remove.remove( ( Integer )(tmp+1) );
                }
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
                    Integer tw2 = findQBetter( Sminusv, (Integer)v );
                    
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
        System.out.println( upper_bound_hit );
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
    
    public int findRec() {
        
        Set<Integer> L = new HashSet<Integer>();
        SimpleGraph S = (SimpleGraph) graph.clone();
        // To start with, L is the empty set
        Set<Integer> S1 = S.vertexSet();
        Set<Integer> Sclone = new HashSet<Integer>();
        for(Integer in: S1)
            Sclone.add(in);
        
        int twByRec = findTreeWidthByRecursion(L, Sclone);
        System.out.println(twByRec);
        return twByRec;
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
            if( Q >= upper_bound )
            {
                break;
            }

        }
        return Q;
    }
    
    private int findQBetter(Set S, Integer v)
    {
        int Q = 0;
        BitSet visited = new BitSet( graph.vertexSet().size() );
        visited.set( v );
        LinkedList queue = new LinkedList();
        for( Object a : nindex.neighborsOf(v) )
        {
            queue.add(a);
        }
        
        while( !queue.isEmpty() )
        {
            Integer consider = (Integer)queue.removeFirst();
            if( visited.get(consider) )
                continue;
            visited.set(consider);
            if( S.contains(consider) )
            {
                for( Object a : nindex.neighborsOf(consider) )
                {
                    if( !visited.get((Integer)a) )
                        queue.add(a);
                }
            }
            else
                Q++;
        }
        return Q;
    }
}
