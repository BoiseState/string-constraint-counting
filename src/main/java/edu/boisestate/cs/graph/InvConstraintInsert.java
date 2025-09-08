package edu.boisestate.cs.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;
import edu.boisestate.cs.solvers.*;
import edu.boisestate.cs.util.Triple;
import edu.boisestate.cs.util.Tuple;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintInsert<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {

    private int start, insertStringID;
    private T inputs = null;
    private Triple<T,T,T> backtrack = null; // current backtrack state: (currentPrefixSearch, (suffixesToSearch, inputModelSuffixes))
    private ArrayList<Tuple<T, T>> outputs = new ArrayList<>();
    private HashMap<T, List<Tuple<T, T>>> mapInOut = new HashMap<>();

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

        // check if there are any insert_backtracks for this constraint to explore
        if (backtrack != null) {
            // backtrack specific method for only search suffixes
            // however it does need to manipulate the insert model as well...
            // unfortunately i think we will implement this here even though Model_Acyclic_Inverse does the same work
            Model_Acyclic_Inverse prefix = (Model_Acyclic_Inverse) backtrack.get1();
            Model_Acyclic_Inverse suffixesToSearch = (Model_Acyclic_Inverse) backtrack.get2();
            Model_Acyclic_Inverse inputSuffix = (Model_Acyclic_Inverse) backtrack.get3();
        }

        T inputModel = incoming();
        if (inputModel.isEmpty()) {
            printDebug("INSERT INCOMING SET INCONSISTENT...");
            ret = new Tuple<>(false, true);
        } else {
            T insertStringModel = solver.getSymbolicModel(insertStringID);
            T sourceModel = solver.getSymbolicModel(nextID); // model of source/target from forward analysis


            // inv_insert needs to take both forward and backward models, do necessary analysis, intersections,
            // as well as change stored models and set up backtracking as necessary
            // nps - 9-4-25 : method returns a model of the target, i.e. what was the original string before insertion
            // it also needs to modify the insertStringModel to reflect what was actually inserted
            // additionally we need to add it to backtracking if there are remaining choices for prefixes
            // the insert constraint should also have its own backtracking list specifically for the suffix search.
            // so we should first check if backtracks exists for this constraint and try a new suffix.
            // otherwise start a new prefix search, same as if we had backtracked.
            T resModel = solver.inv_insert(inputModel, sourceModel, start, insertStringModel);

            if (!resModel.isEmpty()) {
                //index?
                outputSet.put(1, resModel);
                outputSet.put(2, insertStringModel); // also prop insert model though this is never manipulated??
            } else {
                printDebug("INSERT RESULT MODEL EMPTY...");
                ret = new Tuple<>(false, true);
            }

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
