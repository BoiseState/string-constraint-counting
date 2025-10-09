/**
 * 
 */
package edu.boisestate.cs.graph;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintLength<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {

	public InvConstraintLength(int ID, Solver_Inverse<T> solver) {
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.op  = Operation.LENGTH;
		this.outputSet = new HashMap<Integer,T>();
	}

	@Override
	public Tuple<Boolean,Boolean> evaluate(){
		Tuple<Boolean,Boolean> ret = new Tuple<>(true, true);
		printDebug("EVALUATE LENGTH" + ID + " ...");
		T inputModel = incoming();
		// parse int bound
		if (inputModel.isEmpty()) {
			printDebug("LENGTH INCOMING SET INCONSISTENT");
			ret = new Tuple<>(false, true);
		} else {
			// we handle the bounds in the predicate at forward prop, just pass back the input model
			outputSet.put(1, inputModel);
		}
		return ret;
	}
}
