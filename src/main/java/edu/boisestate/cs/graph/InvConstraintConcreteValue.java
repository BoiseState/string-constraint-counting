/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.*;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
//import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;


/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintConcreteValue<T extends A_Model_Inverse<T>>  extends A_Inv_Constraint<T> {

	
	//private SolutionSet<T> solutionSet;
	
	//private int solutionIndex = -1;
	
	
	public InvConstraintConcreteValue (int ID, Solver_Inverse<T> solver) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.outputSet = new HashMap<Integer,T>();
		this.solutionSet = new SolutionSetInternal<T>(ID);
		this.op  = Operation.INIT_CON;
		this.argString = "[NONE]";

	}
	
	
//	public InvConstraintConcreteValue (int ID, Solver_Inverse<T> solver, List<Integer> args, SolutionSet<T> solutionSet) {
//
//		// Store reference to solver
//		this.solver = solver;
//		this.ID = ID;
//		this.argList = args;
//		this.op  = Operation.INIT_CON;
//		this.argString = "[NONE]";
//		//this.solutionSet = solutionSet;
//	}
//
//	public InvConstraintConcreteValue (int ID, Solver_Inverse<T> solver, List<Integer> args, SolutionSet<T> solutionSet, int base, int input) {
//
//		// Store reference to solver
//		this.solver = solver;
//		this.ID = ID;
//		this.argList = args;
//		this.op  = Operation.INIT_CON;
//		this.argString = "[NONE]";
//		//this.solutionSet = solutionSet;
//		this.nextID = base;
//		this.prevIDs = new HashSet<Integer>(); prevIDs.add(input);
//		solver.duplicateString(args.get(0), ID);
//	}
	
	
	@Override
	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex)  {
		
		System.out.format("EVALUATE CONCRETE VALUE %d ...\n",ID);
		
		T concrete = solver.getSymbolicModel(ID);
		String test = concrete.getShortestExampleString();
		T output = inputConstraint.output(sourceIndex);
		if (output.containsString(test)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Tuple<Boolean, Boolean> evaluate() {
		printDebug("EVALUATE CONCRETE VALUE " + ID + "...");
//		System.out.format("EVALUATE CONCRETE VALUE %d ...\n",ID);
		
		T concrete = solver.getSymbolicModel(ID);
		String test = concrete.getShortestExampleString();
		Iterator<I_Inv_Constraint<T>> iter = (new ArrayList<>(prevConstraint)).iterator();
		I_Inv_Constraint<T> prev = iter.next();
		//System.out.println("prev " + prev);
		// TODO: we don't use replaceCC anymore, so can almost def remove
		// this was def to fix a specific bug, hopefully not relevant anymore
		while (prev.getOp() == Operation.REPLACE_CHAR_CHAR) {
			prev = iter.next();
		}

		T inputs = prev.output(this);
		
		while(iter.hasNext()) {
			I_Inv_Constraint<T> nextC = iter.next();
			// issue occurs because of way replaceCC outputSet is handled.
			if (nextC.getOp() == Operation.REPLACE_CHAR_CHAR) continue;
			T next = nextC.output(this);
			if (next == null) {
				System.err.println("NO VALUE FOR CONCRETE EXISTS " + ID );
				System.exit(1);
			}
			inputs = inputs.intersect(next);
			if (inputs == null || inputs.isEmpty()) {
				System.err.println("NO VALUE FOR CONCRETE EXISTS " + ID );
				System.exit(1);
			}
		}

		//eas: sanity check mare sure the inputs is the
		//actual concrete value - add || inputs.getFiniteStrings().size() != 1

		if (!inputs.containsString(test)) {
			System.err.println("ERROR IN EVALUATE CONCRETE VALUE " + ID + "...");
			System.out.println("inputs model: " + inputs);
			System.out.println("input strings: " + inputs.getFiniteStrings(100));
			System.out.println("concrete value: " + test);
			System.exit(1);
		}
		return new Tuple<Boolean, Boolean>(true, true);
	}



}
