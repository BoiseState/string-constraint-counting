package edu.boisestate.cs;

import edu.boisestate.cs.graph.InvDefaultDirectedGraph;
import java.io.Serializable;

public class AStrBenchmarkBundle implements Serializable {
    // This is the ID you asked about! Explanation below.
    private static final long serialVersionUID = 1L; 
    
    public InvDefaultDirectedGraph graph;
    public String alphabetString;
    public int bound;

    public AStrBenchmarkBundle(InvDefaultDirectedGraph graph, String alphabetString, int bound) {
        this.graph = graph;
        this.alphabetString = alphabetString;
        this.bound = bound;
    }
}
