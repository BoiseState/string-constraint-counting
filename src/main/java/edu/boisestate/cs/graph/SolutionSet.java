/**
 * 
 */
package edu.boisestate.cs.graph;

import edu.boisestate.cs.model.Model_Acyclic_Inverse;

import java.util.*;

/**
 * @author nat
 * Created based on a graph for a inverse reporterwith a provided number of inputs to solve for.
 * Consistency et cetera is/should be handled by the solver and this object is only used for
 * storing and updating the found soutions for output later.
 *
 */
public class SolutionSet{

	public class Solution implements Comparable<Solution> {

		public int ID;
		public String originalName;
		public String example;
		public Model_Acyclic_Inverse model; // model included for debugging i guess

		public Solution(int ID, String originalName, String solution, Model_Acyclic_Inverse model) {
			this.ID = ID;
			this.originalName = originalName.replace("_SYMSTRING", ""); // just for spf
			this.example = solution;
			this.model = model;
		}

		public String toString() {
			return originalName + ": \"" + example + "\"";
		}

		public int compareTo(Solution s) {
			return Integer.compare(ID, s.ID);
		}
	}

	private LinkedHashMap<String, Solution> solutions;
	private boolean SAT;
	private final int numInputs;

	public SolutionSet(int numInputs) {
		solutions = new LinkedHashMap<>();
		SAT = false;
		this.numInputs = numInputs;
	}

	public void setSAT(boolean sat) {
		this.SAT = sat;
	}

	public void add(int id, String originalName, Model_Acyclic_Inverse solution) {
		Solution sol = new Solution(id, originalName, solution.getAcceptedStringExample(), solution);
		solutions.put(originalName, sol);
		SAT = solutions.size() == numInputs;
	}

	public String getResult() {
		if (!SAT) return "unsat";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("sat,");
			for (Solution s : solutions.values()) {
				sb.append("\n").append(s.toString());
			}
			return sb.toString();
		}
	}

	public List<Solution> getSolutions() {
		return new ArrayList<>(this.solutions.values());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SAT: ").append(SAT).append("\n");
		sb.append("Expected Solutions: ").append(numInputs).append("\n");
		sb.append("Solutions: (").append(solutions.size()).append(")\n");
		for (Solution s : solutions.values()) {
			sb.append("\t").append(s.toString()).append("\n");
		}
		return sb.toString();
	}

	public boolean isSAT() {
		return SAT;
	}

	public SolutionSet clone() {
		SolutionSet newSet = new SolutionSet(this.numInputs);
		newSet.setSAT(this.SAT);
		newSet.solutions = new LinkedHashMap<>();
		for (Solution s : this.solutions.values()) {
			Solution newSol = new Solution(s.ID, s.originalName, s.example, s.model.clone());
			newSet.solutions.put(s.originalName, newSol);
		}
		return newSet;
	}

	public Solution getSolutionForVar(String originalName) {
		return solutions.get(originalName);
	}
}
