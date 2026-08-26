/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;

/**
 * @author Marlin Roberts
 *
 */
public class SolutionSetInternal{

	Map<Integer,Model_Acyclic_Inverse> solutions;
	HashSet<Model_Acyclic_Inverse> sols;
	Model_Acyclic_Inverse comp;
	int ID;
	
	/**
	 * Constructs example set for use inside an inverse constraint.
	 * Maps incoming edge to example.
	 * 
	 * @param ID = Containing Inverse constraint ID.
	 */
	public SolutionSetInternal (int ID) {
		
		this.ID = ID;
		sols = new HashSet<>();
		solutions = new HashMap<Integer,Model_Acyclic_Inverse>();
	}
	
	/**
	 * Sets a example for an incoming edge ID
	 * @param incomingEdge - ID of incoming edge
	 * @param solution - Automata example
	 */
	public void setSolution (Integer incomingEdge, Model_Acyclic_Inverse solution) {
		solutions.put(incomingEdge, solution);
	}

	public void addSolution(Model_Acyclic_Inverse solution) {
		sols.add(solution);
	}

	public void remSolution(Model_Acyclic_Inverse solution) {
		sols.remove(solution);
	}

	/**
	 * Removes a example from solutions set
	 * @param incomingEdge - ID of incoming edge
	 */
	public boolean remSolution (Integer incomingEdge) {

		if (solutions.containsKey(incomingEdge)) {
			solutions.remove(incomingEdge);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets a single example for incoming edge ID
	 * @param incomingEdge ID
	 * @return Automata example for single incoming edge
	 */
	public Model_Acyclic_Inverse getSolution (Integer incomingEdge) {

		if (solutions.containsKey(incomingEdge)) {
			return solutions.get(incomingEdge);
		}
		return null;
	}
	
	/**
	 * Gets intersection of solutions for all incoming edges
	 * @return Automata example for all incoming edges
	 */
	public Model_Acyclic_Inverse getSolution () {
		
		Model_Acyclic_Inverse firstSolution = null;
		
//		for (Integer ID: solutions.keySet()) {
//			T thisSolution = solutions.get(ID);
//			if (thisSolution != null) {
//				if (firstSolution == null) {
//					firstSolution = thisSolution.clone();
//				} else {
//					firstSolution = firstSolution.intersect(thisSolution);
//				}
//			}
//		}

		for (Model_Acyclic_Inverse thisSolution: sols) {
			if (thisSolution != null) {
				if (firstSolution == null) {
					firstSolution = thisSolution.clone();
				} else {
					firstSolution = firstSolution.intersect(thisSolution);
				}
			}//TODO: else? shuoldnt this mean inconsistent and/or never be null?
		}
		if (firstSolution != null) comp = firstSolution.complement();	// :)
		return firstSolution;
	}

	public Model_Acyclic_Inverse getComp() {
		return comp;
	}
	
	/**
	 * Returns true if intersection of all incoming edge solutions is not empty
	 * @return - boolean
	 */
	public boolean isConsistent() {
		
//		System.out.println("CHECKING CONSISTENCY - SOLUTIONS PRESENT: " + solutions.size());
		
		if (sols.isEmpty()) {
			return false;
		}
		
//		T firstSolution = null;
//
//		for (Integer ID: solutions.keySet()) {
//			T thisSolution = solutions.get(ID);
//			if (thisSolution != null) {
//				if (firstSolution == null) {
//					firstSolution = thisSolution;
//				} else {
//					firstSolution = firstSolution.intersect(thisSolution);
//				}
//			}
//		}

		Model_Acyclic_Inverse firstSolution = getSolution();

		if (firstSolution != null) {
			if (firstSolution.isEmpty()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}

	}
	
	@Override
	public String toString() {
		
		StringBuilder output = new StringBuilder();
		Formatter fm = new Formatter (output);
		
		Model_Acyclic_Inverse solution = this.getSolution();
		
		fm.format("\nINPUT:      [%d]\n", ID);
		fm.format("EDGES:      [%d]\n", solutions.size());
		fm.format("CONSISTENT: [%s]\n", this.isConsistent() ? "YES" : "NO");
//		fm.format("COUNT:      [%d]\n", example.modelCount());
		fm.close();

		return output.toString();
	}

	public void clear() {
		solutions.clear();
		sols.clear();
	}
	
}
