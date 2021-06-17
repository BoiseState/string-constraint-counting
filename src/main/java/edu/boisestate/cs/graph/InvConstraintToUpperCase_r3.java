/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.HashMap;
import java.util.List;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.*;

/**
 * @author marli
 *
 */
public class InvConstraintToUpperCase_r3<T extends A_Model_Inverse<T>> extends A_Inv_Constraint_r3<T> {
	
	
	public InvConstraintToUpperCase_r3 (int ID, Solver_Inverse_r3<T> solver) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.op  = Operation.TOUPPERCASE;
		this.outputSet = new HashMap<Integer,T>();
		this.solutionSet = new SolutionSetInternal<T>(ID);
		this.argString = "[NONE]";
	}
	

	
	public InvConstraintToUpperCase_r3 (int ID, Solver_Inverse_r3<T> solver, List<Integer> args) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op = Operation.TOUPPERCASE;
		this.argString = "[NONE]";
	}
	
	public InvConstraintToUpperCase_r3 (int ID, Solver_Inverse_r3<T> solver, List<Integer> args, int base, int input) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op = Operation.TOUPPERCASE;
		this.argString = "[NONE]";
		this.nextID = base;
		this.prevID = input;
	}
	
	
	@Override
	public boolean evaluate(I_Inv_Constraint_r3<T> inputConstraint, int sourceIndex) {

		System.out.format("EVALUATE TOUPPER %d ...\n",ID);
		
		T inputModel = inputConstraint.output(sourceIndex);

		// perform inverse function on output from the input constraint at given index
		T resModel = solver.inv_toUpperCase(inputModel);

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
				System.out.println("TOUPPER SOLUTION SET INCONSISTENT...");
				solutionSet.remSolution(inputConstraint.getID());
				return false;
			}
			
		} else {
			System.out.println("TOUPPER RESULT MODEL EMPTY...");
			// halt solving, fallback
			return false;
		}

	}

}