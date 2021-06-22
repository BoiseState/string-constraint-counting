/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.Formatter;
import java.util.List;
import java.util.Map;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
//import edu.boisestate.cs.solvers.Solver_Inverse;
import edu.boisestate.cs.solvers.Solver_Inverse;

/**
 * @author marli
 *
 */
public abstract class A_Inv_Constraint<T extends A_Model_Inverse<T>> implements I_Inv_Constraint<T> {

	// This will hold a reference to the containing solver.
	// This allows the constraint access to the solver functions and string tables.
	protected Solver_Inverse<T> solver;
	
	protected int ID;
	
	protected int nextID = -1;
	protected int prevID = -1;
	protected int argID = -1;
	
	public int fallbackCount = 0;
	public int evaluateCount = 0;
	public int newConcatChoiceCount = 0;
	public int newSplitOutputCount = 0;
	
	protected I_Inv_Constraint<T> prevConstraint;
	protected I_Inv_Constraint<T> nextConstraint;
	protected I_Inv_Constraint<T> argConstraint;
	
	protected SolutionSetInternal<T> solutionSet;
	
	protected Map<Integer,T> outputSet;
	
	protected Operation op; 
	
	protected List<Integer> argList;
	
	protected String argString;
	
	protected boolean debug = false;
	
	@Override
	public boolean evaluate(I_Inv_Constraint<T> inputConstraint, int sourceIndex) {
		
		return false;
	}

	@Override
	public T output(Integer index) {
		return outputSet.get(index);
	}
	
//	@Override
//	public boolean fallback() {
//		
//		return false;
//	}
	
	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void setNext(I_Inv_Constraint<T> constraint) {
		
		this.nextConstraint = constraint;
		this.nextID = constraint.getID();
	}
	
	@Override
	public void setArg(I_Inv_Constraint<T> constraint) {
		
		this.argConstraint = constraint;
		this.argID = constraint.getID();
	}

	@Override
	public void setPrev(I_Inv_Constraint<T> constraint) {
		
		this.prevConstraint = constraint;
		this.prevID = constraint.getID();
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
	
	@Override
	public int getNextID() {

		return this.nextID;
	}
	
	@Override
	public int getArgID() {

		return this.argID;
	}
	
	@Override
	public int getPrevID() {

		return this.prevID;
	}
	
	
	public T getSolution() {
		
		
		return solutionSet.getSolution();
		
	};
	
//	@Override
//	public void setNextID(int nextID) {
//
//		this.nextID = nextID;
//	}
	
//	@Override
//	public void setPrevID(int prevID) {
//
//		this.prevID = prevID;
//	}
	
	
	public String toString() {
		
		StringBuilder output = new StringBuilder();
		Formatter fm = new Formatter (output);
//		int arg0 = 0;
//		if (argList.size() >= 1) arg0 = argList.get(0);
		
		//fm.format("[%d] \t%s\tBASE(N): [%d] \tINP(P): [%d] \tARGS: [%d] ARG(0): [%d]",ID,op,nextID,prevID,argList.size(),arg0);
		fm.format("[%d] \t%-15s\tBASE(N): [%d] \t\tARG: [%d]",ID,op,nextID,argID);
		
//		fm.format("ID: [%d] | ", ID);
//		fm.format("OP: [%s] | ", op.toString());
//		fm.format("ARGS: %s | ", this.argString);
//		fm.format("NEXT: [%d] | ", this.nextConstraint != null ? this.nextConstraint.getID() : -1);
//		fm.format("PREV: [%d]\n", this.prevConstraint != null ? this.prevConstraint.getID() : -1);
		fm.close();
		
		
		
		
		return output.toString();
	}

}