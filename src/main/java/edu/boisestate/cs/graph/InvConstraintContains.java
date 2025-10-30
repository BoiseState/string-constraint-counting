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

    // This will hold a reference to the containing solver.
    // This allows the constraint access to the solver functions and string tables.
    //private Solver_Inverse<T> solver;

    //private int ID;

    //private I_Inv_Constraint prevConstraint;
    //private I_Inv_Constraint nextConstraint;

    //private Operation op;

    //private List<Integer> argList;

    //private String argString;

    private boolean result;

    public InvConstraintContains(int ID, Solver_Inverse<T> solver) {

        // Store reference to solver
        this.solver = solver;
        this.ID = ID;
        //this.argID = argID;
        this.outputSet = new HashMap<Integer, T>();
        //this.argString = "[" + argList.get(0) + "]";
        this.op = Operation.PREDICATE;
    }

    public InvConstraintContains(int ID, Solver_Inverse<T> solver, boolean result) {
        this(ID, solver);
        this.result = result;
    }

    public InvConstraintContains(int ID, Solver_Inverse<T> solver, int argID) {

        // Store reference to solver
        this.solver = solver;
        this.ID = ID;
        this.argID = argID;
        this.outputSet = new HashMap<Integer, T>();
        //this.argString = "[" + argList.get(0) + "]";
        this.op = Operation.PREDICATE;
    }

    public InvConstraintContains(int ID, Solver_Inverse<T> solver, List<Integer> args) {

        // Store reference to solver
        this.solver = solver;
        this.ID = ID;
        this.argList = args;
        //this.argString = "[" + argList.get(0) + "]";
        this.op = Operation.PREDICATE;
    }

    public InvConstraintContains(int ID, Solver_Inverse<T> solver, List<Integer> args, int base, int input) {

        // Store reference to solver
        this.solver = solver;
        this.ID = ID;
        this.argList = args;
        //this.argString = "[" + argList.get(0) + "]";
        this.op = Operation.PREDICATE;
        this.nextID = base;
    }


    @Override
    public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex) {

        System.out.format("\nEVALUATE PREDICATE %d ...\n", ID);
        //T predicateResult = solver.getSymbolicModel(ID);
        T predicateResult = solver.getSymbolicModel(nextConstraint.getID());

        if (!predicateResult.isEmpty()) {

            // place symbolic string from solver string table into output set, position 1
            outputSet.put(1, predicateResult);

            // call evaluate on the next constraint, source is this inverse constraint
            // returning false from here means we have an error, as the predicate must
            // be satisfiable.
            return nextConstraint.evaluate(this, 1);
        }

        System.out.println("ERROR: Predicate has no forward results, UNSAT");
        return false;

    }

    @Override
    public Tuple<Boolean, Boolean> evaluate() {
        printDebug("EVALUATE PREDICATE " + ID + " ...");

        // nps - 8.9.24 - isEmpty predicate is not handled. also it seems like result is not checked?
        // i think there is potentially only contains and isEmpty predicates other than equals which is handled seperately

//		System.out.format("\nBFS EVALUATE PREDICATE %d ...\n",ID);
        Tuple<Boolean, Boolean> ret = new Tuple<Boolean, Boolean>(true, true);
        //T predicateResult = solver.getSymbolicModel(ID);
        T predicateResult = solver.getSymbolicModel(nextConstraint.getID()); // forward result from target constraint
//	System.out.println("solver " + solver);
        //System.out.println("nextConstr " + nextConstraint.getID());
        //System.out.println("predicate " + predicateResult.getFiniteStrings());

        if (!predicateResult.isEmpty()) {

            // nps - results handled in forward prop for isEmptu
//            if (argID == -1) { //isEmpty predicate
//                T emptyString = this.solver.modelManager.createString("");
//                if (result) {
//                    T isEmpty = predicateResult.intersect(emptyString);
//                    outputSet.put(1, isEmpty); //
//                } else {
//                    T notEmpty = this.solver.modelManager.createAnyString(predicateResult.getBoundLength());
//                    notEmpty.minus(emptyString);
//                    notEmpty = notEmpty.intersect(predicateResult);
//                    outputSet.put(1, notEmpty);
//                }
//            } else { // two arguments so contains predicate
                //predicate result would go with non-concrete constratin, which
                //could be target or a source
                int indxSymb = 1;
                int indxConcr = 2;
//                if (nextConstraint.getOp() == Operation.INIT_CON) {
//                    indxSymb = 2;
//                    indxConcr = 1;
//                }
                printDebug("arg " + (argConstraint == null ? null : argConstraint.getOp())); // arg null for isEmpty for example
                printDebug("oper " + nextConstraint.getOp());
//		System.out.println("arg " + (this.argConstraint==null? null : this.argConstraint.getOp()));
//		System.out.println("oper " + this.nextConstraint.getOp());
                // place symbolic string from solver string table into output set, position 1
                outputSet.put(indxSymb, predicateResult);
                outputSet.put(indxConcr, solver.getSymbolicModel(argID));//the argument for now is concrete, so whatever is coming from it
//            }
        } else {
//            printDebug("ERROR: Predicate has no forward results, UNSAT");
            System.err.println("ERROR: Predicate has no forward results, UNSAT");
            System.exit(1);
        }

        //predicate always get its results nothing has left to try
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
