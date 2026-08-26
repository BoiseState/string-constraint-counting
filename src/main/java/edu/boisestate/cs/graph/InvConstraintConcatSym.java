/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintConcatSym extends A_Inv_Constraint {

	//	private I_Inv_Constraint suffixConstraint;
	//	private int suffixID;
//		private boolean initialized = false;
	//this in not for BFS as Marlin done origianlly
	private List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>> outputs;
	//	private T inputModel;
	// private int input, arg, base;
	private Model_Acyclic_Inverse inputs = null;
	//for BFS we need a map: input to outputs and does not use outputs
	private Map<Model_Acyclic_Inverse, List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>> mapInOut = new HashMap<Model_Acyclic_Inverse, List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>>();

	public InvConstraintConcatSym (int ID, Solver_Inverse solver) {

		// assignments to class variables in the abstract class A_Inv_Constraint
		this.solver = solver;
		this.ID = ID;
		//this.argList = args;
		this.op = Operation.CONCAT_SYM;
		//this.argString = "[" + argList.get(0) + "] SUFFIX";
		this.outputSet = new HashMap<Integer,Model_Acyclic_Inverse>();
		// assignments to class variables in InvConstraintConcatSymPref
		//this.suffixConstraint = suffixConstraint;
		//this.suffixID = suffixConstraint.getID();
		this.outputs = new ArrayList<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>();
	}



	@Override
	public boolean evaluate(I_Inv_Constraint inputConstraint, int sourceIndex) {

		evaluateCount++;

		printDebug("EVALUATE CONCAT " + ID + " ...");
		Model_Acyclic_Inverse inputs = inputConstraint.output(sourceIndex);
		//List<Tuple<T,T>> outputs;

		while (!inputs.isEmpty()) {

			Model_Acyclic_Inverse input = inputs.getShortestExampleModel();
			inputs.minus(input);

			newConcatChoiceCount++;

			Model_Acyclic_Inverse nextModel = solver.getSymbolicModel(nextConstraint.getID());
			Model_Acyclic_Inverse argModel = solver.getSymbolicModel(argConstraint.getID());

			outputs = input.inv_concatenate_sym_set(nextModel, argModel);

			for (Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse> t : outputs) {
				printDebug("RCVD: P " + t.get1().getShortestExampleString() + "\t S " + t.get2().getShortestExampleString());
//				System.out.format("RCVD:  P %4s  S %4s\n", t.get1().getShortestExampleString(),t.get2().getShortestExampleString());
			}

			while (!outputs.isEmpty()) {

				Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse> split = outputs.remove(0);

				Model_Acyclic_Inverse prefix = split.get1();
				Model_Acyclic_Inverse suffix = split.get2();

				outputSet.put(1, prefix);
				outputSet.put(2, suffix);

				printDebug("CHOSE: P " + prefix.getShortestExampleString() + "\t S " + suffix.getShortestExampleString());
//				System.out.format("CHOSE: P %4s  S %4s\n", prefix.getShortestExampleString(), suffix.getShortestExampleString());

				newSplitOutputCount++;

				if (argConstraint.evaluate(this, 2)) {

					if (nextConstraint.evaluate(this, 1)) {

						return true;
					}

				}

			} // continue until suffix / prefix combinations are exhausted

		} // continue until all inputs are exhausted

		// catastrophic fail 
		return false;

	}

	@Override
	public void clear() {
		super.clear();
		inputs = null;
		mapInOut.clear();
//		initialized = false;
	}

	@Override
	public Tuple<Boolean, Boolean> evaluate() {
		Tuple<Boolean, Boolean> ret = new Tuple<Boolean, Boolean>(true, true); //continue and don't add to backtrack
		//compute the intersection of all incoming values
		boolean ostrich = true;

//		if (!initialized) {
//			printDebug("INITIAL EVAL OF CONCAT " + ID + " ...");
//			initialized = true;
//			inputs = incoming();
//		}
		if(inputs == null) { // first time processing (or after clear)
			printDebug("inputs is null");
			//the first time the node is evaluated
			//do the intersection
			inputs = incoming();
		}
		if(inputs.isEmpty()) {
			printDebug("CONCAT SYMV INCOMING SET INCONSISTENT...");
			ret = new Tuple<Boolean,Boolean>(false, true);
		} else {

			//System.out.println("inputs " + inputs.getFiniteStrings());
			//remove one example from the inputs
			Model_Acyclic_Inverse input = ostrich? inputs : inputs.getShortestModel();
//			System.out.println("input " + input.getFiniteStrings() + " hash " + input.hashCode());
			List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>> currOutput = new ArrayList<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>();
			//equals is implemented between two automata, but
			//the hash functions is not, so in order to use hash map
			//we just find the equal object use that's object hash value.
			for(Model_Acyclic_Inverse in : mapInOut.keySet()) {
				if(in.equals(input)) {
					input = in;
					break;
				}
			}
			printDebug("mapInOut " + mapInOut.containsKey(input));


			if(mapInOut.containsKey(input)) {
				//already computed outputs before just get the next value
				currOutput = mapInOut.get(input);
				printDebug("Processed trying new valus");
			} else {
				//compute it fresh and add to the map
				//compute outgoing solutions
				Model_Acyclic_Inverse nextModel = solver.getSymbolicModel(nextConstraint.getID());
				Model_Acyclic_Inverse argModel = solver.getSymbolicModel(argConstraint.getID());

				if (nextModel.isSingleton() && argModel.isSingleton()) { // inputs is also singleton
					currOutput = new ArrayList<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>();
					Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse> t = new Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>(nextModel, argModel);
					currOutput.add(t);
					mapInOut.put(input, currOutput);
					printDebug("Both next and arg are singleton: propagating");
				} else {
			
				if(ostrich) {
					//Make two copies of inputs
					//the algorithm from ostrich paper POPL'19
					currOutput = inputs.inv_concatenate_sym_all(nextModel, argModel);
					if(currOutput.isEmpty()) {
						//backtrack to previous nodes, don't add to backtrack, no more inputs are left here
						return new Tuple<Boolean, Boolean>(false, true);
					}
					mapInOut.put(inputs, currOutput);

				} else {
					//compute it fresh and add to the map
					//compute outgoing solutions
					//					T nextModel = solver.getSymbolicModel(nextConstraint.getID());
					//					T argModel = solver.getSymbolicModel(argConstraint.getID());
					//			System.out.println("input " + input.getFiniteStrings());
					//			System.out.println("nextM " + nextModel.getFiniteStrings());
					//			System.out.println("argM " + argModel.getFiniteStrings());
					//let's try this input
					currOutput = input.inv_concatenate_sym_set(nextModel, argModel);
					//if not a single split is produced then try another if some inputs are left
					printDebug("currOutput " + currOutput);
					while(currOutput.isEmpty()) {
						//more inputs left?
						inputs.minus(input);
						printDebug("inputs empty " + inputs.isEmpty());
						if(inputs.isEmpty()) {
							//backtrack to previous nodes, don't add to backtrack, no more inputs are left here
							return new Tuple<Boolean, Boolean>(false, true);
						}
						//try them that
						input = inputs.getShortestModel();
						currOutput = input.inv_concatenate_sym_set(nextModel, argModel);
					}
					mapInOut.put(input, currOutput);

				}}



			}

//			for (Tuple<T,T> t : currOutput) {
//				//System.out.format("RCVD1:  P %4s  S %4s\n", t.get1().getShortestExampleString(),t.get2().getShortestExampleString());
//				System.out.format("RCVD: P " + t.get1().getFiniteStrings() + "\t S " + t.get2().getFiniteStrings() + "\n");
//
//			}

			//recomputing new outgoing values and updating the 
			//set of choices
			Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse> split = currOutput.remove(0);

			Model_Acyclic_Inverse prefix = split.get1();
			Model_Acyclic_Inverse suffix = split.get2();

			// we need to get all possible suffixes with the same prefix and then intersect them.
			//Step 1: create prefix*
			//T newPrefix = prefix.concatenate( /* with * of the max length of the input model*/ suffix);

			outputSet.put(1, prefix);
			outputSet.put(2, suffix);

			//if there are more choices left in currOutput then 
			//backtrack to it
			if(!currOutput.isEmpty()) {
				ret = new Tuple<Boolean, Boolean>(true, false);//continue and add to backtrack
			} else {
				// nps - 9.2.24 - do not add to backtrack map, all the outputs have been processed

				//probably don't need to do mapinout stuff here but leaving for now
				//TODO: remove this
//				inputs.minus(input);
				mapInOut.remove(input);
//				//check if more input left
				if (!mapInOut.isEmpty()) {
					inputs = mapInOut.keySet().iterator().next();
				} else {
					inputs = null;
				}
				//if no more inputs left then don't add to backtrack
				ret = new Tuple<Boolean, Boolean>(true, true);// don't add to backtrack cause this is last output
			}

			//System.out.format("CHOSE: P %4s  S %4s\n", prefix.getShortestExampleString(), suffix.getShortestExampleString());
//			System.out.format("CHOSE: P " + prefix.getFiniteStrings() + "\t S " + prefix.getFiniteStrings() + "\n");
		}


		return ret;
	}


//	public boolean inputsEmpty() {
//		return inputs == null;
//	}

}
