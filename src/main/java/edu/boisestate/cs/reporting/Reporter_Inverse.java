package edu.boisestate.cs.reporting;

import edu.boisestate.cs.BasicTimer;
import edu.boisestate.cs.Parser_2;
import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.graph.*;
import edu.boisestate.cs.solvers.Solver_Inverse;

import org.jgrapht.DirectedGraph;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Reporter that attempts to determine input example for each predicate encountered.
 *
 * @param <T> - Automata model that implements inverse operations.
 * @author Marlin Roberts
 */
@SuppressWarnings("unused")
public class Reporter_Inverse<T extends A_Model_Inverse<T>> extends A_Reporter<T> {

    protected final Solver_Inverse<T> invSolver;
    protected Map<Integer, PrintConstraint> allConstraints = new HashMap<>();
    protected Map<Integer, I_Inv_Constraint<T>> allInverseConstraints = new HashMap<>();
    //protected Map<Integer,SolutionSet<T>> inputSolutions = new HashMap<>();
//    protected Map<Integer, T> inputSolution = new HashMap<>();
//    protected SolutionSet<T> solutions;
    protected Map<Integer, Integer> inputIndexes = new HashMap<>();
    protected List<Integer> predicateIDs = new ArrayList<>();
    private boolean saveResults = false;
    private String graphName;
    private String saveFile = "src\\test\\automata\\";

    // temporary implementation of example output to file
    public String solutionFile = "./temp/solutions.txt";

    // prefix for output when running inside SPF
    protected static String cid = "[IGEN] ";

    /**
     * Constructor for inverse reporter. Keeps a reference to the given solver as an inverse solver.
     * Stores constraints in a new map for reference later.
     *
     * @param graph
     * @param parser
     * @param invSolver
     * @param debug
     */
    public Reporter_Inverse(DirectedGraph<PrintConstraint, SymbolicEdge> graph,
                            Parser_2<T> parser,
                            Solver_Inverse<T> invSolver,
                            boolean debug) {

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
    }

    /**
     * sets save option for file output, used for getting example back to SPF
     *
     * @param save - boolean true = save to file
     * @param name - filename
     */
    public void setSaveOption(boolean save, String name) {
        this.saveResults = save;
        this.graphName = name;
        this.saveFile = this.saveFile + graphName;
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

        buildICG_r3();
        // build the transposed graph of inverse constraints
//        if (build) {
////            buildICG_r3();
//            solutions = new SolutionSet<>(((InvDefaultDirectedGraph) graph).getNumSymInputs());
//            build = false; // only building once and keeping the inverse constaints from before, which may be wrong
//        }

        if (debug) {
            for (I_Inv_Constraint<T> con : allInverseConstraints.values()) {
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
////        	}
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
////						}
////					}
//                }
//            }
//        }


    }

    /*
     * builds the transposed graph of inverse constraints.
     * first, creates an inverse constraint for every print constraint.
     * second, sets the internal next and arg references to the correct inverse constraint.
     */
    protected void buildICG_r3() {

        boolean localDebug = false;

        List<Integer> args;

        // create inverse constraint for every print constraint
        // op was set during the forward graph construction
        for (PrintConstraint pc : allConstraints.values()) {

            int ID = pc.getId();
            Operation op = pc.getOp();
            String value = pc.getActualVal();
            printDebug("ID " + ID + " op " + op);
            I_Inv_Constraint<T> newConstraint;

            switch (op) {

                case INIT:

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }


                    break;

                case INIT_CON:

                    newConstraint = new InvConstraintConcreteValue<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case INIT_SYM:

                    newConstraint = new InvConstraintInput<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case PREDICATE:

                    boolean result = value.equals("true") ? true : false;
                    newConstraint = new InvConstraintPredicate<T>(ID, invSolver, result);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case EQUALS:
                    boolean output = value.equals("true") ? true : false;
                    newConstraint = new InvConstraintEquals<T>(ID, invSolver, output);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case PROPAGATION:

                    newConstraint = new InvConstraintPropagation<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case CONCAT_SYM:

                    newConstraint = new InvConstraintConcatSym<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case CONCAT_CON:

                    // FIX
                    // **** Using symbolic code for now, needs concrete ported to r3
                    newConstraint = new InvConstraintConcatSym<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case TOUPPERCASE:

                    newConstraint = new InvConstraintToUpperCase<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case TOLOWERCASE:

                    newConstraint = new InvConstraintToLowerCase<T>(ID, invSolver);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case SUBSTR_STRT_END:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintSubStringStartEnd<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case SET_LENGTH:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintSetLength<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case SUBSTRING_START:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintSubStringStart<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;


                case DELETE_START_END:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintDeleteStartEnd<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case DELETE_CHAR_AT:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintDeleteCharAt<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }

                    break;

                case REPLACE_CHAR_CHAR:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintReplaceCharChar<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
                    }

                    break;

                case REPLACE_FIRST:

                    args = pc.getArgList();
                    newConstraint = new InvConstraintReplaceFirst<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
                    }

                    break;
                case REPLACE_ALL: // unsure why we need seperate cases for these?
                    args = pc.getArgList();
                    newConstraint = new InvConstraintReplaceAll<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
                    }

                    break;
                case CHAR_AT:
                    args = pc.getArgList();
                    newConstraint = new InvConstraintCharAt<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
                    }

                    break;
                case INDEX_OF:
                    args = pc.getArgList();
                    newConstraint = new InvConstraintIndexOf<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
                    }

                    break;
                case TRIM:
                    args = pc.getArgList();
                    newConstraint = new InvConstraintTrim<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);
                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId());
                    }
                    break;
                case INSERT:
                    args = pc.getArgList();
                    newConstraint = new InvConstraintInsert<T>(ID, invSolver, args);
                    allInverseConstraints.put(ID, newConstraint);

                    if (localDebug) {
                        System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
                    }

                    break;
                default:

                    if (localDebug) {
                        System.out.println("WARNING: Unhandled constraint type... " + op.toString() + "  " + pc.getId() + "  " + pc.getValue());
                    }
                    System.err.println("WARNING: Unhandled constraint in Reporter_Inverse.buildICG_r3() of type... " + op.toString() + "  " + pc.getId() + "  " + pc.getValue());


            } // end switch


        } // end for each printconstraint


        // all inverse constraints have been created, now we set the next and arg constraint references
        for (PrintConstraint pc : allConstraints.values()) {

            if (pc.getOp() != Operation.UNDEFINED) {
                // each print constraint has a corresponding inverse constraint, get a reference to it
                I_Inv_Constraint<T> invConstraint = allInverseConstraints.get(pc.getId());

                // the next inverse constraint to evaluate is the base of printconstraint
                I_Inv_Constraint<T> nextConstraint = allInverseConstraints.get(pc.getBase());
                if (nextConstraint != null) {
                    invConstraint.setNext(nextConstraint);
                }


                // get the arglist and check if not empty
                List<Integer> argList = pc.getArgList();


                // MJR some operations only have concrete arguments and the value in args are the actual values and NOT constraint IDs.
                // nps: this is no longer true, we handle all args symbolically , though integers not fully/properly yet
                // TODO: revisit this and always handle constraint
                if (!argList.isEmpty() && pc.getOp() != Operation.SUBSTR_STRT_END &&
                        pc.getOp() != Operation.SUBSTRING_START &&
                        pc.getOp() != Operation.SET_LENGTH &&
//                        pc.getOp() != Operation.REPLACE_CHAR_CHAR &&
                        pc.getOp() != Operation.DELETE_START_END) {


                    // argument present, set invConstraint argument reference.
                    int arg = argList.get(0);
                    if (arg != -1) {

                        if (localDebug) {
                            System.out.println("pcID: " + pc.getId() + " getting arg: " + arg);
                        }


                        if (invConstraint.getOp()!=Operation.CHAR_AT) {// TODO: nps i should probably revisit this
                            if (invConstraint.getOp() == Operation.INSERT) {
                                invConstraint.setArg(allInverseConstraints.get(argList.get(1)));
                            } else {
                                invConstraint.setArg(allInverseConstraints.get(arg));
                            }
                        }
                    } // end if
                    if (argList.size()==2){ // this shouldnt ever happen anymore as we handle replace first and all symboliclally - nps - 04/16/2025
                        if (pc.getOp() == Operation.REPLACE_FIRST || pc.getOp() == Operation.REPLACE_ALL) {
                            int arg2 = argList.get(1);
                            if (arg2 != -1) {
                                invConstraint.setArg2(allInverseConstraints.get(arg2));
                            }
                        }
                    }
//					if (pc.getOp() == Operation.REPLACE_CHAR_CHAR) {
//						int arg2 = argList.get(1);
//						if (arg2 != -1) {
//							invConstraint.setArg2(allInverseConstraints.get(arg2));
//						}
//					}

                } // end if

            } // end if
            else {
                System.err.println("WARNING: UNDEFINED constraint in Reporter_Inverse.buildICG_r3() of type... "  + pc.getId() + "  " + pc.getValue());
                System.exit(1);
            }

        }  // end for each printconstraint

        //populate previous constraints -- need for BFS
        //get all parents for the particular node
        for (I_Inv_Constraint<T> p : allInverseConstraints.values()) {

            HashSet<I_Inv_Constraint<T>> invParents = new HashSet<I_Inv_Constraint<T>>();
            for (SymbolicEdge e : graph.outgoingEdgesOf(allConstraints.get(p.getID()))) {
                PrintConstraint source = (PrintConstraint) e.getATarget();
                //add to p's incoming set
                I_Inv_Constraint<T> invSource = allInverseConstraints.get(source.getId());
                //it could be null since it has not been processed yet
                if (invSource != null) {
                    invParents.add(allInverseConstraints.get(source.getId()));
                }

            }
            printDebug("Parents  " + p + " are " + invParents);
            I_Inv_Constraint<T> invP = allInverseConstraints.get(p.getID());
            invP.setPrev(invParents);
        }

    } // end buildICG_r3


    /*
     * solves inputs after all forward analysis is complete
     */
    protected void solveInputs() {

        // output finalized inverse constraints for debug
        if (false) {
            System.out.println(cid);
            System.out.println(cid + "Inverse Constraint Set:");
            for (I_Inv_Constraint<T> c : allInverseConstraints.values()) {
                System.out.println(cid + c.toString() + "\t" + allConstraints.get(c.getID()).toString());
            }
            System.out.println(cid);
        }

        long startTime = System.nanoTime();

        // initialize our copy of the symbolic string map as it is right now
        invSolver.initStringMap();

        // this treats all predicates as unrelated.
        // need to replace with queue and backtracking across predicates.
        for (Integer predicateID : predicateIDs) {
            // get a reference to the predicate inverse constraint
            I_Inv_Constraint<T> predicate = allInverseConstraints.get(predicateID);

            // ********************************
            // The call that starts it all ....
            predicate.evaluate(null, 0);
            // ******************************** 

        }

        long endTime = System.nanoTime();

        long durationInNano = (endTime - startTime);

        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        for (I_Inv_Constraint<T> i : allInverseConstraints.values()) {
            if (i.getOp() == Operation.INIT_SYM) {
                if (i.getSolution() == null) {
                    System.out.println("\nFAILURE: Failed to get example to one or more inputs...");
                    System.out.println("\nSOLUTION TIME ms: 0");
                    return;
                }
            }
        }

        System.out.println("\nSOLUTION TIME ms: " + durationInMillis);

//        for (I_Inv_Constraint<T> i : allInverseConstraints.values()) {
//            if (i.getOp() == Operation.INIT_SYM) {
//                T solution = i.getSolution();
//
//                // populate map for output to file/SPF
//                inputSolution.put(i.getID(), solution);
//
//                System.out.println(i.getID() + ": " + solution.getShortestExampleString());
//
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
        headers.add("T CT");
        headers.add("F CT");
        headers.add("OLP");
        headers.add("PRE");

        // generate headers string
        String header = joinStrings(headers, "\t");

        // output header
        printDebug(cid + header);
    }

    @Override
    public SolutionSet<T> getSolutionSet() {
        throw new UnsupportedOperationException("getSolutions not supported yet for this reporter");
    }
}
