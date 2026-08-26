package edu.boisestate.cs.graph;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.List;

public class InvConstraintReplaceFirst extends A_Inv_Constraint {

    private final Model_Acyclic_Inverse find, replace;
    // these could/can be symbolics based on regexes but currently not implemented to take symbolic as arg.

    public InvConstraintReplaceFirst(int id, Solver_Inverse invSolver, List<Integer> args) {
        this.solver = invSolver;
        this.ID = id;
        this.argList = args;
        this.op = Operation.REPLACE_FIRST;
        this.outputSet = new HashMap<Integer, Model_Acyclic_Inverse>();
        this.solutionSet = new SolutionSetInternal(id);
        this.argString = "0:FIND 1:REPLACE";
        int findId = argList.get(0);
        int replaceId = argList.get(1);

        String findStr = solver.getConcreteString(findId);
        if (findStr == null) {
            this.find = solver.getSymbolicModel(findId);
//            System.err.println("FIND IS NULL");
//            System.exit(1);
        } else {
            this.find = this.solver.modelManager.createString(findStr);
        }

        String replaceStr = solver.getConcreteString(replaceId);
        if (replaceStr == null) {
            this.replace = solver.getSymbolicModel(replaceId);
//            System.err.println("REPLACE IS NULL");
//            System.exit(1);
        } else {
            this.replace = this.solver.modelManager.createString(replaceStr);
        }
    }

    public Tuple<Boolean, Boolean> evaluate() {
        Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);
        printDebug("EVALUATE REPLACE FIRST " + ID + " ...");
        Model_Acyclic_Inverse inputs = incoming();
        printDebug("REPLACE FIRST INCOMING: " + inputs.getShortestExampleString());
        if(inputs.isEmpty()){
            printDebug("REPLACE FIRST INCOMING SET INCONSISTENT");
            ret = new Tuple<>(false, true);
        } else {
            // calls the solver_inverse method which calls the model_acyclic method
            Model_Acyclic_Inverse resModel = solver.inv_replaceFirst(inputs, find, replace);
            if (resModel != null) {
                // add the result to the outputSet
                // for some reason we use a hashmap that is indexed not by the ID by just by liek 1,2,3? i guess to do with source vs target but still unclear
                outputSet.put(1, resModel);
                // add find/replace to outputSet - TODO: make replacement use models for find/replace
                outputSet.put(2, find);
                printDebug("INVERSE REPLACE FIRST: FIND " + find.getShortestExampleString() + " REPLACE " + replace.getShortestExampleString());
                outputSet.put(3, replace);
                printDebug("INVERSE REPLACE FIRST OUTPUT SET: " + resModel.getShortestExampleString());
            } else {
                System.err.println("INVERSE REPLACE FIRST OUTPUT SET IS NULL");
                System.exit(1);
            }
        }
        // set up outputSet?
        return ret;
    }
}
