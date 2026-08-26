package edu.boisestate.cs.solvers;

import java.util.HashMap;
import java.util.Map;

import edu.boisestate.cs.Alphabet;
import edu.boisestate.cs.BasicTimer;
import edu.boisestate.cs.model.*;
import edu.boisestate.cs.util.Tuple;

public class Solver{

	protected Map<Integer, String> concreteStringMap = new HashMap<>();
	protected int initialBound = -1;
	protected Model_Acyclic_Inverse last = null;
	protected Model_Acyclic_Inverse lastArg = null;
	protected int lastArgId = -1;
	protected int lastId = -1;
	protected Map<Integer, Model_Acyclic_Inverse> symbolicStringMap = new HashMap<>();

	public final Model_Acyclic_Inverse_Manager modelManager;

	public int getTempId() {
		return -1;
	}

	public Solver(Model_Acyclic_Inverse_Manager modelManager) {

		// initialize factory from parameter
		this.modelManager = modelManager;
	}

	public Solver(Model_Acyclic_Inverse_Manager modelManager,
				  int initialBound) {

		// initialize bound from parameter value
		this.initialBound = initialBound;

		// initialize factory from parameter
		this.modelManager = modelManager;
	}

	/**
	 * Checks if the parameter containsString a predicate method.
	 *
	 * @param string
	 *         The name of the method to be checked.
	 *
	 * @return true if the parameter is a predicate.
	 */
	public static boolean containsBoolFunction(String string) {
		String fName = string.split("!!")[0];
		return fName.equals("equals") ||
			   fName.equals("contains") ||
			   fName.equals("contentEquals") ||
			   fName.equals("endsWith") ||
			   fName.equals("startsWith") ||
			   fName.equals("equalsIgnoreCase") ||
			   fName.equals("matches") ||
			   fName.equals("isEmpty") ||
			   fName.equals("regionMatches");
	}

	public Model_Acyclic_Inverse getModel(int id) {
		return this.symbolicStringMap.get(id);
	}

	public void append(int id, int base, int arg, int start, int end) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		// start timer
		BasicTimer.start();

		// get substring model
		Model_Acyclic_Inverse substrModel = argModel.substring(start, end);

		// append substring model to base model
		baseModel = baseModel.concatenate(substrModel);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void append(int id, int base, int arg) {

//    	System.out.println(id + " " + base + " " + arg);
//    	System.out.println("sybmolicStringMap " + symbolicStringMap);
		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);
		// start timer
		BasicTimer.start();
		//System.out.println("bM " + baseModel.getAutomaton().toString() + " aM " + argModel.getAutomaton().toString());
		// perform operation
		baseModel = baseModel.concatenate(argModel);

		//System.out.println("Append " + baseModel + " id " + id);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void contains(boolean result, int base, int arg) {
		// here we do a preliminary search if possible and sat test (null models causing Parser_2 to return false)

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		// if either argument is concrete we do preliminary searches
		if (baseModel.isSingleton()){
			String b = baseModel.getAcceptedStringExample();
			if (argModel.isSingleton()){
				String a = argModel.getAcceptedStringExample();
				if (result && !b.contains(a)){
					baseModel = null;
					argModel = null;
				} else if (!result && b.contains(a)){
					baseModel = null;
					argModel = null;
				}
			} else {
				String aExample = argModel.getAcceptedStringExample();
				// search for at least one possible assignment removing inconsistent examples on the way
				if (result) {
					while (aExample != null && !b.contains(aExample)) {
						argModel = argModel.removeString(aExample);
						aExample = argModel.getAcceptedStringExample();
					}
				} else {
					while (aExample != null && b.contains(aExample)) {
						argModel = argModel.removeString(aExample);
						aExample = argModel.getAcceptedStringExample();
					}
				}
				// if argModel is empty then no solution which is caught in Parser_2
			}
		} else if (argModel.isSingleton()) {
			// base sym, but arg concrete, so pin base to contain arg
			baseModel = baseModel.assertContains(argModel, result);
		}

		// store result models
		this.symbolicStringMap.put(base, baseModel);
		this.symbolicStringMap.put(arg, argModel);
	}

	public void deleteCharAt(int id, int base, int loc) {

		// delegate to delete method with start and end based on loc
		this.delete(id, base, loc, loc + 1);
	}

	public void delete(int id, int base, int start, int end) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform delete
		baseModel = baseModel.delete(start, end);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void endsWith(boolean result, int base, int arg) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		if (result) {

			// start timer
			BasicTimer.start();

			// get satisfying base model
			baseModel = baseModel.assertEndsWith(argModel);

			// get satisfying arg model
			argModel = argModel.assertEndsOther(baseModel);

			// stop timer
			BasicTimer.stop();
		} else {

			// start timer
			BasicTimer.start();
			// get satisfying base model as temp
			Model_Acyclic_Inverse tempBaseModel = baseModel.isSingleton() ? baseModel : baseModel.assertNotEndsWith(argModel);

			// get satisfying arg model
			Model_Acyclic_Inverse tempArgModel = argModel.isSingleton() ? argModel : argModel.assertNotEndsOther(baseModel);

			// issue with two symbolics
			if (tempBaseModel.isEmpty()) {
				System.err.println("Warning, Solver.endsWith(): base model is empty");
				if (tempArgModel.isEmpty()) {
					// likely both anyString
					System.err.println("and arg model is empty");
				}
			} else if (tempArgModel.isEmpty()) {
//				System.err.println("Warning, Solver.endsWith(): argModel is empty");
				tempBaseModel = baseModel.clone();
				// this will manipulate the tempBaseModel and return its disjunct pair
				tempArgModel = tempBaseModel.createDisjoint();
				tempArgModel = tempArgModel.intersect(argModel);
			}

			baseModel = tempBaseModel;
			argModel = tempArgModel;
			// stop timer
			BasicTimer.stop();
		}

		// store result models
		this.symbolicStringMap.put(base, baseModel);
		this.symbolicStringMap.put(arg, argModel);
	}

	public void equals(boolean result, int base, int arg) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);
//        System.out.println("argModel " + arg + "\n" + argModel.getAutomaton());
//        System.out.println("result " + result);
		// perform equals
		if (result) {

			// start timer
			BasicTimer.start();
			//System.out.println(baseModel + " id " + base);
			// get satisfying base model
			baseModel = baseModel.assertEquals(argModel);

			// get satisfying arg model
			argModel = argModel.assertEquals(baseModel);

			// stop timer
			BasicTimer.stop();
		} else {

			// start timer
			BasicTimer.start();

			Model_Acyclic_Inverse tempBaseModel = baseModel.isSingleton() ? baseModel.clone() : baseModel.assertNotEquals(argModel);
			Model_Acyclic_Inverse tempArgModel = argModel.isSingleton() ? argModel.clone() : argModel.assertNotEquals(baseModel);

			// if either model is anyString we'll have an empty language
			// or if one is a subset of the other
			// also if the language are equivalent we'll have two empty languages....
			// need to enforce disjunct languages
			// for languages that are subsets we can just change the order
			if (tempBaseModel.isEmpty()) {
				if (tempArgModel.isEmpty()) {
					// both models empty so equivalent and need (ideally evenly split) disjunct models
					tempBaseModel = baseModel.clone();
					// this will manipulate the tempBaseModel and return its disjunct pair
					tempArgModel = tempBaseModel.createDisjoint();
				} else {
					tempBaseModel = baseModel.clone();
				}
			} else if (tempArgModel.isEmpty()) {
				tempArgModel = argModel.clone();
			}
			baseModel = tempBaseModel;
			argModel = tempArgModel;

			// ummm so make sure that if they are BOTH singleton that they are different strings
			if (baseModel.isSingleton() && argModel.isSingleton()) {
				if (baseModel.equals(argModel)) { // UNSAT
					// make one empty or both i guess
					baseModel = null;
					argModel = null;
				}
			}

			// stop timer
			BasicTimer.stop();
		}

		// store result models
		this.symbolicStringMap.put(base, baseModel);
		this.symbolicStringMap.put(arg, argModel);
	}

	public void equalsIgnoreCase(boolean result, int base, int arg) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		// perform equals
		if (result) {

			// start timer
			BasicTimer.start();

			// get satisfying base model
			baseModel = baseModel.assertEqualsIgnoreCase(argModel);

			// get satisfying arg model
			argModel = argModel.assertEqualsIgnoreCase(baseModel);

			// stop timer
			BasicTimer.stop();
		} else {

			// start timer
			BasicTimer.start();

			// get satisfying base model as temp
			Model_Acyclic_Inverse tempModel = baseModel.assertNotEqualsIgnoreCase(argModel);

			// get satisfying arg model
			argModel = argModel.assertNotEqualsIgnoreCase(baseModel);

			// set base model from temp
			baseModel = tempModel;

			// stop timer
			BasicTimer.stop();
		}

		// store result models
		this.symbolicStringMap.put(base, baseModel);
		this.symbolicStringMap.put(arg, argModel);
	}

	public String getSatisfiableResult(int id) {

		// get model
		Model_Acyclic_Inverse model = this.symbolicStringMap.get(id);
		return model.getAcceptedStringExample();
	}

	public void insert(int id, int base, int arg, int offset) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		// start timer
		BasicTimer.start();

		// perform insert
		baseModel = baseModel.insert(offset, argModel);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void insert(int id,
					   int base,
					   int arg,
					   int offset,
					   int start,
					   int end) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		// start timer
		BasicTimer.start();

		// get substring from arg model
		Model_Acyclic_Inverse substrModel = argModel.substring(start, end);

		// perform insert
		baseModel = baseModel.insert(offset, substrModel);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void isEmpty(boolean result, int base) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		if (result) {

			// start timer
			BasicTimer.start();

			// get satisfying automaton
			baseModel = baseModel.assertEmpty();

			// stop timer
			BasicTimer.stop();

		} else {

			// start timer
			BasicTimer.start();

			// get satisfying automaton
			baseModel = baseModel.assertNotEmpty();

			// stop timer
			BasicTimer.stop();

		}

		// store result models
		this.symbolicStringMap.put(base, baseModel);
	}

	public boolean isSatisfiable(int id) {

		// get model
		Model_Acyclic_Inverse model = this.symbolicStringMap.get(id);
		//System.out.println("model " + model + " id " + id);
		// return true if not empty
		return !model.isEmpty();
	}

	public boolean isSingleton(int id, String actualValue) {

		// get model
		Model_Acyclic_Inverse model = this.symbolicStringMap.get(id);
		//System.out.println("model " + model + " id " + id);
		//System.out.println(model.getAutomaton() + " val " + actualValue);

		// return singleton status
		return model.containsString(actualValue) && model.isSingleton();
	}

	public boolean isSingleton(int id) {
		// System.out.println("singleton " + id);
		// get model
		Model_Acyclic_Inverse model = this.symbolicStringMap.get(id);

		// return singleton status
		return model.isSingleton();
	}

	public boolean isSound(int id, String actualValue) {

		// get model
		Model_Acyclic_Inverse model = this.symbolicStringMap.get(id);
		//why do we intersect? Why not just check whether
		//the automaton accepts the string?
		//System.out.println("M " + model.getAutomaton() + " id " + id + " val " + actualValue);
		boolean ret = true;
		if (actualValue.equals("true") || actualValue.equals("false")) {
			//since the actual program execution went either true
			//or false then the resulting automaton should not be empty
			ret = !model.isEmpty();
		} else {
			ret = model.containsString(actualValue);
		}
		return ret;
        
        /* eas 10-20-18 old code 

        // get value model
        AutomatonModel value =
                this.modelManager.createString(actualValue);

        // intersect models
        AutomatonModel intersection = model.intersect(value);  
        // sound if intersection is not empty
        return !intersection.isEmpty();
        */

	}

	public void newConcreteString(int id, String string) {
		// start timer
		BasicTimer.start();

		// create new automaton model from string
		Model_Acyclic_Inverse model = this.modelManager.createString(string);

		//System.out.println("newConcreteString " + id + " : " + string + " " + model.getClass());
		// stop timer
		BasicTimer.stop();

		// store new model
		this.symbolicStringMap.put(id, model);

		// System.out.println("m " + "\n" + model.getAutomaton());

		// store string value
		this.concreteStringMap.put(id, string);
	}

	public void newSymbolicString(int id) {
		// start timer
		BasicTimer.start();

		// create new symbolic string
		Model_Acyclic_Inverse model =
				this.modelManager.createAnyString(this.initialBound);

		// stop timer
		BasicTimer.stop();
		//System.out.println("M " + model + " id " + id);
		// store new model
		this.symbolicStringMap.put(id, model);
	}

	public void propagateSymbolicString(int id, int base) {
		// get model
		Model_Acyclic_Inverse model = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// clone model
		Model_Acyclic_Inverse clone = model.clone();

		// stop timer
		BasicTimer.stop();

		// store clone
		this.symbolicStringMap.put(id, clone);
	}

	public void replaceCharFindKnown(int id, int base, char find) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform replace string operation
		baseModel = baseModel.replaceFindKnown(find);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);

	}

	public void replaceCharKnown(int id, int base, char find, char replace) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();
		// perform replace string operation
		baseModel = baseModel.replace(find, replace);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);

	}

	public void replaceCharReplaceKnown(int id, int base, char replace) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform replace string operation
		baseModel = baseModel.replaceReplaceKnown(replace);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void replaceCharUnknown(int id, int base) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform replace string operation
		baseModel = baseModel.replaceChar();

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);

	}

	public String replaceEscapes(String value) {

		// all unicode characters supported
		return value;
	}


	/**
	 * This should be working
	 *
	 * @param id
	 * @param base
	 * @param argOne
	 * @param argTwo
	 */
	public void replaceAll(int id, int base, int argOne, int argTwo) {
		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		// nps - 04.16.25 - unsure why this is here
//    	if (baseModel.getClass() != Model_Acyclic.class)
//    		return;

		// nps - switched to models: untested as of 4.3.25
		Model_Acyclic_Inverse arg1, arg2;
		if (this.concreteStringMap.get(argOne) == null) {
			arg1 = this.symbolicStringMap.get(argOne);
		} else {
			String arg1String = this.concreteStringMap.get(argOne);
			arg1 = this.modelManager.createString(arg1String);
		}
		if (this.concreteStringMap.get(argTwo) == null) {
			arg2 = this.symbolicStringMap.get(argTwo);
		} else {
			String arg2String = this.concreteStringMap.get(argTwo);
			arg2 = this.modelManager.createString(arg2String);
		}

//    	System.out.println("Before:\n" + baseModel.getFiniteStrings());
//    	System.out.println("\n==========\n\nStarting Automaton:\n\n" + baseModel.getAutomaton().toString() + "\n\n========\n\n");
		// start timer
		BasicTimer.start();
		// perform replaceFirst string operation
		baseModel = baseModel.replaceAll(arg1, arg2);
		// stop timer
		BasicTimer.stop();
//    	System.out.println("After:\n" + baseModel.getFiniteStrings());
//    	System.out.println("\n==========\n\nFinished Automaton:\n\n" + baseModel.getAutomaton().toString() + "\n\n========\n\n");
		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}


	/**
	 * This should be working
	 *
	 * @param id
	 * @param base
	 * @param argOne
	 * @param argTwo
	 */
	public void replaceFirst(int id, int base, int argOne, int argTwo) {
		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
//    	if (baseModel.getClass() != Model_Acyclic.class)
//    		return;
		// nps - tryingot handle concrete and symbolic arguments.
		Model_Acyclic_Inverse arg1, arg2;
		if (this.concreteStringMap.get(argOne) == null) {
			arg1 = this.symbolicStringMap.get(argOne);
		} else {
			String arg1String = this.concreteStringMap.get(argOne);
			arg1 = this.modelManager.createString(arg1String);
		}
		if (this.concreteStringMap.get(argTwo) == null) {
			arg2 = this.symbolicStringMap.get(argTwo);
		} else {
			String arg2String = this.concreteStringMap.get(argTwo);
			arg2 = this.modelManager.createString(arg2String);
		}
//    	System.out.println("Before:\n" + baseModel.getFiniteStrings());
		// start timer
		BasicTimer.start();
		// perform replaceFirst string operation
		// this replaceFirst does take find argument as regex as well
		baseModel = baseModel.replaceFirst(arg1, arg2);
		// stop timer
		BasicTimer.stop();
//    	System.out.println("After:\n" + baseModel.getFiniteStrings());
		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void replaceStrings(int id, int base, int argOne, int argTwo) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		String arg1String = this.concreteStringMap.get(argOne);
		String arg2String = this.concreteStringMap.get(argTwo);

		// start timer
		BasicTimer.start();

		// perform replace string operation
		baseModel = baseModel.replace(arg1String, arg2String);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void reverse(int id, int base) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform operation
		baseModel = baseModel.reverse();

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void setCharAt(int id, int base, int arg, int offset) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		// start timer
		BasicTimer.start();

		// perform set char
		baseModel = baseModel.setCharAt(offset, argModel);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void setLength(int id, int base, int length) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform set length
		baseModel = baseModel.setLength(length);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void shutDown() {
		// nothing needed
	}

	public void startsWith(boolean result, int base, int arg) {

		// get models
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(arg);

		if (result) {

			// start timer
			BasicTimer.start();

			// get satisfying base model
			baseModel = baseModel.assertStartsWith(argModel);

			// get satisfying arg model
			argModel = argModel.assertStartsOther(baseModel);

			// stop timer
			BasicTimer.stop();

		} else {

			// start timer
			BasicTimer.start();

			// get satisfying base model as temp
			Model_Acyclic_Inverse tempBaseModel = baseModel.isSingleton() ? baseModel : baseModel.assertNotStartsWith(argModel);

			// get satisfying arg model
			Model_Acyclic_Inverse tempArgModel = argModel.isSingleton() ? argModel : argModel.assertNotStartsOther(baseModel);

			if (tempBaseModel.isEmpty()) {
				System.err.println("Warning, Solver.startsWith(): base model is empty");
				if (tempArgModel.isEmpty()) {
					System.err.println("and arg model is empty");
				}
			} else if (tempArgModel.isEmpty()) {
				tempBaseModel = baseModel.clone();
				tempArgModel = tempBaseModel.createDisjoint();
				tempArgModel = tempArgModel.intersect(argModel);
			}
			// set base model from temp
			baseModel = tempBaseModel;
			argModel = tempArgModel;

			// stop timer
			BasicTimer.stop();
		}

		// store result models
		this.symbolicStringMap.put(base, baseModel);
		this.symbolicStringMap.put(arg, argModel);
	}

	public void substring(int id, int base, int start) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform operation
		baseModel = baseModel.suffix(start);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void substring(int id, int base, int start, int end) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform operation
		baseModel = baseModel.substring(start, end);

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void toLowerCase(int id, int base) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform operation
		baseModel = baseModel.toLowercase();

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void toUpperCase(int id, int base) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform operation
		baseModel = baseModel.toUppercase();

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void trim(int id, int base) {

		// get model
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);

		// start timer
		BasicTimer.start();

		// perform operation
		baseModel = baseModel.trim();

		// stop timer
		BasicTimer.stop();

		// store result model
		this.symbolicStringMap.put(id, baseModel);
	}

	public void charAt(int id, int base, int index) {
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		baseModel = baseModel.charAt(index);
		this.symbolicStringMap.put(id, baseModel);
	}

	public void indexOf(int id, int base, int find) {
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		Model_Acyclic_Inverse argModel = this.symbolicStringMap.get(find);

		baseModel = baseModel.indexOf(argModel);
		this.symbolicStringMap.put(id, baseModel);
	}

	private Tuple<Character, Boolean> getCharFromString(String string) {

		// initialize result variables
		boolean isKnown = true;
		char charValue;

		// attempt to parse char value from string
		try {

			// parse string to int
			int tempVal = Integer.parseInt(string);

			// if value falls between 0 and 9, not known char value
			if (tempVal >= 0 || tempVal < 10) {
				isKnown = false;
			}

			// set char value via cast
			charValue = (char) tempVal;

		} catch (NumberFormatException e) {

			// if string is not empty
			if (!string.isEmpty()) {

				// set value to first char in string
				charValue = string.charAt(0);

			} else {

				// set value to first value from alphabet
				Alphabet alphabet = this.modelManager.getAlphabet();
				charValue = alphabet.getSymbolSet().iterator().next();

			}
		}

		// return results
		return new Tuple<>(charValue, isKnown);
	}

	public String getConcreteString(int id) {
		return this.concreteStringMap.get(id);
	}


	public void length(int id, int base) {
		Model_Acyclic_Inverse baseModel = this.symbolicStringMap.get(base);
		int upperBound = baseModel.getBoundLength();
		int lowerBound = baseModel.getLowerBoundLength();
		// create anyString with lengths bounds that will act as integer check
		// TODO: any problem if this somehow isn't interpreted as an integer range?
		Model_Acyclic_Inverse lenModel = this.modelManager.createString(Integer.toString(lowerBound));
		for (int i = lowerBound + 1; i <= upperBound; i++) {
			String lenStr = Integer.toString(i);
			lenModel = lenModel.union(this.modelManager.createString(lenStr));
		}
		this.symbolicStringMap.put(id, lenModel);
	}

	/**
	 * Used to get the value currently stored in the symbolic string map for the
	 * given id.
	 *
	 * @param id
	 *         id used to get the value to return.
	 *
	 * @return the symbolic string value represented by the id.
	 */
	public Model_Acyclic_Inverse getValue(int id) {
		return symbolicStringMap.get(id);
	}

	/**
	 * Used to check if the values involved are capable of being used in the
	 * coming operations. For example, in EStranger, the time required to solve
	 * constraints grows as more transitions appear in the automata.
	 *
	 * @param base
	 *         representation of calling string
	 * @param arg
	 *         representation of argument string
	 *
	 * @return boolean value indicating if the symbolic values can be used in
	 * the coming operations.
	 */
	public boolean isValidState(int base, int arg) {
		return true;
	}

	/**
	 * Remove a symbolic string that won't be used anymore.
	 *
	 * @param id
	 *         represents string that won't be used anymore.
	 */
	public void remove(int id) {
		if (symbolicStringMap != null && symbolicStringMap.containsKey(id)) {
			symbolicStringMap.remove(id);
		}
	}

	/**
	 * Used to undo the last predicate applied. Useful for checking if the
	 * branch is satisfiable without actually applying the predicate.
	 */
	public void revertLastPredicate() {
		if (last == null) {
			throw new IllegalStateException();
		}
		symbolicStringMap.put(lastId, last);
		last = null;
		lastId = -1;

		if (lastArg != null) {
			symbolicStringMap.put(lastArgId, lastArg);
			lastArg = null;
			lastArgId = -1;
		}

	}

	/**
	 * Sets the last base and argument for reverting the last predicate.
	 *
	 * @param base
	 *         id of the current base.
	 * @param arg
	 *         id of the current arg
	 */
	public void setLast(int base, int arg) {
		last = symbolicStringMap.get(base);
		lastId = base;
		if (arg > 0) {
			lastArg = symbolicStringMap.get(arg);
			lastArgId = arg;
		}
	}
}
