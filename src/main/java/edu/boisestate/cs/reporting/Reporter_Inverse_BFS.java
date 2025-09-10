package edu.boisestate.cs.reporting;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;
import edu.boisestate.cs.graph.*;
import org.jgrapht.DirectedGraph;

import edu.boisestate.cs.Parser_2;
import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

public class Reporter_Inverse_BFS<T extends A_Model_Inverse<T>> extends Reporter_Inverse<T> {
    //this class should also remember all previous constraints, it might be in
    //allConstraints

    //private BufferedWriter out;
    private SolutionSet<T> solutions;

    public Reporter_Inverse_BFS(DirectedGraph<PrintConstraint, SymbolicEdge> graph, Parser_2<T> parser,
                                Solver_Inverse<T> invSolver, boolean debug) {
        super(graph, parser, invSolver, debug);
        this.solutions = new SolutionSet<T>(((InvDefaultDirectedGraph) graph).getNumSymInputs());
//		// TODO Auto-generated constructor stub
//		try {
//			out = new BufferedWriter(new FileWriter("./temp/solutions.txt"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

    @Override
    protected void solveInputs() {
        //from Marlin's code
        // output finalized inverse constraints for debug

        printDebug(cid);
        printDebug(cid + "Inverse Constraint Set:");
        for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
            printDebug(cid + c.toString() + "\t" + allConstraints.get(c.getID()).toString());
        }
        printDebug(cid);


        long startTime = System.nanoTime();

        // initialize our copy of the symbolic string map as it is right now
        invSolver.initStringMap();
        //end from Marlin's code


        printDebug("Solving using BFS");
        //create a queue of all dependent predicates
        // get covering set of predicates that will process all nodes and inputs
        ArrayList<PrintConstraint> toProcess = ((InvDefaultDirectedGraph) graph).getNecessaryPredicates();
        for (PrintConstraint c : toProcess) {
            predicateIDs.add(c.getId());
        }

        printDebug(predicateIDs.toString());
        InvDefaultDirectedGraph eGraph = (InvDefaultDirectedGraph) graph;

        TreeSet<Integer> qID = new TreeSet<Integer>();
        //predicateIDs have the last predicate is the current constraint predicate
        //and it contains all predicates solved so far
//		qID.addAll(predicateIDs);
        for (int id : predicateIDs) {
            qID.addAll(eGraph.getDependedPredicates(id));
        }
        qID.addAll(eGraph.getPredicatesID());
//		qID.addAll((eGraph.getDependedPredicates(predicateIDs.get(predicateIDs.size()-1))));
        //sort it so the predicate with the largest ids processed first
        ArrayList<Integer> qIDL = new ArrayList<Integer>(qID);
        Collections.sort(qIDL, Collections.reverseOrder());
        qID = new TreeSet<Integer>(qIDL);
        printDebug("Q " + qID);
        printDebug(predicateIDs.toString());
        List<I_Inv_Constraint<T>> q = new ArrayList<I_Inv_Constraint<T>>();
        Set<Integer> actual = new HashSet<Integer>();
        for (Integer val : qID) {
            q.add(allInverseConstraints.get(val));
            //remove nodes that have not been processed as predicates
            actual.addAll(eGraph.getAncestors(allConstraints.get(val)));
            actual.add(val);
        }
        boolean found = false;
        for (PrintConstraint p : eGraph.getPredicates()) {
            found = false;
            for (int id : qID) {
                if (p.getId() == id) {
                    found = true;
                    continue;
                }
            }
            if (!found) {
                System.out.println("predicate  not in qID " + p.getId());
            }
        }
        printDebug("actuall " + actual);
        //iterate over all actual nodes and remove them from the parents
        //those nodes that are not there
        for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
            printDebug(c + "1 " + c.getPrevID());
            c.getPrevID().retainAll(actual);
            c.update();
            printDebug(c + "2 " + c.getPrevID());
        }


        //backtrack map - records the current current queue for the backtracked id
        Map<Integer, TreeSet<Integer>> backtrackMap = new HashMap<Integer, TreeSet<Integer>>();

        //processed elements -- it an acyclic graph, so we just need those for efficiency
        List<Integer> processedID = new ArrayList<Integer>();

        //now q contains depended predicates
        while (!qID.isEmpty()) {
            int currID = qID.last();
            qID.remove(currID);
            processedID.add(currID);
            I_Inv_Constraint<T> curr = allInverseConstraints.get(currID);
            printDebug("node: " + curr);
            printDebug("parents are: " + curr.getPrevID());
            //1st true - continue, false - backtrack
            //2nd true - don't add to the backtrack map, false do
            Tuple<Boolean, Boolean> result = curr.evaluate();
            printDebug("result " + result);
            if (result.get1()) {
                //continue by extending the queue
                if (curr.getNextID() != -1 && !qID.contains(curr.getNextID())) {
                    qID.add(curr.getNextID());
                }
                if (curr.getArgID() != -1 && !qID.contains(curr.getArgID())) {
                    qID.add(curr.getArgID());
                }
                if (!result.get2()) {
                    printDebug("ADDING TO BACKTRACK MAP " + curr.getID());
                    //add to the backtrack queue
                    TreeSet<Integer> backtrackQ = new TreeSet<Integer>();
                    //just for debuging -- check to make sure there is no
                    //curr.getID in the backtracking map
                    if (backtrackMap.containsKey(curr.getID())) {
                        printDebug("ERRROR: adding the same id to the backtrack map " + curr.getID());
                    } else {
                        backtrackQ.add(curr.getID());
                        backtrackQ.addAll(qID);
                        backtrackMap.put(curr.getID(), backtrackQ);
                    }
                }
            } else {// do not continue and figure out how to backtrack
                // inv_insert is set up for greedy takes and may backtrack immediately to itself:
                if (!result.get2()) {
                    qID.add(curr.getID());
                } else {
                    //find the "closest" node to backtrack to curr
                    //in our case one that has smallest ID since
                    //the numbering go up as we go closer to the
                    //predicates
                    //eas: need to use lambda-expression here
                    printDebug("BACKTRAKING " + backtrackMap);
                    Integer backtrackID = Integer.MAX_VALUE; //all our nodes have positive ids
                    for (Integer ids : backtrackMap.keySet()) {
                        if (backtrackID > ids) {
                            // only backtrack to most recent & relevant node
                            // and want to ensure the node being backtracked to isn't fully spent, i.e. its inputs/output option have been exhausted
                            if (eGraph.getChildren(allConstraints.get(currID)).contains(ids)) { // in parents (inverse)
                                // only inv concats have input/output options
//							if (allInverseConstraints.get(ids).getOp() == Operation.CONCAT_SYM) {
                                // only backtrack to a concat if it has more options else find another node to backtrack to
//								if (!((InvConstraintConcatSym<T>) allInverseConstraints.get(ids)).inputsEmpty()) {
//									backtrackID = ids;
//								}
//							} else {
                                backtrackID = ids;
//							}
                            }
                        }
                    }
                    // nps - 8.21.24 - most recently processed backtrackable node may not contain a node that is relevant.
                    // e.g. the parents of teh node with the issue aren't include in the backtrack map
                    // nps - 8.27.24 if we want to only backtrack to nodes that are in parents we need to adjust the clearing
                    // and continuing beloew. we would want to jump back and reprocess the node we backtrack to but then jump
                    // ahead again to whichever node caused the issue... except that wouldn't account for potential effects on
                    // those later nodes, but maybe we could just check for consistency? certainly could just clear back to relevant node

                    // nat - 9.2.24 - as part of what mentioned below. we only need to backtrack to the path/lineage for the conflict
                    //i.e. we don't need the full queue just backtrack to the parent with more options, then propogate that down to the conflict.
                    // but we need to check every constraint that is a descendant of the backtrack node, and cler/reprocess those.

                    printDebug("backtrackID " + backtrackID);
                    //case when nothing to backtrack to
                    if (backtrackID != Integer.MAX_VALUE) {
                        //get the queue
                        TreeSet<Integer> newQ = backtrackMap.remove(backtrackID);


                        // nps - 8.27.24 - i imagine this could all be handled with processedID list as that shoudl store all
                        // evaluated nodes

                        // nat - 9.2.24 - so it'd be better to be able to not clear all nodes that were processed after the node
                        // we bactrack to. that is avoid as much as possble retracking. the issue is that the current qid contains
                        // either processed IDs and/or parents of them. We would need to keep the current id before backtrack.
                        // remove children of backtrackID from the current qid and merge it with backtrack id (curr after backtrack)
                        // that way we can safely not clear nodes, and only remove backrtack and its child from processedid.
                        //TODO: this ^ (insteadt of just redoing it all)
                        // just clear processed IDs that are descendants of the backtrackID
                        // don't need backtrackID queue at all? just add it to q and clear descendants from q and processedID

                        PrintConstraint backtrack = allConstraints.get(backtrackID);
                        Set<Integer> descendants = eGraph.getAncestors(backtrack);
                        Set<Integer> clearAdd = new HashSet<>();
                        // clear and remove descendants
                        for (int i = processedID.indexOf(backtrackID) + 1; i < processedID.size(); i++) { // check any nodes processed after backtrack node
                            if (descendants.contains(processedID.get(i))) { // get any constraints effected by this backtrack
                                clearAdd.add(processedID.get(i));
                            }
                        }
                        for (Integer id : clearAdd) { // add to queue, remove from processed, and clear
                            processedID.remove(id);
                            qID.add(id);
                            allInverseConstraints.get(id).clear();
                        }
                        qID.add(backtrackID);
                        processedID.remove(backtrackID);
                    }
                    else {
                        //nothing to backtrack to stop iterations
                        //need to try for the next length
                        printDebug("NOTHING TO BACKTRACK TO, UNSAT AT THIS LENGTH");
                        break;
                    }
                }
            }


        }

        //ending -- Marlin's code
        long endTime = System.nanoTime();

        long durationInNano = (endTime - startTime);

        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        //for (I_Inv_Constraint<T> i : allInverseConstraints.values())  {
        //those inputs that have been processed
        for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
            if (c.getOp() == Operation.INIT_SYM) {
                T solution = c.getSolution();
                if (solution == null || solution.isEmpty()) {
                    printDebug("INPUT SOLUTION SET INCONSISTENT: " + c.getID());
                } else {
                    solutions.add(c.getID(), solution);
                }
            }
        }
        printDebug(solutions.toString());
        printDebug("\nSOLUTION TIME FOR LAST BACKPROP ms: " + durationInMillis);

        //for (I_Inv_Constraint<T> i : allInverseConstraints.values()) {
        // debug branch so don't loop for now reason ....
//		if (debug) { // nps 8.19.24 consistency is now checked when solving an input constraint
//			printDebug("INPUT SOLUTIONS FOUND:");
//			for (int id : processedID) {
//				I_Inv_Constraint<T> i = allInverseConstraints.get(id);
//				if (i.getOp() == Operation.INIT_SYM) {
//					T solution = i.getSolution();
////				T example = i.output(0);//symbolic nodes hold their example in the output values
//
//					// populate map for output to file/SPF
////				if (inputSolution.get(i.getID()) != null) {
////					T prev = inputSolution.get(i.getID());
////					example = prev.intersect(example);
////                }
//					if (solution.isEmpty()) { // should never happen
//						printDebug("INPUT SOLUTION SET INCONSISTENT: " + i.getID());
//					}
////				inputSolution.put(i.getID(), example);
//
//					printDebug(i.getID() + ": " + solution.getShortestExampleString());
//
//				}
//			}
//		}

        /// nps - here the solutions are added to the actual solution set after a backpropagation occurs
//		for (int id : processedID) {
//			I_Inv_Constraint<T> i = allInverseConstraints.get(id);
//			if (i.getOp() == Operation.INIT_SYM) {
//				T solution = i.getSolution();
//				solutions.add(id, solution);
//			}
//		}

        System.out.println(solutions.getSolutions());

    }

    @Override
    public SolutionSet<T> getSolutionSet() {
        return solutions;
    }

}
