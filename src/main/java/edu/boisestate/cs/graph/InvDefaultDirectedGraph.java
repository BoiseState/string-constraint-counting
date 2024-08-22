package edu.boisestate.cs.graph;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;
import java.util.Map.Entry;

public class InvDefaultDirectedGraph extends DefaultDirectedGraph<PrintConstraint, SymbolicEdge> {

    private Map<PrintConstraint, Set<PrintConstraint>> predDepend;
    private Map<Integer, Set<Integer>> predDependID;
    private ArrayList<PrintConstraint> necessaryPredicates = new ArrayList<>();
    private HashSet<PrintConstraint> sources = new HashSet<>();
    private Map<Integer, Set<Integer>> symDepend = new HashMap<Integer, Set<Integer>>(); // map of predicates and the symbolics in their ancestors...
    private Map<Integer, Set<Integer>> dependSym = new HashMap<Integer, Set<Integer>>(); // map of symbolics and the predicates that depend on them...

    public InvDefaultDirectedGraph(Class<? extends SymbolicEdge> edgeClass) {
        super(edgeClass);
        predDepend = new HashMap<PrintConstraint, Set<PrintConstraint>>();
        predDependID = new HashMap<Integer, Set<Integer>>();
    }

    public void computePredicateDependencies() {
        //get all the sinks that are predicates and create map entry for them
        //get all the symbolic sources
        for (PrintConstraint c : vertexSet()) {
            if (this.outDegreeOf(c) == 0 && (c.getActualVal().equals("false") || c.getActualVal().equals("true"))) {
                HashSet<PrintConstraint> dependSet = new HashSet<PrintConstraint>();
                HashSet<Integer> dependSetID = new HashSet<Integer>();
                dependSet.add(c);
                dependSetID.add(c.getId());
                predDepend.put(c, dependSet);
                predDependID.put(c.getId(), dependSetID);
            }
        }

        //get all the symbolic sources

        for (PrintConstraint s : vertexSet()) {
            // TODO: this will sometimes not identify the symbolic inputs
            if (this.inDegreeOf(s) == 0 && (s.getSplitValue().startsWith("r") || s.getSplitValue().startsWith("$r"))) {
                sources.add(s);
                //System.out.println(s.getSplitValue());
            }
        }

        //System.out.println(sources);

        //intermediate map that remember symbolic sources for each predicate
        Map<PrintConstraint, Set<PrintConstraint>> symbValPred = new HashMap<PrintConstraint, Set<PrintConstraint>>();
        //DFS for each sink
        for (PrintConstraint s : sources) {
//			System.out.println(s.getValue());
//			System.out.print("\t");
            Set<PrintConstraint> reachedPred = new HashSet<PrintConstraint>();
            Set<Integer> reachedPredID = new HashSet<Integer>();
            DepthFirstIterator<PrintConstraint, SymbolicEdge> dfi = new DepthFirstIterator<PrintConstraint, SymbolicEdge>(this, s);
            while (dfi.hasNext()) {
                PrintConstraint n = dfi.next();
                if (this.outDegreeOf(n) == 0 && (n.getActualVal().equals("false") || n.getActualVal().equals("true"))) {
//					System.out.print(n.getId() + " ");
                    reachedPred.add(n);
                    reachedPredID.add(n.getId());
                }
            }
//			System.out.println();
            //iterate over predicates and add them to each other
            for (PrintConstraint p : reachedPred) {
                predDepend.get(p).addAll(reachedPred);
            }

            for (Integer p : reachedPredID) {
                predDependID.get(p).addAll(reachedPredID);
            }

        }

        findNecessaryPredicates();
        makeSymDepend();
        makeDependSym();

//		//resulting map
//		for(Entry<PrintConstraint, Set<PrintConstraint>> e : predDepend.entrySet()) {
//			//System.out.println(e);
//			System.out.println(e.getKey().getId());
//			System.out.print("\t");
//			for(PrintConstraint p : e.getValue()) {
//				System.out.print(p.getId() + " ");
//			}
//			System.out.println();
//		}
    }

    public Set<Integer> getDependedPredicates(Integer id) {
        Set<Integer> ret = new HashSet<Integer>();
        ret.addAll(predDependID.get(id));

        return ret;
    }

    // note that this includes itself
    public Set<Integer> getAncestors(PrintConstraint start) {
        Set<Integer> ret = new HashSet<Integer>();
        EdgeReversedGraph<PrintConstraint, SymbolicEdge> reversedGraph = new EdgeReversedGraph<PrintConstraint, SymbolicEdge>(this);
        BreadthFirstIterator<PrintConstraint, SymbolicEdge> breadthFirstIterator =
                new BreadthFirstIterator<PrintConstraint, SymbolicEdge>(reversedGraph, start);
        while (breadthFirstIterator.hasNext()) {
            ret.add(breadthFirstIterator.next().getId());
        }

        return ret;
    }

    public Set<Integer> getChildren(PrintConstraint start) {
        Set<Integer> ret = new HashSet<Integer>();
        BreadthFirstIterator<PrintConstraint, SymbolicEdge> breadthFirstIterator =
                new BreadthFirstIterator<PrintConstraint, SymbolicEdge>(this, start);
        while (breadthFirstIterator.hasNext()) {
            ret.add(breadthFirstIterator.next().getId());
        }
        return ret;
    }

    public PrintConstraint getConstraint(Integer id) {
        for (PrintConstraint c : vertexSet()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Set<PrintConstraint> getPredicates() {
        return predDepend.keySet();
    }

    public Set<Integer> getPredicatesID() {
        return predDependID.keySet();
    }

    public Integer getNumSymInputs() {
        return sources.size();
    }

    public ArrayList<PrintConstraint> getNecessaryPredicates() {
        return necessaryPredicates;
    }

    public void findNecessaryPredicates() {
        // create priority queue structure for topological iteration
        Queue<PrintConstraint> queue = new PriorityQueue<>(1, new PrintConstraintComparator());

        // create topological iterator for graph
        TopologicalOrderIterator<PrintConstraint, SymbolicEdge> iterator = new TopologicalOrderIterator<>(this, queue);

        HashMap<Integer, Set<Integer>> copyPredDepends = new HashMap<Integer, Set<Integer>>();
        for (Entry<Integer, Set<Integer>> e : predDependID.entrySet()) {
            copyPredDepends.put(e.getKey(), new HashSet<Integer>(e.getValue()));
        }

        // iterate over graph in topological order to find predicates that will need to be processed
        while (iterator.hasNext()) {
            PrintConstraint current = iterator.next();
            int currentID = current.getId();
            if (copyPredDepends.containsKey(currentID)) {
                // if not just itself, remove itself from all predicates that depend on it
                if (copyPredDepends.get(currentID).size() == 1) {
                    necessaryPredicates.add(current);
                }
                Set<Integer> removals = new HashSet<Integer>(copyPredDepends.get(currentID));
                copyPredDepends.remove(currentID);
                for (Integer pred : removals) {
                    if (copyPredDepends.containsKey(pred)) copyPredDepends.get(pred).remove(currentID);
                }

            }
        }

        // nps - 8.13.24
        // sometimes in real graphs there are no symbolics for certain predicates so just remove those
        // probably a better way of doing this...
        ArrayList<PrintConstraint> ret = new ArrayList<>();
        for (PrintConstraint c : necessaryPredicates) {
            if (hasSymbolicAncestor(c)) {
                ret.add(c);
            }
        }

        necessaryPredicates = ret;
    }

    public boolean hasSymbolicAncestor(PrintConstraint c) {
        for (int ancestor : getAncestors(c)) {
            if (sources.contains(getConstraint(ancestor))) {
                return true;
            }
        }
        return false;
    }

    // get each predicate and the symbolics that it has as ancestors
    private void makeSymDepend() {
        for (PrintConstraint p : predDepend.keySet()) {
            Set<Integer> symSet = new HashSet<Integer>();
            for (int a : getAncestors(p)) {
                if (sources.contains(getConstraint(a))) {
                    symSet.add(a);
                }
            }
            symDepend.put(p.getId(), symSet);
        }
    }

    private void makeDependSym() {
        for (PrintConstraint s : sources) {
            dependSym.put(s.getId(), new HashSet<>());
        }
        for (Entry<Integer, Set<Integer>> e : symDepend.entrySet()) {
            for (Integer sym : e.getValue()) {
                dependSym.get(sym).add(e.getKey());
            }

        }
    }

}
