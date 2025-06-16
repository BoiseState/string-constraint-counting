package edu.boisestate.cs.graph;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.List;

public class InvConstraintCharAt<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {
    private int index;

    public InvConstraintCharAt(int ID, Solver_Inverse<T> solver, List<Integer> args) {
        this.solver = solver;
        this.ID = ID;
        this.argList = args;
        this.op = Operation.CHAR_AT;
        this.outputSet = new HashMap<Integer, T>();
        this.solutionSet = new SolutionSetInternal<T>(ID);
        this.argString = "0:INDEX";
        this.index = argList.get(0);
    }

    @Override
    public Tuple<Boolean, Boolean> evaluate() {
        Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);
        printDebug("EVALUATE CHAR AT " + ID + " ...");
        T inputs = incoming();
        printDebug("CHAR AT INCOMING: " + inputs.getShortestExampleString());
        if (inputs.isEmpty()) {
            printDebug("CHAR AT INCOMING SET INCONSISTENT");
            ret = new Tuple<>(false, true);
        } else {
            // calls the solver_inverse method which calls the model_acyclic method
            T resModel = solver.inv_charAt(inputs, index);

            if (resModel == null) {
                System.err.println("INVERSE CHAR AT FAILED");
                System.exit(1);
            } else {
                outputSet.put(1, resModel);
                printDebug("CHAR AT OUTPUT: " + resModel.getShortestExampleString());
                // no way to propogate the index value back..?
                // TODO: would be interesting to know/see how one might do a Numeric Model
            }
        }
        return ret;
    }
}
