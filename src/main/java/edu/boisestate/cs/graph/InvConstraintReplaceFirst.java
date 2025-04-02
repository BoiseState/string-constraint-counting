package edu.boisestate.cs.graph;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.List;

public class InvConstraintReplaceFirst<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {

    private String find, replace;

    public InvConstraintReplaceFirst(int id, Solver_Inverse<T> invSolver, List<Integer> args) {
        this.solver = invSolver;
        this.ID = id;
        this.argList = args;
        this.op = Operation.REPLACE_FIRST;
        this.outputSet = new HashMap<Integer, T>();
        this.solutionSet = new SolutionSetInternal<T>(id);
        this.argString = "0:FIND 1:REPLACE";
        int findId = argList.get(0);
        int replaceId = argList.get(1);
        this.find = solver.getConcreteString(findId);
        this.replace = solver.getConcreteString(replaceId);
    }

    public Tuple<Boolean, Boolean> evaluate() {
        Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);
        printDebug("EVALUATE REPLACE FIRST " + ID + " ...");
        T inputs = incoming();

        if(inputs.isEmpty()){
            printDebug("REPLACE FIRST INCOMING SET INCONSISTENT");
            ret = new Tuple<>(false, true);
        } else {
            T resModel = solver.inv_replaceFirst(inputs, find, replace); // forcing known for now: i.e. args(find, replace) are concrete
        }
        return ret;
    }
}
