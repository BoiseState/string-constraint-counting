/**
 * 
 */
package edu.boisestate.cs.graph;

import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintLength extends A_Inv_Constraint {

	public InvConstraintLength(int ID, Solver_Inverse solver) {
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.op  = Operation.LENGTH;
		this.outputSet = new HashMap<Integer,Model_Acyclic_Inverse>();
	}

	@Override
	public Tuple<Boolean,Boolean> evaluate(){
		Tuple<Boolean,Boolean> ret = new Tuple<>(true, true);
		printDebug("EVALUATE LENGTH" + ID + " ...");
		Model_Acyclic_Inverse inputModel = incoming();
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
