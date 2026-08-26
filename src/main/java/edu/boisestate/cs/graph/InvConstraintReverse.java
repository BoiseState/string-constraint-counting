/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;
import edu.boisestate.cs.solvers.*;
import edu.boisestate.cs.util.Tuple;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintReverse extends A_Inv_Constraint {
	
	
	public InvConstraintReverse (int ID, Solver_Inverse solver) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.op  = Operation.REVERSE;
		this.outputSet = new HashMap<Integer,Model_Acyclic_Inverse>();
		this.solutionSet = new SolutionSetInternal(ID);
		this.argString = "[NONE]";
	}
	
	
	
	public InvConstraintReverse (int ID, Solver_Inverse solver, List<Integer> args) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op  = Operation.REVERSE;
		this.argString = "[NONE]";
	}
	
	public InvConstraintReverse (int ID, Solver_Inverse solver, List<Integer> args, int base, int input) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op  = Operation.REVERSE;
		this.argString = "[NONE]";
		this.nextID = base;
		this.prevIDs = new HashSet<Integer>(); this.prevIDs.add(input);
	}


    public Tuple<Boolean,Boolean> evaluate() {
        Tuple<Boolean, Boolean> ret = new Tuple<Boolean, Boolean>(true, true);
        printDebug("EVALUATE REVERSE " + ID + " ...");
        Model_Acyclic_Inverse inputModel = incoming();
        printDebug("REVERSE INCOMING: " + inputModel.getShortestExampleString());
        if (inputModel.isEmpty()) {
            printDebug("REVERSE INCOMING SET INCONSISTENT...");
            ret = new Tuple<>(false, true);
        } else {
            // now starting to not call solver as intermediary
            // just call inv_reverse directly
            Model_Acyclic_Inverse resModel = inputModel.inv_reverse(); // just calls reverse
            if (resModel == null) {
                System.err.println("INVERSE REVERSE FAILED");
                System.exit(1);
            } else {
                outputSet.put(1, resModel);
                printDebug("REVERSE OUTPUT: " + resModel.getShortestExampleString());
            }
        }
        return ret;
    }


//	@Override
//	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex) {
//
//		System.out.format("EVALUATE REVERSE %d ...\n",ID);
//
//		T inputModel = inputConstraint.output(sourceIndex);
//
//		// perform inverse function on output from the input constraint at given index
//		T resModel = solver.inv_reverse(inputModel);
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
//				System.out.println("REVERSE SOLUTION SET INCONSISTENT...");
//				solutionSet.remSolution(inputConstraint.getID());
//				return false;
//			}
//
//		} else {
//			System.out.println("REVERSE RESULT MODEL EMPTY...");
//			// halt solving, fallback
//			return false;
//		}
//
//
//
//
//
//
//	}


}
