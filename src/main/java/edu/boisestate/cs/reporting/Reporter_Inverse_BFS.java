package edu.boisestate.cs.reporting;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;
import edu.boisestate.cs.graph.*;

import edu.boisestate.cs.Parser_2;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

public class Reporter_Inverse_BFS extends A_Reporter {
    //this class should also remember all previous constraints, it might be in
    //allConstraints

    protected final Solver_Inverse invSolver;
    protected Map<Integer, PrintConstraint> allConstraints = new HashMap<>();
    protected Map<Integer, I_Inv_Constraint> allInverseConstraints = new HashMap<>();
    protected List<Integer> predicateIDs = new ArrayList<>();

    // prefix for output when running inside SPF
    protected static String cid = "[IGEN] ";

    //private BufferedWriter out;
    private SolutionSet solutions;

    public Reporter_Inverse_BFS(InvDefaultDirectedGraph graph, Parser_2 parser,
                                Solver_Inverse invSolver, boolean debug) {
        super(graph, parser, invSolver, debug);    // solver instance variable in A_Reporter available
        this.invSolver = invSolver;                // same solver as inverse solver

        // this saves the set of constraints as references so that we can use access them later
        // while building the inverse constraints. There are some graphs that have inputs stored twice,
        // once with outgoing edges and once with no incoming/outgoing edges. The conditional avoids
        // adding references to the latter.
        for (PrintConstraint p : graph.vertexSet()) {
            if (!((graph.inDegreeOf(p) == 0) & (graph.outDegreeOf(p) == 0))) {
                allConstraints.put(p.getId(), p);
            }

        }
        this.solutions = new SolutionSet(graph.getNumSymInputs());
//		// TODO Auto-generated constructor stub
//		try {
//			out = new BufferedWriter(new FileWriter("./temp/solutions.txt"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

    /*
     * called when forward analysis reaches predicate, computes stats and inputs
     */
    @Override
    protected void calculateStats(PrintConstraint constraint) { // nps: constraint isn't actually used. backprop is all handled in BFS subclass

        // get constraint info as variables
//        Map<String, Integer> sourceMap = constraint.getSourceMap();
//        StringBuilder stats = new StringBuilder();
//        String actualVal = constraint.getActualVal();
//        int base = sourceMap.get("t");
//        long tTime, fTime, inMCTime, tMCTime, fMCTime = 0;

        // get id of second symbolic string if it exists
//        int arg = -1;
//        if (sourceMap.get("s1") != null) {
//            arg = sourceMap.get("s1");
//        }

        // initialize boolean flags
//        boolean isSingleton = false;
//        boolean trueSat = false;
//        boolean falseSat = false;

        // determine if symbolic strings are singletons
//        boolean argIsSingleton = false;
//        if(arg != -1) {
//        	argIsSingleton = solver.isSingleton(sourceMap.get("s1"));
//        }
//        if (solver.isSingleton(base, actualVal) &&
//            (sourceMap.get("s1") == null || argIsSingleton)) {
//            isSingleton = true;
//        }

//        long initialCount = this.invSolver.getModelCount(base);
//        inMCTime = BasicTimer.getRunTime();

        // store symbolic string values
//        solver.setLast(base, arg);

        // test if true branch is SAT
//        parser.assertBooleanConstraint(true, constraint);
//        tTime = BasicTimer.getRunTime();
//        if (solver.isSatisfiable(base)) {
//            trueSat = true;
//        }

//        long trueModelCount = this.invSolver.getModelCount(base);
//        tMCTime = BasicTimer.getRunTime();

        // revert symbolic string values
//        solver.revertLastPredicate();

        // store symbolic string values
//        solver.setLast(base, arg);

        // test if false branch is SAT
//        parser.assertBooleanConstraint(false, constraint);
//        fTime = BasicTimer.getRunTime();
//        if (solver.isSatisfiable(base)) {
//            falseSat = true;
//        }

//        long falseModelCount = this.invSolver.getModelCount(base);
//        fMCTime = BasicTimer.getRunTime();

        // revert symbolic string values
//        solver.revertLastPredicate();

        // if actual execution did not produce either true or false
//        if (!actualVal.equals("true") && !actualVal.equals("false")) {
//            System.err.println("warning constraint detected without true/false value");
//            return;
//        }

        // determine result of actual execution
//        boolean result = true;
//        if (actualVal.equals("false")) {
//            result = false;
//        }

        // branches disjoint?
//        parser.assertBooleanConstraint(result, constraint);


        // update accumulated timer for base
//        long prevTime = 0;
//        if (timerMap.containsKey(base)) {
//            prevTime = timerMap.get(base);
//        }
//        long lastTime = BasicTimer.getRunTime();
//        timerMap.put(base, lastTime + prevTime);

        // update accumulated timer for arg
//        prevTime = 0;
//        if (timerMap.containsKey(arg)) {
//            prevTime = timerMap.get(arg);
//        }
//        timerMap.put(arg, lastTime + prevTime);


        // store symbolic string values
//        solver.setLast(base, arg);

//        parser.assertBooleanConstraint(!result, constraint);

        // set yes or no for disjoint branches
//        String disjoint = "yes";
//        if (solver.isSatisfiable(base)) {
//            disjoint = "no";
//        }

        // set yes or no for disjoint branches
//        long overlap = this.invSolver.getModelCount(base);

        // revert symbolic string values
//        solver.revertLastPredicate();

        // get accumulated time
//        long accTime = 0;
//        if (timerMap.containsKey(base)) {
//            accTime = timerMap.get(base);
//        }

        // get constraint function name
//        String constName = constraint.getSplitValue().split("!!")[0];

        // add boolean operation to operation list
//        addBooleanOperation(base, arg, constName, constraint.getId(), argIsSingleton);

        // get operations
//        String[] opsArray = this.operationsMap.get(base);
//        String ops = joinStrings(Arrays.asList(opsArray), "\t -> \t");
//
//        // gather column data in list
//        List<String> columns = new ArrayList<>();
//        // id
//        columns.add(String.valueOf(constraint.getId()));
//        // actual value
//        columns.add(String.format("%s", constraint.getActualVal()));
//        // is singleton?
//        columns.add(String.valueOf(isSingleton));
//        // true sat?
//        columns.add(String.valueOf(trueSat));
//        // false sat?
//        columns.add(String.valueOf(falseSat));
//        // disjoint?
//        columns.add(String.format(disjoint));
//        // id of initial model
//        columns.add(String.valueOf(base));
//        // initial model count
////        columns.add(String.valueOf(initialCount));
//        // true model count
////        columns.add(String.valueOf(trueModelCount));
//        // false model count
////        columns.add(String.valueOf(falseModelCount));
//        // overlap count
////        columns.add(String.valueOf(overlap));
//        // previous operations
//        columns.add(ops);
//
//        // generate row string
//        String row = joinStrings(columns, "\t");
//
//        // output row
//        printDebug(cid + row);

        // --------------------------------------------------------------------------------------------
        // The process for solving the inputs needed to reach the current predicate location starts here.
        // --------------------------------------------------------------------------------------------

        // initialize our copy of the symbolic string map as it is right now
        //invSolver.initStringMap();
        //invSolver.initStringMapAccum();
        // we will rebuild all constraints, since this is a new path
//        allInverseConstraints.clear();

        // clear previous input solutions
        //inputSolutions.clear();

        // save this predicate ID so we can grab the new inverse constraint
        // from the allInverseConstraints container later
//        int predID = constraint.getId();

//        predicateIDs.add(predID);

        // this stops any backprop from happening until forward prop has finished
        // also currently only works on a necessary subset though the soundness should be confirmed
        // ------------------------ Traversal Optimization?
//		if (!toProcess.contains(constraint)){
////			printDebug("SKIPPING PROCESSING PREDICATE " + predID);
//			return;
//		}
        // ------------------------
//        processIt.remove();

        new ICGBuilder(graph, allConstraints, allInverseConstraints, invSolver, debug).build();
        // build the transposed graph of inverse constraints
//        if (build) {
////            buildICG_r3();
//            solutions = new SolutionSet<>(((InvDefaultDirectedGraph) graph).getNumSymInputs());
//            build = false; // only building once and keeping the inverse constaints from before, which may be wrong
//        }

        if (debug) {
            for (I_Inv_Constraint con : allInverseConstraints.values()) {
                con.setDebug(true);
            }
        }

        solveInputs();

        // output finalized inverse constraints for debug
//        if (true) {
//        	System.out.println(cid);
//        	System.out.println(cid + "Inverse Constraint Set:");
//        	for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
//        		System.out.println(cid + c.toString() + "\t" + allConstraints.get(c.getID()).toString());
//        	}
//        	System.out.println(cid);
//        }

        // get a reference to the predicate inverse constraint
        // I_Inv_Constraint<T> predicate = allInverseConstraints.get(predicateID);

        // ********************************
        // The call that starts it all ....
        //predicate.evaluate(null, 0);
        // ********************************

        // check for SAT here ...

        // TODO: consolidate solutions from inside sink nodes into a example set
        // then either output solutions or write them to a file
        // THIS CODE DOES NOT CURRENTLY DO ANYTHING ....

        // indicate if output going to file ..
//        if (solutionFile != "") {
//        	printDebug(cid + "Outputting to example file: " + solutionFile);
//
//        	// Code to output json example file here ...
//        	// A set of SPF inputs
//        	SPFInputSet SPFInputs = new SPFInputSet();
//
//        	// set this to reporter SAT ...
//        	SPFInputs.SAT = true;
//
//        	// for every solutionset, get possible strings, select one, add it to SPFInputSet
////        	for (SolutionSet<T> ss : inputSolutions.values()) {
////        		SPFInput SPFInput = new SPFInput();
////        		SPFInput.ID = ss.getID();
////        		SPFInput.input = ss.getSolution().getShortestExampleString();
////        		SPFInputs.inputSet.add(SPFInput);
////            }
//
//        	for (Integer i : inputSolution.keySet()) {
//        		SPFInput SPFInput = new SPFInput();
//        		SPFInput.ID = i;
//        		SPFInput.input = inputSolution.get(i).getShortestExampleString();
//        		SPFInputs.inputSet.add(SPFInput);
//        	}
//
//        	ObjectMapper mapper = new ObjectMapper();
//        	mapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//        	try {
//        		mapper.writeValue(new File(solutionFile), SPFInputs);
//        	} catch (JsonGenerationException e1) {
//        		System.err.println(cid + "Error Generating JSON ...");
//        	} catch (JsonMappingException e1) {
//        		System.err.println(cid + "Error Mapping JSON ...");
//        	} catch (IOException e1) {
//        		System.err.println(cid + "Error Writing JSON File ...");
//        		// return false;
//        	}
//
//        }

        // output all input solutions

//        for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
//            if (c.getOp() == Operation.INIT_SYM) {
//                if (c.getSolution() == null) {
//                    System.out.println("\nFAILURE: Failed to get example to one or more inputs...");
//                    System.out.println("\nSOLUTION TIME ms: 0");
//                    return;
//                } else {
//
//                }
//            }
//        }

        // ------------------------------------------------------------------------------------
        // The input example process stops here.
        // ------------------------------------------------------------------------------------


//        if (toProcess.isEmpty()) {
//            printDebug("DONE PROCESSING\n");
//            printDebug(solutions.toString());
//            System.out.println(solutions.getSolutions());
//        }

//        if (debug) { //printing of solutions done each iteration just print unsat/sat
//            if (toProcess.isEmpty()) {//processing is done
//                System.out.println("DONE PROCESSING\n");
//                if (inputSolution.size() != ((InvDefaultDirectedGraph) graph).getNumSymInputs()) {
//                    System.out.println("error in solutions set");
//                    System.out.println("expected: " + ((InvDefaultDirectedGraph) graph).getNumSymInputs());
//                    System.out.println("actual: " + inputSolution.size());
//                    for (Integer id : inputSolution.keySet()) {
//                        System.out.println(id + ": \"" + inputSolution.get(id).getShortestExampleString() + "\"");
//                    }
//                    System.out.println("unsat");
//                } else {
//                    System.out.println("sat,");
//                    for (Integer id : inputSolution.keySet()) {
//                        System.out.println(id + ": \"" + inputSolution.get(id).getShortestExampleString() + "\"");
//                    }
//                }
//            }
//        } else {
//            if (toProcess.isEmpty()) { //done
//                if (inputSolution.size() != ((InvDefaultDirectedGraph) graph).getNumSymInputs())
//                    System.out.println("unsat");
//                else {
//                    System.out.println("sat,");
//                    for (Integer id : inputSolution.keySet()) {
//                        System.out.println(id + ": \"" + inputSolution.get(id).getShortestExampleString() + "\"");
//                    }
////					for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
////						if (c.getOp() == Operation.INIT_SYM){
////							System.out.println(c.getID() + ": \"" + c.output(0).getShortestExampleString() + "\"");
////                        }
////                    }
//                }
//            }
//        }


    }

    @Override
    protected void outputHeader() {

        // gather headers in list
        List<String> headers = new ArrayList<>();
        headers.add("ID");
        headers.add("ACT");
        headers.add("SING");
        headers.add("TSAT");
        headers.add("FSAT");
        headers.add("DSJ");
        headers.add("IN ID");
        headers.add("IN CT");
        headers.add("Model_Acyclic_Inverse CT");
        headers.add("F CT");
        headers.add("OLP");
        headers.add("PRE");

        // generate headers string
        String header = joinStrings(headers, "\t");

        // output header
        printDebug(cid + header);
    }

    @Override
    protected void solveInputs() {
        //from Marlin's code
        // output finalized inverse constraints for debug

        printDebug(cid);
        printDebug(cid + "Inverse Constraint Set:");
        for (I_Inv_Constraint c : allInverseConstraints.values()) {
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
        ArrayList<PrintConstraint> toProcess = graph.getNecessaryPredicates();
        for (PrintConstraint c : toProcess) {
            predicateIDs.add(c.getId());
        }

        printDebug(predicateIDs.toString());
        InvDefaultDirectedGraph eGraph = graph;

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
        List<I_Inv_Constraint> q = new ArrayList<I_Inv_Constraint>();
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
        for (I_Inv_Constraint c : allInverseConstraints.values()) {
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
            I_Inv_Constraint curr = allInverseConstraints.get(currID);
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
        for (I_Inv_Constraint c : allInverseConstraints.values()) {
            if (c.getOp() == Operation.INIT_SYM) {
                Model_Acyclic_Inverse solution = c.getSolution();
                if (solution == null || solution.isEmpty()) {
                    printDebug("INPUT SOLUTION SET INCONSISTENT: " + c.getID());
                } else {
					InvConstraintInput ic = (InvConstraintInput) c;
					// Adding solution complements for caching purposes
                    solutions.add(c.getID(), ic.getOriginalName(), solution);
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

        System.out.println(solutions.getResult());
		printDebug("*at bound length: " + invSolver.getBound());

    }

    @Override
    public SolutionSet getSolutionSet() {
        return solutions;
    }

}
