/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.util.Tuple;

/**
 * @author Marlin Roberts, 2020-2021
 *
 */
public class InvConstraintEquals<T extends A_Model_Inverse<T>> extends A_Inv_Constraint<T> {

	// This will hold a reference to the containing solver.
	// This allows the constraint access to the solver functions and string tables.
	//private Solver_Inverse<T> solver;

	//private int ID;

	//private I_Inv_Constraint prevConstraint;
	//private I_Inv_Constraint nextConstraint;

	//private Operation op;

	//private List<Integer> argList;

	//private String argString;

	//to keep the initial value
	private T inputs = null;

	//the result of equals to evaluate to: true or false;
	private boolean result;

	//optimization for two level partition inverse
	private boolean optim = true;
	int partition = 0;
	//set up a couple of data structures
	//one model maps to a list of outputs
	//when
	private Map<T, List<Tuple<T,T>>> mapInOut = null;


	public InvConstraintEquals (int ID, Solver_Inverse<T> solver, boolean  result) {

		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		//this.argID = argID;
		this.outputSet = new HashMap<Integer,T>();
		//this.argString = "[" + argList.get(0) + "]";
		this.op = Operation.EQUALS;
		this.result = result;
	}


	public InvConstraintEquals (int ID, Solver_Inverse<T> solver, int argID) {

		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argID = argID;
		this.outputSet = new HashMap<Integer,T>();
		//this.argString = "[" + argList.get(0) + "]";
		this.op = Operation.EQUALS;

	}

	public InvConstraintEquals (int ID, Solver_Inverse<T> solver, List<Integer> args) {

		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		//this.argString = "[" + argList.get(0) + "]";
		this.op = Operation.EQUALS;
	}

	public InvConstraintEquals (int ID, Solver_Inverse<T> solver, List<Integer> args, int base, int input) {

		// Store reference to solver
		this.solver = solver;
		this.ID = ID;
		this.argList = args;
		//this.argString = "[" + argList.get(0) + "]";
		this.op = Operation.EQUALS;
		this.nextID = base;
	}


	@Override
	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex) {

		System.out.format("\nEVALUATE PREDICATE %d ...\n",ID);
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
		printDebug("EVALUATE EQUALS PREDICATE " + ID + " ...");
//		System.out.format("\nBFS EVALUATE PREDICATE %d ...\n",ID);
		Tuple<Boolean, Boolean>  ret = new Tuple<Boolean,Boolean>(true, true);
		//9-15-23 handling two symbolic values
		//requires ensuring consistency between values
		//propagated up to the children of the predicate
		//thus only output the matching pair and do similar tracking
		//as in symbolic concat

		// nps: 9-25-25 - stupid edge case for duplicate edges:
		if (this.argConstraint == null || this.nextConstraint == null) {
			printDebug("EVALUATE EQUALS PREDICATE " + ID + " is unary ...");
			int id = this.argConstraint == null ? this.nextID : this.argID;
			outputSet.put(1, solver.getSymbolicModel(nextID));
			outputSet.put(2, solver.getSymbolicModel(argID));
			return ret; // would have returned false if not equals
		}

		//first time around inputs have not been initialize
		if(inputs == null) {
			printDebug("original is null");
//			System.out.println("original is null");
			inputs = solver.getSymbolicModel(nextConstraint.getID()).clone();
			if (inputs == null) {
				System.err.println("No forward model for source " + nextConstraint.getID());
				System.exit(1);
			}
		}

		//T argResult = solver.getSymbolicModel(argConstraint.getID()).clone();
		//System.out.println("arg " + argResult.getFiniteStrings());
		//T predicateResult = solver.getSymbolicModel(nextConstraint.getID());
		//	System.out.println("solver " + solver);
		//System.out.println("nextConstr " + nextConstraint.getID());
		//System.out.println("predicate " + inputs.getFiniteStrings());

		//eas the second set of disjunction is for the optimization
		if ( !inputs.isEmpty() || (mapInOut == null || !mapInOut.isEmpty())) {



			//different cases: 
			//if one of them is a concrete, the solver
			//already did the narrowing and the symbolic part should match the result (true/false)
			//so it's ok to pass an entire set up
			//better query for a singleton solutions set
			if(this.argConstraint.getOp() == Operation.INIT_CON || this.nextConstraint.getOp() == Operation.INIT_CON) {
				//predicate result would go with non-concrete constraint, which
				//could be target or a source
				int indxSymb = 1;
				int indxConcr = 2;
				// nps - 8/9/24 - some real benches have predicates on concretes....
				if (this.argConstraint.getOp() == Operation.INIT_CON && this.nextConstraint.getOp() == Operation.INIT_CON) {
					// just give outputs whatever the inputs are.... trivial
					outputSet.put(1, solver.getSymbolicModel(nextID));
					outputSet.put(2, solver.getSymbolicModel(argID));
					return new Tuple<Boolean, Boolean>(true, true);
				}

				if(this.nextConstraint.getOp() == Operation.INIT_CON){
					indxSymb = 2;
					indxConcr = 1;
					if (this.argConstraint.getOp() == Operation.LENGTH){
						// parse concrete value and propogate back valid model
						String concrStr = solver.getSymbolicModel(nextID).getShortestExampleString();
						int len = Integer.parseInt(concrStr);
						T lenModel = solver.modelManager.createAnyString(len,len);
						if (!result) {
							T all = solver.modelManager.createAnyString();
							all.minus(lenModel);
							lenModel = all;
						}
						inputs = lenModel;
					}
				} else { //arg is the one const
					if (this.nextConstraint.getOp() == Operation.LENGTH){
						// parse concrete value and propogate back valid model
						String concrStr = solver.getSymbolicModel(argID).getShortestExampleString();
						int len = Integer.parseInt(concrStr);
						T lenModel = solver.modelManager.createAnyString(len,len);
						if (!result) {
							T all = solver.modelManager.createAnyString();
							all.minus(lenModel);
							lenModel = all;
						}
						inputs = lenModel;
					}
				}
				//System.out.println("arg " + (this.argConstraint==null? null : this.argConstraint.getOp()));
				//System.out.println("oper " + this.nextConstraint.getOp());
				// place symbolic string from solver string table into output set, position 1
				//because the second argument is concrete inputs will always has one values
				//thus there is no need to intersect here.
				outputSet.put(indxSymb, inputs);
				outputSet.put(indxConcr, solver.getSymbolicModel(argID));//the argument for now is concrete, so whatever is coming from it
			} else {
				//none of them are concrete need to ensure consistency
				//should be operator dependent
				printDebug("Two Symbolic " + this.getOp());
//				System.out.println("Two Symbolic " + this.getOp());
				if(result) {
					//expected result is true
					//cannot do any optimization since it is relational
					T input = inputs.getShortestExampleModel();
//					System.out.println("Solution " + input.getFiniteStrings());
					//remove it from the inputs
					inputs.minus(input);
					//because of the equality operator perform intersection
					//of both automata there is no need to do intersection,
					//such narrowing already ensures that input is in both models.
					//set it to both outputs
					outputSet.put(1, input);
					outputSet.put(2, input);
					if(!inputs.isEmpty()) {
						//adding to backtracking
						ret = new Tuple<Boolean,Boolean>(true, false);
					}
				} else {
					//expected result is false
					printDebug("Not equals");
//					System.out.println("Not equals");

					//argument model
					T input2 = solver.getSymbolicModel(argConstraint.getID()).clone();
					//if both have no common strings: intersection is empty
//					boolean common = !solver.intersect(inputs, argID).isEmpty();
//					printDebug("Target and args have common strings? " + common);
//					System.out.println("Target and args have common strings? " + common);

					if(optim && partition != 2) {

						//in optimized version we create 3 partitions/cases
						//we have t (inputs) and s (input2) values that for
						//not equals remain the same, no narrowing happened
						//partiton0 considers the case when we propagate back the entire t for the target and s \ t cap s for the argument, i.e., without common elements between t and s
						//parition1 is symmetric: t \ t cap s and the entire s
						//parition2 does the brute force algorithm: el \in t and s\{el}, i.e., what the default implementation is doing
						//the partitions and their set of inverses (for partition2, partitions 0 and 1 only have single inverses) are not created until the previous one propagated backwards has failed.

						if(partition == 0 /*mapInOut == null*/) {
							//create partition of different subsets
							T input1;
							//output for a given subset of inputs
							List<Tuple<T,T>> currOutput = new ArrayList<Tuple<T,T>>();

							mapInOut = new HashMap<T, List<Tuple<T,T>>>();
							//the first partition considers an entire target
							//so no need to remove input1 from inputs
							//case 1 the common string are in the first but not in the second
							input1 = inputs.clone();
							T input2Copy = input2.clone();
							input2Copy.minus(input1);
							if(!input2Copy.isEmpty()) {
								currOutput.add(new Tuple(input1, input2Copy ));
//								System.out.println("input1 " + input1.getFiniteStrings());
//								System.out.println("inpu12Copy " + input2Copy.getFiniteStrings());
								mapInOut.put(input1, currOutput);
							}
							printDebug("map1 : " /* + mapInOut*/);
							if(mapInOut.isEmpty()) {
								partition++;
							}
							//case 2 the common strings are in the second but not in the frist
						}

						if (partition == 1) {
							List<Tuple<T,T>> currOutput = new ArrayList<Tuple<T,T>>();
							T input1 = inputs.clone();
							T input1Copy = input1.clone();
							input1Copy.minus(input2);
							if(!input1Copy.isEmpty()) {
								currOutput = new ArrayList<Tuple<T,T>>();
								currOutput.add(new Tuple(input1Copy, input2 ));
//								System.out.println("input1Copy " + input1Copy.getFiniteStrings());
//								System.out.println("inpu2 " + input2.getFiniteStrings());
								mapInOut.put(input1Copy, currOutput);
							}
							printDebug("map2 : " /* + mapInOut*/);

							//case 3 intersection
							//when a string is present in both sets
							//this what should be left after removing
							//
							if(mapInOut.isEmpty()) {
							inputs.minus(input1Copy);
							partition++;
							}

						}

						// if (partition == 3) {
//							input1 = inputs.clone();
//
//							//creating bunch of tuples
//							//need to do it on the fly later
//							currOutput = new ArrayList<Tuple<T,T>>();
//
//							//remove each pair and propagate up
//							//input is empty
//							while(!inputs.isEmpty()) {
//								input1Copy = inputs.getShortestExampleModel();
//								System.out.println(input1Copy.getAcceptedStringExample());
//								inputs.minus(input1Copy);
//								input2Copy = input2.clone();
//								input2Copy.minus(input1Copy);
//								if(!input2Copy.isEmpty()) {
//									currOutput.add(new Tuple(input1Copy, input2Copy));
////									System.out.println("input1Copy " + input1Copy.getFiniteStrings());
////									System.out.println("inpu2Copy " + input2Copy.getFiniteStrings());
//								}
//							}
//							if(!currOutput.isEmpty()) {
//								mapInOut.put(input1, currOutput);
//							}

						//	System.out.println("Should not gete there");
					//		System.out.println("map3 : " /*+ mapInOut*/);

//						}

						if(partition !=2 ) {

						//if map was not null and has become one,
						//then process the first value

						Entry<T, List<Tuple<T,T>>> entry = mapInOut.entrySet().iterator().next();

						Tuple<T,T> values = entry.getValue().get(0);

						outputSet.put(1, values.get1());
						outputSet.put(2, values.get2());
//						System.out.println("ou1 " + values.get1().getFiniteStrings());
//						System.out.println("ou2 " + values.get2().getFiniteStrings());

						entry.getValue().remove(0);
						if(entry.getValue().isEmpty()) {
							//remove the processed element
							mapInOut.remove(entry.getKey());
						}

						//if(!mapInOut.isEmpty()) {
							//backtrack to me
							ret = new Tuple<Boolean, Boolean>(true, false);//continue and add to backtrack since there are more inputs
						//}
						//else there is no values left, can go back to me but then UNSAT will thrown
							partition++;


					 }
					}
					if (!optim || partition == 2) {

						//if both have no common strings: intersection is empty
						boolean common = !solver.intersect(inputs, argID).isEmpty();
						printDebug("Target and args have common strings? " + common);

						//if no common string then propagate them both up
						if(!common) { //turn back to !common after done debugging
							outputSet.put(1, inputs);
							outputSet.put(2, input2);
							//return the default true, true
							//no backtracking here back
						} else {



							//TOD: an optimization where inputs, over which we iterate
							//single values has the smallest number of strings in
							//its example set.

							T input1 = inputs.getShortestExampleModel();
							//remove it from the set
							inputs.minus(input1);
							//create a copy of the full model

							//leave the set of strings to which input1 is not equal to.
							input2.minus(input1);
							//no need to do intersection the incoming values are actually ones
							//from the nodes themselves, and not over-approximating computations.

							//try a different concrete string and find all string for arg
							//that are not equal to that string, and propagate up.
							//the loop considers a case when input1 is larger than input 2
							// and removing input1 from input2 would make input2 empty,
							//e.g., input 1 = {aa,ab} and input2 = {aa}
							while (input2.isEmpty()) {
								input1 = inputs.getShortestExampleModel();
								inputs.minus(input1);
								input2 = solver.getSymbolicModel(argConstraint.getID()).clone();
								input2.minus(input1);
							}

													printDebug("target: " + input1.getFiniteStrings() + "\targ: " +
															input2.getShortestExampleString());
							//send them up
							outputSet.put(1, input1);
							outputSet.put(2, input2);
							if(!inputs.isEmpty()) {
								//adding to backtracking
								ret = new Tuple<Boolean,Boolean>(true, false);
							}
						}
					}
				}
			}

		} else {
			System.err.println("ERROR Equals hs no forward results, UNSAT");
			System.exit(1);
		}

		//eas: depends on the type of the query and arguments it might needs to backtrack
		printDebug("ret in eq " + ret);
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
