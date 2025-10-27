/**
 * 
 */
package edu.boisestate.cs.graph;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
//import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;


/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintInput<T extends A_Model_Inverse<T>>  extends A_Inv_Constraint<T> {

	
	//private SolutionSet<T> solutionSet;
	
	//private int solutionIndex = -1;
	
	
	public InvConstraintInput (int ID, Solver_Inverse<T> solver) {
		
		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.outputSet = new HashMap<Integer,T>();
		this.solutionSet = new SolutionSetInternal<T>(ID);
		this.op  = Operation.INIT_SYM;
		this.argString = "[NONE]";

	}
	
	
//	public InvConstraintInput (int ID, Solver_Inverse<T> solver, List<Integer> args, SolutionSet<T> solutionSet) {
//
//		// Store reference to solver
//		this.solver = solver;
//		this.ID = ID;
//		this.argList = args;
//		this.op  = Operation.INPUT;
//		this.argString = "[NONE]";
//		//this.solutionSet = solutionSet;
//	}
//
//	public InvConstraintInput (int ID, Solver_Inverse<T> solver, List<Integer> args, SolutionSet<T> solutionSet, int base, int input) {
//
//		// Store reference to solver
//		this.solver = solver;
//		this.ID = ID;
//		this.argList = args;
//		this.op  = Operation.INPUT;
//		this.argString = "[NONE]";
//		//this.solutionSet = solutionSet;
//		this.nextID = base;
//		this.prevIDs = new HashSet<Integer>(); prevIDs.add(input);
//		solver.duplicateString(args.get(0), ID);
//		solver.duplicateString(args.get(0), ID);
//	}
	
	
	@Override
	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex)  {
		
		//T example = solver.getSymbolicModel(this.prevConstraint.getID());
		System.out.format("EVALUATE INPUT %d ...\n",ID);
		T solution = inputConstraint.output(sourceIndex);
		
		if (false) {
			System.out.print("DEBUG " + op.toString() + " " + ID);
			System.out.print(" solutions .... ");
			
    		BigInteger oneHundred = new BigInteger("300");
    		
    		if (solution.modelCount().compareTo(oneHundred) > 0) {
    			System.out.print("Too many values to output,  " + solution.modelCount() + "  example: ");
    			System.out.println(solution.getShortestExampleString());
    		} else {
//    			for (String s : example.getFiniteStrings()) {
//    				System.out.print(s + " ");
//    			}
    		System.out.println();
    		}			
			
			
//			for (String s : example.getFiniteStrings()) {
//				System.out.print(s + " ");
//			}
//			System.out.println();
		
		
		}
		
//		if (solutionIndex == -1) {
//			//solutionIndex = solutionSet.add(example);
//		} else { 
//			//solutionSet.set(example, solutionIndex);
//		}
		
//		System.out.format("SETTING INPUT %d INCOMING EDGE %d\n", ID, inputConstraint.getID());
//		solutionSet.setSolution(inputConstraint.getID(), example);
//
//		if (!solutionSet.isConsistent()) {
//			System.out.println("      example set " + this.ID + " not consistent, falling back ...");
//			solutionSet.remSolution(inputConstraint.getID());
//			return false;
//		}
		
		//if (debug) {
			System.out.println("      example set " + ID + " consistent ...");
		//}
		
		return true;
	}
	
	@Override
	public Tuple<Boolean, Boolean> evaluate() {
		Tuple<Boolean, Boolean>  ret = new Tuple<Boolean, Boolean>(true, true) ; //don't backtrack
		printDebug("EVALUATE INPUT " + ID + "...");
//		System.out.format("EVALUATE INPUT %d ...\n",ID);
//		Iterator<I_Inv_Constraint<T>> iter = prevConstraint.iterator();
//		I_Inv_Constraint<T> nextC = iter.next();
//		System.out.println("nextInput " + nextC);
//		T inputs = nextC.output(this);
//		System.out.println("prevConstraint " +(inputs==null? null : inputs.getFiniteStrings()));
//		while(iter.hasNext()) {
//			nextC = iter.next();
//			System.out.println("nextInput " + nextC);
//				T nextInput = nextC.output(this);
//				inputs = inputs.intersect(nextInput);
//		}
		T inputs = incoming();
		
		//if consistent
		if(!inputs.isEmpty()) {
			printDebug("DEBUG " + op.toString() + " " + ID);
			printDebug("Current SOLUTION: " + inputs.getShortestExampleString());

			//TODO: should/could forward prop here
//			suggest(inputs);

//    		BigInteger oneHundred = new BigInteger("300");
//
//    		if (inputs.modelCount().compareTo(oneHundred) > 0) {
//    			System.out.print("Too many values to output,  " + inputs.modelCount() + "  example: ");
//    			System.out.println(inputs.getShortestExampleString());
//    		} else {
////    			for (String s : inputs.getFiniteStrings()) {
////    				System.out.print(s + " ");
////    			}
//    		System.out.println();
//    		}

			printDebug("Setting new found example for input " + ID + " of " + inputs.getShortestExampleString());
			solutionSet.addSolution(inputs);

			if (!solutionSet.isConsistent()) {
				printDebug("Solution set " + ID + " not consistent, falling back ...");
				solutionSet.remSolution(inputs);
				ret = new Tuple<>(false, true); //backtrack as example for input for currentbackprop not consistent with a previous example
				// TODO: does not backtrack to previous run
			} else {
				this.outputSet.put(0, inputs); // used?
			}

		} else {
			//example not consistent, then backtrack
			// suggest solution to backtracked parents?
			ret = new Tuple<Boolean, Boolean> (false, true);
			printDebug("      example set " + this.ID + " not consistent, falling back ...");
//			System.out.println("      example set " + this.ID + " not consistent, falling back ...");
		}
		return ret;//never backtrack here
	}

	public T getComp() {
		return solutionSet.getComp();
	}

	public void clear() {
		super.clear();
		solutionSet.clear();
	}


}
