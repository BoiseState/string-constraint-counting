package edu.boisestate.cs.solvers;

import java.util.List;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;
//import edu.boisestate.cs.graph.I_Inv_Constraint;
import edu.boisestate.cs.util.Quadruple;
import edu.boisestate.cs.util.Tuple;

public interface I_Solver_Inverse{
	
	
//	public void pushInvConstraint(I_Inv_Constraint constraint);
//	
//	public I_Inv_Constraint popInvConstraint ();
	
	public void outputConcreteStrings ();
	
	public void outputSymbolicStrings ();
	
//	void inv_append(int id, int base, int arg, int start, int end);

	Model_Acyclic_Inverse inv_append(int id, int base, int arg);

	void inv_contains(boolean result, int base, int arg);

//	void inv_deleteCharAt(int id, int base, int loc);

	Model_Acyclic_Inverse inv_delete(Model_Acyclic_Inverse input, int start, int end);

//	Quadruple<T,T,T,T> inv_insert(T input, T base, T arg, int start);

	void inv_insert(int id, int base, int arg, int offset, int start, int end);

	void inv_replaceCharFindKnown(int id, int base, char find);

	Model_Acyclic_Inverse inv_replaceCharKnown(Model_Acyclic_Inverse input, char find, char replace);

	Model_Acyclic_Inverse inv_replaceFirst(Model_Acyclic_Inverse input, Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace);

	Model_Acyclic_Inverse inv_replaceAll(Model_Acyclic_Inverse input, Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace);

	void inv_replaceCharReplaceKnown(int id, int base, char replace);

	void inv_replaceCharUnknown(int id, int base);

	String inv_replaceEscapes(String value);

	void inv_replaceStrings(int id, int base, int argOne, int argTwo);

	Model_Acyclic_Inverse inv_reverse(Model_Acyclic_Inverse input);

	void inv_setCharAt(int id, int base, int arg, int offset);

	void inv_setLength(int id, int base, int length);

	void inv_substring(int id, int base, int start);

	Model_Acyclic_Inverse inv_substring(Model_Acyclic_Inverse input, int start, int end);

	Model_Acyclic_Inverse inv_substring(Model_Acyclic_Inverse input, int start);
	
	Model_Acyclic_Inverse inv_setLength(Model_Acyclic_Inverse input, int length);
	
	
	Model_Acyclic_Inverse inv_toLowerCase(Model_Acyclic_Inverse input);

	Model_Acyclic_Inverse inv_toUpperCase(Model_Acyclic_Inverse input);

	Model_Acyclic_Inverse inv_trim(Model_Acyclic_Inverse input);

	/**
	 * Performs base.inv_concatenate(arg), stores result as symbolic string id
	 * 
	 * 05/15/2020 MJR
	 * 
	 * @param id - id of symbolic string location to store result
	 * @param input - symbolic string to use as input 
	 * @param arg - symbolic string to use as argument
	 */
//	void inv_append(int id, int input, int arg, int base);

	public List<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>> inv_concat_sym_set(int id, int input, int arg, int base); 
	public void setSymString(int id, Model_Acyclic_Inverse stringModel);

}
