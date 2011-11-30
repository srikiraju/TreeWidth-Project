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
import java.util.Arrays;
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
    int upper_bound = 11;
    int upper_bound_hit = 0;

    
    public TreeWidth( SimpleGraph<Integer, DefaultEdge> graph )
    {
        this.graph = graph;
    }
    
    /*
    public int findTreeWidthByRecursion(Set<Integer> L, Set<Integer> S) {

        System.out.println("Recursing again..");
        System.out.println("Printing L and S");
        System.out.println("L:	" + L.toString());
        System.out.println("S:	" + S.toString());
        System.out.println("Size of S:	" + S.size());

        if (S.size() <= 1) {
            if (S.size() == 0) {
                return 0;
            }
            Set<Integer> tempSet = new HashSet<Integer>();
            tempSet.addAll(L);
            tempSet.addAll(S);

            Set<Integer> vertexSet = graph.vertexSet();

            SimpleGraph check = (SimpleGraph) graph.clone();
            int Q = 0;
            for (Integer vert : vertexSet) {
                for (Integer tmp : tempSet) {
                    if (vert != tmp) {
                        check.removeVertex(vert);
                    }
                }

            }
            for (Integer i : L) {
                if (DijkstraShortestPath.findPathBetween(check, i, S.toArray()[0]) != null) {
                    Q++;
                }
            }
            return Q;
        }

        // Set Opt to infinity
        int Opt = 100000;
        int initialDivide = S.size() / 2;
        System.out.println("Size of division:	" + initialDivide);
        // Generate all the combinations of size initialDivide from the graph's vertex set
        Combinations combs_gen = new Combinations(S.size(), initialDivide);

        // Iterate through them and find recursive tree width
        while (combs_gen.hasNext()) {
            if (combs_gen.getNumLeft().mod(new BigInteger("1000")).equals(BigInteger.ZERO)) {
                System.out.println(combs_gen.getNumLeft());
            }
            int a[] = (int[]) combs_gen.next();
            System.out.println("Find below the current S'");
            System.out.print("[");
            for (int s1 : a) {
                System.out.print((s1 + 1) + " ");
            }
            System.out.println("]");
            System.out.println();
            // Put a in a set
            Set<Integer> Sdash = new LinkedHashSet();
            for (int a1 : a) {
                Sdash.add((Integer) (a1 + 1));
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
    */
    private int min(int opt, int max) {
        return (opt < max ? opt : max);
    }

    private int max(int v1, int v2) {
        return (v1 > v2 ? v1 : v2);
    }


    public int findDynamic()
    {
        
        HashMap<BitSet, Integer> processed = new HashMap<BitSet, Integer>();

        nindex = new NeighborIndex( graph );
        for( int i = 1; i <= graph.vertexSet().size(); i++ )
        {
            HashMap<BitSet, Integer> processed_now = new HashMap<BitSet, Integer>();
            Combinations combs_gen = new Combinations(graph.vertexSet().size(), i);
                        System.out.println( "Processing combs of size:" + i +":" + combs_gen.getTotal() );

            while( combs_gen.hasNext() )
            {
                if( combs_gen.getNumLeft().mod(new BigInteger("100000")).equals(BigInteger.ZERO) )
                    System.out.println( combs_gen.getNumLeft() );
                int a[] = (int[])combs_gen.next();
                //Create graph containing these vertices
                BitSet S = new BitSet( graph.vertexSet().size() + 1 );
                for( int tmp : a )
                {
                    S.set( tmp + 1 );
                }
                
                HashMap<Integer, Integer> qs = new HashMap<Integer, Integer>();
                findQEvenBetter( S, qs );
                
                int tw = 1000;
                //For all vertices in our checklist
                for( int v = S.nextSetBit(1); v >= 0; v = S.nextSetBit( v + 1 ) )
                {
                    BitSet Sminusv = (BitSet)S.clone();
                    Sminusv.clear( v );
                    //Find TW(S-v)
                    Integer tw1 = processed.get(Sminusv);
                    if( tw1 == null && Sminusv.cardinality() > 1 ) //eliminate this subset has tw > upperbound
                    {
                        upper_bound_hit ++;
                        continue;
                    }
                    //Find Q(S-v,v)
                    //Integer tw2 = findQBetter( Sminusv, (Integer)v );
                    Integer tw2 = (Integer)qs.get(v);//TW( S - v )

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
                if( tw < upper_bound )
                    processed_now.put( S, tw );
            }
            processed = processed_now;
        }
        System.out.println( upper_bound_hit );
        BitSet ret = new BitSet( graph.vertexSet().size() + 1);
        ret.set(1, graph.vertexSet().size() + 1 );
        return processed.get( ret ) == null ? upper_bound : (int)processed.get( ret );
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
            /**
             * Including following code since ClassCastExceptions
             * were prevalent (in my impl) in casting between Integer
             * and Object back and forth. So I decided  to pass Integer[]
             * vertices itself. 
             *
             */
            Integer[] intperm = new Integer[permutation.length];
            int c = 0;
            for(Object o:permutation) {
                intperm[c] = (Integer)o;
            	c++;
            }	
            int kbyRec = findMaxQByRecursion( intperm );    // Note: The method still returns k and not 
                                                            // kbyRec. If you would like to test, change k to
                                                            // kbyRec. 
            int k = findmaxQ( permutation );
            if( k < tw )
                tw = k;
            i++;
        }
        return tw;
    }
    /*
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
    */
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
        BitSet visited = new BitSet( graph.vertexSet().size() + 1 );
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
    
    private int findMaxQByRecursion(Integer[] vertices) {
        int Q = 0;
        Integer[] L = new Integer[0];
    	int q = findTreeWidthByRec(L,vertices, Q);
    	System.out.println("Value of q after Recursion:	" + q);
    	int maxq = 0;
    	
        if( q > maxq ) {
            maxq = Q;
            System.out.println("Value of maxQ:	" + maxq);
        }
        return q;
    }
    
    // Recursive Algorithm - Let me know if tree width is not ok. I dint test 
    // on Suresh's graphs
    private int findTreeWidthByRec(Integer[] leftToVertices, Integer[] vertices, int Q) {
    	
		Integer[] L = leftToVertices;
		Integer[] S = vertices;
		if(S.length == 1) {
			Set<Integer> tempSet = new LinkedHashSet<Integer>();
			List<Integer> Llist = Arrays.asList(L);
			tempSet.addAll(Llist);
			List<Integer> Slist1 = Arrays.asList(S);
			tempSet.addAll(Slist1);
			Set vertexSet = graph.vertexSet();
			SimpleGraph check = (SimpleGraph)graph.clone();
			for( Object vert : vertexSet )
	        {
				if(!tempSet.contains((Integer)vert))
					check.removeVertex(vert);
			}
//			System.out.println("Printing graph check again:	" + check.vertexSet().toString());
//			System.out.println();
			for(Integer i: L) {
				if(DijkstraShortestPath.findPathBetween(check, (i), (S[0])) != null) {
					Q++;
				}
			}
			System.out.println("The value Q is(size of S = 1):	" + Q);
			return Q;
		}
		
		int inputSize = S.length/2;
		int countToInputSize = 0;
		Integer[] storeS = new Integer[inputSize];
		Integer[] storeL = new Integer[L.length];
		for(int i = 0; i < S.length; i++) {
			if(countToInputSize == inputSize) {
				if(i - countToInputSize - 1 >= 0) {
					int k = i - countToInputSize;
					int j = 0;
					while(k >= 0) {
						storeL[j] = new Integer((Integer) L[k]);
						k--;
						j++;
					}
				}
				countToInputSize = 0;
				
				Integer[] SminusSdash = new Integer[S.length-storeS.length];
				Integer[] LplusSdash = new Integer[L.length+storeS.length];
				
				int ctr = 0;
				ArrayList store = new ArrayList();
				for(int i1 = 0; i1 < storeS.length; i1++) {
					for(int j1 = 0; j1 < S.length; j1++) {
						if(S[j1] == storeS[i1]) {
//							System.out.print(S[j1] + " ");
							store.add(j1);
							ctr++;
							break;
						}
					}
				}
//				System.out.println();
				int sctr = 0;
				for(int j1 = 0;j1 < S.length; j1++) {
					if(!store.contains(j1)) {
						SminusSdash[sctr] = S[j1];
//						System.out.print(SminusSdash[sctr] +" ");
						sctr++;
					}
				}
//				System.out.println();
				
				for(int tmp = 0; tmp < storeL.length;tmp++)
					LplusSdash[tmp] =  storeL[tmp];
				int storeLlen = storeL.length;
				for(int tmp = 0; tmp < storeS.length; tmp++)
					LplusSdash[storeLlen+tmp] = storeS[tmp]; 
				
				int tw1 = findTreeWidthByRec(L, storeS, Q);
				int tw2 = findTreeWidthByRec(LplusSdash, SminusSdash, Q);
				Q = max(tw1, tw2);
				System.out.println("Value of Q(S.size > 1):	" + Q);
			}
			else {
				storeS[countToInputSize] = (Integer) S[i];
				countToInputSize++;
			}
		}
		return Q;
	}    

    //Finding Q all at once, for all vertices
    private void findQEvenBetter(BitSet S, HashMap<Integer, Integer> qs)
    {
        BitSet visited = new BitSet( graph.vertexSet().size() + 1 );
        for( int v = S.nextSetBit(1); v >= 0; v = S.nextSetBit( v + 1 ) )
        {
            if( visited.get( v ) )
                continue;
            int Q = 0;
            visited.set( v );

            //All the vertices visited now are in the same connected
            //component and therefore have the same Q
            BitSet cc = new BitSet( graph.vertexSet().size() + 1 );
            BitSet notwithin_s = new BitSet( graph.vertexSet().size() + 1);
            cc.set( v );
            LinkedList queue = new LinkedList();
            for( Object a : nindex.neighborsOf((Integer)v) )
            {
                queue.add(a);
            }

            while( !queue.isEmpty() )
            {
                Integer consider = (Integer)queue.removeFirst();
                if( visited.get(consider) )
                    continue;
                visited.set(consider);
                cc.set(consider);
                if( S.get(consider) )
                {
                    for( Object a : nindex.neighborsOf(consider) )
                    {
                        if( !visited.get((Integer)a) )
                            queue.add(a);
                    }
                }
                else
                {
                    Q++;
                    notwithin_s.set( consider );
                }
            }

            visited.andNot(notwithin_s);
            cc.andNot(notwithin_s);
            for (int i = cc.nextSetBit(1); i >= 0; i = cc.nextSetBit(i+1)) {
                qs.put(new Integer(i), new Integer(Q));
            }
            


        }
    }
}
