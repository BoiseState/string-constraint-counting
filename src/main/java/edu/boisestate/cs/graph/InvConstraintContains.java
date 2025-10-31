/**
 *
 */
package edu.boisestate.cs.graph;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.List;

/**
 * @author Marlin Roberts, 2020-2021
 */
public class InvConstraintContains<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {
    private boolean result;
	private T arg1;
	private T arg2;

    public InvConstraintContains(int ID, Solver_Inverse<T> solver, boolean result) {
        this.solver = solver;
        this.ID = ID;
		this.result = result;
		this.outputSet = new HashMap<>();
        this.op = Operation.CONTAINS;
    }

    @Override
    public Tuple<Boolean, Boolean> evaluate() {
		// at this point a simple check has been made and we are simply making choices to propagate
        printDebug("EVALUATE CONTAINS PREDICATE " + ID + " ...");
        Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);

		if (arg1 == null || arg2 == null) {
			// first time eval, set args
			// i didnt choose these designs, im just trying to make the best of them :)
			arg1 = solver.getSymbolicModel(nextID).clone();
			arg2 = solver.getSymbolicModel(argID).clone();
		}

		// arg2 is manipulated in place to keep track of remaining choices
		Tuple<T,T> choice = arg1.inv_contains(arg2, result);
		T sup = choice.get1();
		T sub = choice.get2();

		outputSet.put(1, sup);
		outputSet.put(2, sub);

		if (!arg2.isEmpty()){
			// add to backtrack if more choices
			ret = new Tuple<>(true, false);
		}
		return ret;
    }

    @Override
    public void setOp(Operation op) {

        this.op = op;
    }

    @Override
    public Operation getOp() {

        return op;
    }

    @Override
    public void setID(int ID) {

        this.ID = ID;
    }

    @Override
    public int getID() {

        return this.ID;
    }


}
