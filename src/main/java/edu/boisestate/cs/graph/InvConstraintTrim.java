/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.*;
import edu.boisestate.cs.util.Tuple;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintTrim<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {
	
	
	public InvConstraintTrim (int ID, Solver_Inverse<T> solver) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.op  = Operation.TRIM;
		this.outputSet = new HashMap<Integer,T>();
		this.solutionSet = new SolutionSetInternal<T>(ID);
		this.argString = "[NONE]";
	}
	
	
	
	public InvConstraintTrim (int ID, Solver_Inverse<T> solver, List<Integer> args) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op  = Operation.TRIM;
		this.argString = "[NONE]";
	}
	
	public InvConstraintTrim (int ID, Solver_Inverse<T> solver, List<Integer> args, int base, int input) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op  = Operation.TRIM;
		this.argString = "[NONE]";
		this.nextID = base;
		this.prevIDs = new HashSet<Integer>(); this.prevIDs.add(input);
	}

	@Override
	public Tuple<Boolean,Boolean> evaluate(){
		Tuple<Boolean,Boolean> ret = new Tuple<>(true, true);
		printDebug("EVALUATE TRIM " + ID + " ...");
		T inputModel = incoming();
		printDebug("TRIM INCOMING: " + inputModel.getShortestExampleString());
		if (inputModel.isEmpty()) {
			printDebug("TRIM INCOMING SET INCONSISTENT");
			ret = new Tuple<>(false, true);
		} else {
			// calls the solver_inverse method which calls the model_acyclic method
			T resModel = solver.inv_trim(inputModel);

			if (resModel == null) {
				System.err.println("INVERSE TRIM FAILED");
				System.exit(1);
			} else {
				outputSet.put(1, resModel);
				printDebug("TRIM OUTPUT: " + resModel.getShortestExampleString());
			}
		}
		return ret;
	}
	
	@Override
	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex) {

		System.out.format("EVALUATE TRIM %d ...\n",ID);
		
		T inputModel = inputConstraint.output(sourceIndex);

		// perform inverse function on output from the input constraint at given index
		T resModel = solver.inv_trim(inputModel);

		// intersect result with forward analysis results from previous constraint
		resModel = solver.intersect(resModel, nextConstraint.getID());


		if (!resModel.isEmpty()) {
			solutionSet.setSolution(inputConstraint.getID(), resModel);

			if (solutionSet.isConsistent()) {
	
				// store result in this constraints output set at index 1
				outputSet.put(1, resModel);	
	
	
				// we have values, so continue solving ...
				return nextConstraint.evaluate(this, 1);
			} else {
				System.out.println("TRIM SOLUTION SET INCONSISTENT...");
				solutionSet.remSolution(inputConstraint.getID());
				return false;
			}
			
		} else {
			System.out.println("TRIM RESULT MODEL EMPTY...");
			// halt solving, fallback
			return false;
		}






	}


}
