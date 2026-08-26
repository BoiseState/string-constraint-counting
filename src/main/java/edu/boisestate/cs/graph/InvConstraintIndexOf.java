package edu.boisestate.cs.graph;

import dk.brics.automaton.Automaton;
import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

import java.util.HashMap;
import java.util.List;

public class InvConstraintIndexOf extends A_Inv_Constraint {
	private int index = -1; // not to be confused with \uffff that we use for not found even though java would return -1

	public InvConstraintIndexOf(int ID, Solver_Inverse solver, List<Integer> args) {
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		this.op = Operation.INDEX_OF;
		this.outputSet = new HashMap<Integer, Model_Acyclic_Inverse>();
		this.solutionSet = new SolutionSetInternal(ID);
		this.argString = "0:INDEX";
	}

	@Override
	public Tuple<Boolean, Boolean> evaluate() {
		Tuple<Boolean, Boolean> ret = new Tuple<>(true, true);
		printDebug("EVALUATE INDEX OF " + ID + " ...");
		if (index == -1) {
			// init
			Model_Acyclic_Inverse inputs = incoming();
			// technically can work with range of indices and not found option
			// but for now we pin to an index and hope for the best
			if (inputs.getAutomatonObject().getInitialState().getTransitions().isEmpty()) {
				printDebug("INDEX OF INCOMING INCONSISTENT");
				return new Tuple<>(false, true);
			}
			index = (int) inputs.getAutomatonObject().getInitialState().getTransitions().iterator().next().getMin();
			index-= 48; // ascii offset
			if (index > 65000) {
				// treat as not found
				index = -1;
			}
		}
		printDebug("INDEX OF INCOMING: " + index);

		// calls the solver_inverse method which calls the model_acyclic method
		Model_Acyclic_Inverse findOriginal = solver.getSymbolicModel(argID);
		Model_Acyclic_Inverse searchOriginal = solver.getSymbolicModel(nextID);
		Model_Acyclic_Inverse findModel = findOriginal.clone();
		Model_Acyclic_Inverse searchModel = searchOriginal.clone();
//            T inputsOrig = inputs.clone();
		// a find choice is made and set in findModel
		// res is constructed based on that
		Model_Acyclic_Inverse resModel = solver.inv_indexOf(searchModel, findModel, index);

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

			// similarly we want to check the indexRange/inputs and see if we have exhausted those possibilities.
			// note i think/hope we shuold have exhausted the specific index search as we don't make choices about hte restul other than based on the find model
			// however we can't adjust inputs itself for reevaluation, we will need ot grab the prevconstraint output that is responsible
//			inputsOrig.minus(inputs);

//			((A_Inv_Constraint<T>) this.prevConstraint.iterator().next()).setOutput(this, inputs); // this is the prev constraint that is responsible for the inputs
			if (!findAutOG.isEmpty()) {
				ret = new Tuple<>(true, false);
			}
			//otherwise we've exhausted our search
		}

		return ret;
	}
}
