/**
 * 
 */
package edu.boisestate.cs.graph;

import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;

/**
 * @author Marlin Roberts
 *
 */
public class SolutionSetInternal<T extends A_Model_Inverse<T>>  {

	Map<Integer,T> solutions;
	HashSet<T> sols;
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
		solutions = new HashMap<Integer,T>();
	}
	
	/**
	 * Sets a example for an incoming edge ID
	 * @param incomingEdge - ID of incoming edge
	 * @param solution - Automata example
	 */
	public void setSolution (Integer incomingEdge, T solution) {

		solutions.put(incomingEdge, solution);
	}

	public void addSolution(T solution) {
		sols.add(solution);
	}

	public void remSolution(T solution) {
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
	public T getSolution (Integer incomingEdge) {

		if (solutions.containsKey(incomingEdge)) {
			return solutions.get(incomingEdge);
		}
		return null;
	}
	
	/**
	 * Gets intersection of solutions for all incoming edges
	 * @return Automata example for all incoming edges
	 */
	public T getSolution () {
		
		T firstSolution = null;
		
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

		for (T thisSolution: sols) {
			if (thisSolution != null) {
				if (firstSolution == null) {
					firstSolution = thisSolution.clone();
				} else {
					firstSolution = firstSolution.intersect(thisSolution);
				}
			}
		}
		
		return firstSolution;
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

		T firstSolution = getSolution();

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
		
		T solution = this.getSolution();
		
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
