package edu.boisestate.cs.solvers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Stack;

import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;
import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse_Manager;
//import edu.boisestate.cs.graph.I_Inv_Constraint;
//import edu.boisestate.cs.graph.I_Inv_Constraint;
import edu.boisestate.cs.util.Quadruple;
import edu.boisestate.cs.util.Tuple;


public class Solver_Inverse extends Solver implements I_Solver_Inverse{

   // protected final A_Model_Manager<T> modelManager;
	
    protected Map<Integer, Model_Acyclic_Inverse> invStringMap = new HashMap<>();
    private boolean reduceToShortest = true;
    //private static int ARG_OFFSET = 1000;	// TODO: REMOVE symbolic arguments are stored in the symbolic string table with this offset
    //private Stack<I_Inv_Constraint> constraintStack; // REMOVE

    /**
     * Constructor with modelManager
     * @param modelManager
     */
    public Solver_Inverse(Model_Acyclic_Inverse_Manager modelManager) {
        super(modelManager);
        //constraintStack = new Stack<I_Inv_Constraint>(); // TODO: REMOVE
        // initialize factory from parameter
        //this.modelManager = modelManager;
    }

    /**
     * Constructor with modelManager and initialBound
     * @param modelManager
     * @param initialBound
     */
    public Solver_Inverse(Model_Acyclic_Inverse_Manager modelManager, int initialBound) {
        super(modelManager,initialBound);
        //constraintStack = new Stack<I_Inv_Constraint>(); // TODO: REMOVE
        // initialize factory from parameter
        //this.modelManager = modelManager;
    }

	@Override
	public void outputConcreteStrings() {

		for (Integer i : concreteStringMap.keySet()) {
			System.out.println("CS ID: " + i + "  " + concreteStringMap.get(i));
		}
		
	}

	@Override
	public Model_Acyclic_Inverse getModel(int id) {
		return symbolicStringMap.get(id);
	}

	public void outputSymbolicStrings() {
		for (Integer i : invStringMap.keySet()) {
			System.out.println("SS ID: " + i + "  " + invStringMap.get(i));
		}
		
	}

	public Model_Acyclic_Inverse getSymbolicModel (int id) {
		return invStringMap.get(id);
	}

//	@Override // TODO: REMOVE
//	public void inv_append(int id, int base, int arg, int start, int end) {
//		
//		
//	}

	/**
	 * r3 version returns Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 * 
	 * @param id - id of symbolic string location to store result
	 * @param input - symbolic string to use as input 
	 * @param arg - symbolic string to use as argument
	 */
	@Override
	public Model_Acyclic_Inverse inv_append(int id, int input, int arg) {
		
		Model_Acyclic_Inverse inputModel = invStringMap.get(input);
		Model_Acyclic_Inverse argModel = invStringMap.get(arg);
		Model_Acyclic_Inverse resModel = inputModel.inv_concatenate(argModel);
		//invStringMap.put(id, resModel);
		return resModel;
	}
	
	
	/**
	 * Performs base.inv_concatenate(arg), stores result as symbolic string id
	 * 
	 * 05/15/2020 MJR
	 * 
	 * @param id - id of symbolic string location to store result
	 * @param input - symbolic string to use as input 
	 * @param arg - symbolic string to use as argument
	 */
//	@Override // TODO: REMOVE
//	public void inv_append (int id, int input, int arg, int base) {
//		
//		T inputModel = invStringMap.get(input);
//		T argModel = invStringMap.get(arg);
//		T baseModel = invStringMap.get(base);
//		Tuple<T,T> results = inputModel.inv_concatenate_sym(baseModel, argModel);
//		//T resModel = inputModel.inv_concatenate(baseModel, argModel);
//		T resModel = results.get1();
//		invStringMap.put(id, resModel);
//		// we store the new arg value at a different location so we don't overwrite the original.
//		invStringMap.put(arg + ARG_OFFSET, results.get2());
//		
//	}

	@Override
	public void inv_contains(boolean result, int base, int arg) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void inv_deleteCharAt(int id, int base, int loc) {
		// TODO Auto-generated method stub
		
//	}

	/**
	 * R3 Version 
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_delete(Model_Acyclic_Inverse input, int start, int end) {
		
		//T baseModel = invStringMap.get(base);
		Model_Acyclic_Inverse resModel = input.inv_delete(start, end);
		//invStringMap.put(id, resultModel);
		return resModel;
		
	}

//	/**
//	 * R3 Version
//	 * Returns result T
//	 *
//	 * 03/18/2021 MJR
//	 */
//	@Override
//	public Quadruple<T,T,T,T> inv_insert(T input, T baseModel, T argModel, int start) {
//        return input.inv_insert(baseModel, argModel, start);
//
//	}

	@Override 
	public void inv_insert(int id, int base, int arg, int offset, int start, int end) {
		// TODO remove
		
	}

	@Override
	public void inv_replaceCharFindKnown(int id, int base, char find) {
		// TODO REPLACE with r3 model
		
	}

	/**
	 * R3 Version 
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_replaceCharKnown(Model_Acyclic_Inverse input, char find, char replace) {
		//T baseModel = invStringMap.get(base);
		//T resModel = baseModel.inv_replace(find, replace);
		//invStringMap.put(id, resModel);
		
		//T baseModel = invStringMap.get(base);
		Model_Acyclic_Inverse resModel = input.inv_replace(find, replace);
		//T resModel = input.inv_replace(replace, find);
		//invStringMap.put(id, resultModel);
		return resModel;
		
		
	}

	// calls the Model Acyclic method
	@Override
	public Model_Acyclic_Inverse inv_replaceFirst(Model_Acyclic_Inverse input, Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace) {
		Model_Acyclic_Inverse resModel = input.inv_replaceFirst(find, replace);
		return resModel;

	}

	@Override
	public Model_Acyclic_Inverse inv_replaceAll(Model_Acyclic_Inverse input, Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace) {
		Model_Acyclic_Inverse resModel = input.inv_replaceAll(find, replace);
		return resModel;
	}

	@Override
	public void inv_replaceCharReplaceKnown(int id, int base, char replace) {
		// TODO REPLACE with r3 model
		
	}

	@Override
	public void inv_replaceCharUnknown(int id, int base) {
		// TODO REPLACE with r3 model
		
	}

	@Override
	public String inv_replaceEscapes(String value) {
		// TODO REPLACE with r3 model
		return null;
	}

	@Override
	public void inv_replaceStrings(int id, int base, int argOne, int argTwo) {
		// TODO REPLACE with r3 model
		
	}

	
	/**
	 * R3 Version 
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 */
	@Override 
	public Model_Acyclic_Inverse inv_reverse(Model_Acyclic_Inverse input) {
		
		Model_Acyclic_Inverse resultModel = input.reverse();
		return resultModel;
		
	}

	@Override
	public void inv_setCharAt(int id, int base, int arg, int offset) {
		// TODO REPLACE with r3 model
		
	}

	@Override
	public void inv_setLength(int id, int base, int length) {
		// TODO REPLACE with r3 model
		
	}

	@Override // TODO: REMOVE
	public void inv_substring(int id, int base, int start) {

		
	}

	/**
	 * R3 Version 
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_substring(Model_Acyclic_Inverse input, int start, int end) {
		
		//T baseModel = invStringMap.get(base);
		Model_Acyclic_Inverse resModel = input.inv_substring(start, end);
		//invStringMap.put(id, resultModel);
		return resModel;
	}

	/**
	 * R3 Version 
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_substring(Model_Acyclic_Inverse input, int start) {
		
		//T baseModel = invStringMap.get(base);
		Model_Acyclic_Inverse resModel = input.inv_substring(start);
		//invStringMap.put(id, resultModel);
		return resModel;
	}
	
	/**
	 * R3 Version Performs base.inv_toLowercase
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/18/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_toLowerCase(Model_Acyclic_Inverse input) {
		
		//T baseModel = invStringMap.get(base);
		Model_Acyclic_Inverse resModel = input.inv_toLowercase();
		//invStringMap.put(id, resModel);
		return resModel;
	}

	/**
	 * R3 Version Performs base.inv_toUppercase
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 03/31/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_toUpperCase(Model_Acyclic_Inverse input) {
		
		//T baseModel = invStringMap.get(base);
		Model_Acyclic_Inverse resModel = input.inv_toUppercase();
		//invStringMap.put(id, resModel);
		return resModel;
		
	}

	/**
	 * R3 Version Performs base.inv_trim
	 * Returns result Model_Acyclic_Inverse
	 * 
	 * 06/21/2021 MJR
	 */
	@Override
	public Model_Acyclic_Inverse inv_trim(Model_Acyclic_Inverse input) {

		Model_Acyclic_Inverse resModel = input.inv_trim();
		return resModel;
		
	}
	
	
	public void showStrings(int id) {
		Model_Acyclic_Inverse baseModel = invStringMap.get(id);
		if (baseModel.getFiniteStrings() != null) {
//			for (String s : baseModel.getFiniteStrings()) {
//			System.out.println(s);
//			}
		} else {
			System.out.println("No Strings...");
		}

		
	}
	
	public void initStringMap () {
		invStringMap.clear();
		
		for (Integer id : symbolicStringMap.keySet()) {
			invStringMap.put(id, symbolicStringMap.get(id).clone());
		}

	}
	
	public void initStringMapAccum () {
		//invStringMap.clear();
		
		for (Integer id : symbolicStringMap.keySet()) {
			if (!invStringMap.containsKey(id)) {
				invStringMap.put(id, symbolicStringMap.get(id).clone());
			}
		}
	}
	
	public void initStringMap (int prevID, int predID) {
		invStringMap.clear();
		
		for (Integer id : symbolicStringMap.keySet()) {
			invStringMap.put(id, symbolicStringMap.get(id).clone());
		}
		
		Model_Acyclic_Inverse prevString = invStringMap.get(prevID).clone();
		invStringMap.put(predID, prevString);

	}
	
	public void duplicateString(int source, int dest) {
		Model_Acyclic_Inverse destString = invStringMap.get(source).clone();
		invStringMap.put(dest, destString);
	}
	
	public void reduceStringToShortest(int id) {
		
		if (reduceToShortest) {
			Model_Acyclic_Inverse shortestString = invStringMap.get(id);
			shortestString = shortestString.getShortestExampleModel();
			invStringMap.put(id, shortestString);	
		}

	}
	
	public void setReduce (boolean reduce) {
		this.reduceToShortest = reduce;
	}
	
	// TODO: REMOVE
	public void intersectPrevious(int current, int previous) {
		Model_Acyclic_Inverse previousModel = invStringMap.get(previous);
		Model_Acyclic_Inverse currentModel = invStringMap.get(current);
		currentModel = currentModel.intersect(previousModel);
		invStringMap.put(current, currentModel);
	}
	
	public Model_Acyclic_Inverse intersect(Model_Acyclic_Inverse currentModel, int previous) {
		Model_Acyclic_Inverse previousModel = invStringMap.get(previous);
		Model_Acyclic_Inverse resModel = currentModel.intersect(previousModel);

		return resModel;
	}

//	@Override // TODO: REMOVE
//	public void pushInvConstraint(I_Inv_Constraint constraint) {
//
//		//constraintStack.push(constraint);
//		
//	}

//	@Override // TODO: REMOVE
//	public I_Inv_Constraint popInvConstraint() {
//		
//		//return constraintStack.pop();
//		return null;
//	}

	
	@Override // TODO: REMOVE 
	public List<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>> inv_concat_sym_set(int id, int input, int arg, int base) {
		
		Model_Acyclic_Inverse inputModel = invStringMap.get(input);
		Model_Acyclic_Inverse argModel = invStringMap.get(arg);
		Model_Acyclic_Inverse baseModel = invStringMap.get(base);
		
		List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>> results = inputModel.inv_concatenate_sym_set(baseModel, argModel);

		return results;
	}
	
	@Override
	public void setSymString(int id, Model_Acyclic_Inverse stringModel) {
		invStringMap.put(id, stringModel);
		
	}

	@Override
	public Model_Acyclic_Inverse inv_setLength(Model_Acyclic_Inverse input, int length) {
		
		Model_Acyclic_Inverse resModel = input.inv_setLength(length);
		//invStringMap.put(id, resultModel);
		return resModel;
	}

	public Model_Acyclic_Inverse inv_charAt(Model_Acyclic_Inverse input, int index, int bound) {
		return input.inv_charAt(index, bound);
	}

	public Model_Acyclic_Inverse inv_indexOf(Model_Acyclic_Inverse input, Model_Acyclic_Inverse find, int index) {
		return input.inv_indexOf(find, index);
	}

	public int getBound() {
		return initialBound;
	}
}
