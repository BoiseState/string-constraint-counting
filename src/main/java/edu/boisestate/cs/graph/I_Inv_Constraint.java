package edu.boisestate.cs.graph;

import java.util.Set;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;
import edu.boisestate.cs.util.Tuple;
//import edu.boisestate.cs.solvers.*;

public interface I_Inv_Constraint{
	
	public boolean evaluate(I_Inv_Constraint inputConstraint, int sourceIndex);
	/**
	 * A second version of evaluate to be used in BFS algorithm
	 * Returns false if the node needs to backtracked because:
	 * either not all inputs were processed, or not all output solutions were examined
	 * @param inputConstraint
	 * @return true - don't backtrack, false - backtrack
	 */
	public Tuple<Boolean, Boolean> evaluate();
	
//	public boolean fallback();
	
	
	public Model_Acyclic_Inverse output(Integer index);
	public Model_Acyclic_Inverse output(I_Inv_Constraint constraint);
	
	public void setDebug(boolean debug);
	
	public void setArg(I_Inv_Constraint constraint);
	
	public void setArg2(I_Inv_Constraint constraint);

	public int getArgID();
	
	public void setNext(I_Inv_Constraint constraint);
	
	public int getNextID();
	
	public void setPrev(Set<I_Inv_Constraint> constraint);
	
	public Set<Integer> getPrevID();
	
	public Model_Acyclic_Inverse getSolution();
//	public int getPrevID();
	
//	public void setNextID(int nextID);
	
//	public void setPrevID(int prevID);
	
	
	public void setOp(Operation op);

	public Operation getOp();
	
	public void setID(int ID);
	
	public int getID();
	
	//resets computed values when the
	//BFS algorithm backtracks
	public void clear();
	/**
	 * synch prevIDs
	 */
	void update();

}
