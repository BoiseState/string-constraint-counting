package edu.boisestate.cs.graph;

import dk.brics.automaton.Automaton;
import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.List;

public class InvConstraintIndexOf<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {
    private final int findID;
    private final int bound;

    public InvConstraintIndexOf(int ID, Solver_Inverse<T> solver, List<Integer> args) {
        this.solver = solver;
        this.ID = ID;
        this.argList = args;
        this.op = Operation.INDEX_OF;
        this.outputSet = new HashMap<Integer, T>();
        this.solutionSet = new SolutionSetInternal<T>(ID);
        this.argString = "0:INDEX";
        this.findID = argList.get(0);
        this.bound = solver.getBound();
    }

    @Override
    public Tuple<Boolean, Boolean> evaluate() {
        Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);
        printDebug("EVALUATE INDEX OF " + ID + " ...");
        T inputs = incoming();
        printDebug("INDEX OF INCOMING: " + inputs.getShortestExampleString());
        if (inputs.isEmpty()) {
            printDebug("INDEX OF INCOMING SET INCONSISTENT");
            ret = new Tuple<>(false, true);
        } else {
            // calls the solver_inverse method which calls the model_acyclic method
            T findOriginal = solver.getSymbolicModel(findID);
            T findModel = findOriginal.clone();
            T resModel = solver.inv_indexOf(inputs, findModel, bound);

            if (resModel == null) {
                System.err.println("INVERSE INDEX OF FAILED");
                System.exit(1);
            } else {
                outputSet.put(1, resModel);
                printDebug("INDEX OF OUTPUT: " + resModel.getShortestExampleString());
                // no way to propogate the index value back..?

                // need to add the find model to the output set as even concretes do sanity checks
                // TODO: in theory we could/would take index that was found and use it to more preciesly define the find model...
                // i think that would require essentially propogating a result/find pair :)

                // so we chose a sinlge string for find to propogate for result/find
                // now we check if there are more possiblilities and add to backtrack map
                // removing the one we chose from original find model
                outputSet.put(2, findModel);
                Automaton findAutOG = findOriginal.getAutomatonObject();
                findAutOG = findAutOG.minus(findModel.getAutomatonObject());
                if (!findAutOG.isEmpty()){
                    ret = new Tuple<>(true, false);
                }
                //otherwise we've exhausted our search
            }
        }
        return ret;
    }
}
