package edu.boisestate.cs.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.*;
import edu.boisestate.cs.util.Quadruple;
import edu.boisestate.cs.util.Tuple;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintInsert<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {

    private int start, insertStringID;
//    private T inputs = null;
//    private Triple<T,T,T> backtrack = null; // current backtrack state: (currentPrefixSearch, (suffixesToSearch, inputModelSuffixes))
//    private ArrayList<Tuple<T, T>> outputs = new ArrayList<>();
//    private HashMap<T, List<Tuple<T, T>>> mapInOut = new HashMap<>();
    private T IN = null;
    private HashMap<T,T> prefSuffMap = new HashMap<>();
    private Tuple<T,T> remaining = null;

    public InvConstraintInsert(int ID, Solver_Inverse<T> solver, List<Integer> args) {

        // Store reference to solver
        this.solver = solver;
        this.ID = ID;
        this.argList = args;
        this.op = Operation.INSERT;
        this.outputSet = new HashMap<Integer, T>();
//		this.solutionSet = new SolutionSetInternal<T>(ID);
//		this.argString = "0:START 1:END";
        this.start = argList.get(0);
        this.insertStringID = argList.get(1);
    }

    public InvConstraintInsert(int ID, Solver_Inverse<T> solver, List<Integer> args, int base, int input) {

        // Store reference to solver
        this.solver = solver;
        this.ID = ID;
        this.argList = args;
        this.op = Operation.INSERT;
        this.argString = "0:START 1:END";
        this.start = argList.get(0);
//		this.end = argList.get(1);
//		this.nextID = base;
        this.nextID = base;
        this.prevIDs = new HashSet<Integer>();
        this.prevIDs.add(input);
    }

    @Override
    public Tuple<Boolean, Boolean> evaluate() {
        Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);
        printDebug("EVALUATE INSERT " + ID + " ...");

//        // check if there are any insert_backtracks for this constraint to explore
//        if (backtrack != null) {
//            // backtrack specific method for only search suffixes
//            // however it does need to manipulate the insert model as well...
//            // unfortunately i think we will implement this here even though Model_Acyclic_Inverse does the same work
//            Model_Acyclic_Inverse prefix = (Model_Acyclic_Inverse) backtrack.get1();
//            Model_Acyclic_Inverse suffixesToSearch = (Model_Acyclic_Inverse) backtrack.get2();
//            Model_Acyclic_Inverse inputSuffix = (Model_Acyclic_Inverse) backtrack.get3();
//        }
        if (remaining!=null){
            // we have found a prefix already and there are remaining suffixes to try
            ret = new Tuple<>(true, false);// continue but add to backtrack as may still be prefixes ot try
            T insertStringModel = solver.getSymbolicModel(insertStringID);
            T sourceModel = solver.getSymbolicModel(nextID); // model of source/target from forward analysis
            T prefix = remaining.get1();
            T suffix = remaining.get2(); // the suffix to search for a new split

            Tuple<T,T> candidate = suffix.getPathConsistentPair(insertStringModel,sourceModel);
            if (candidate == null) { //no consistent pair found
                printDebug("INSERT RESULT MODEL EMPTY...");
                ret = new Tuple<>(false, false);// don't continue but add to backtrack?
                remaining = null;
                return ret;
            }
            T insert = candidate.get1();
            T suff = candidate.get2();
            if (!suffix.isEmpty()){
                remaining = new Tuple<>(prefix, suffix); //yet more suffixes to try
            } else {
                remaining = null; // used up all suffixes for this prefix
            }
            T resModel = prefix.concatenate(suff);
            outputSet.put(1, resModel);
            outputSet.put(2, insert);
            return ret;

        }
        if (IN == null) { // first time or after clear
            printDebug("input is null");
            IN = incoming();
        }
        if (IN.isEmpty()) {
            printDebug("INSERT INCOMING SET INCONSISTENT...");
            ret = new Tuple<>(false, true);
        } else {
            T insertStringModel = solver.getSymbolicModel(insertStringID);
            T sourceModel = solver.getSymbolicModel(nextID); // model of source/target from forward analysis

            // this will manipulate IN and pass back candidates for the target and insert
            // will also need to pass back the specific prefix it used?
            Quadruple<T,T,T,T> candidate = IN.inv_insert(sourceModel, insertStringModel, start);
            if (candidate == null) { // i return null if no candidates found, this isnt exhaustive though
                printDebug("NO CANDIDATES...");
                if (!IN.isEmpty()) {
                    ret = new Tuple<>(false, false); // more prefixes to find: dont continue but add ot backtrack
                } else {
                    ret = new Tuple<>(false, true);
                }
                return ret;
            }
            if (!IN.isEmpty()) {
                // more prefixes to try
                ret = new Tuple<>(true, false); // continue but add to backtrack
            }
            if (!candidate.get4().isEmpty()) {
                // we have remaining suffixes to try with the same prefix
                remaining = new Tuple<>(candidate.get1(), candidate.get4());
            } else {
                remaining = null;
            }
            // check propagation?
            outputSet.put(1, candidate.get1().concatenate(candidate.get3()));
            outputSet.put(2, candidate.get2());

//            if (!resModel.isEmpty()) {
//                //index?
//                outputSet.put(1, resModel);
//                outputSet.put(2, insertStringModel); // also prop insert model though this is never manipulated??
//            } else {
//                printDebug("INSERT RESULT MODEL EMPTY...");
//                ret = new Tuple<>(false, true);
//            }

        }


        return ret;
    }

//	@Override
//	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex) {
//
//		System.out.format("EVALUATE INSERT %d ...\n",ID);
//
//		T inputModel = inputConstraint.output(sourceIndex);
//
//		// perform inverse function on output from the input constraint at given index
//		T resModel = solver.inv_insert(inputModel, start, end);
//
//		// intersect result with forward analysis results from previous constraint
//		resModel = solver.intersect(resModel, nextConstraint.getID());
//
//
//		if (!resModel.isEmpty()) {
//			solutionSet.setSolution(inputConstraint.getID(), resModel);
//
//			if (solutionSet.isConsistent()) {
//
//				// store result in this constraints output set at index 1
//				outputSet.put(1, resModel);
//
//
//				// we have values, so continue solving ...
//				return nextConstraint.evaluate(this, 1);
//			} else {
//				System.out.println("INSERT SOLUTION SET INCONSISTENT...");
//				solutionSet.remSolution(inputConstraint.getID());
//				return false;
//			}
//
//		} else {
//			System.out.println("INSERT RESULT MODEL EMPTY...");
//			// halt solving, fallback
//			return false;
//		}
//
//	}
//

//	@Override
//	public void setNext(I_Inv_Constraint constraint) {
//		
//		this.nextConstraint = constraint;
//	}
//
//	@Override
//	public void setPrev(I_Inv_Constraint constraint) {
//		
//		this.prevConstraint = constraint;
//	}
//
//	@Override
//	public void setOp(Operation op) {
//		
//		this.op = op;
//	}
//
//	@Override
//	public Operation getOp() {
//		
//		return op;
//	}
//
//	@Override
//	public void setID(int ID) {
//		
//		this.ID = ID;
//	}
//
//	@Override
//	public int getID() {
//
//		return this.ID;
//	}

}
