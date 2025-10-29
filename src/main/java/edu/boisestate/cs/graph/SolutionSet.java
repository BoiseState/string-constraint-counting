/**
 * 
 */
package edu.boisestate.cs.graph;

import edu.boisestate.cs.automatonModel.A_Model;
import edu.boisestate.cs.automatonModel.A_Model_Inverse;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * @author nat
 * Created based on a graph for a inverse reporterwith a provided number of inputs to solve for.
 * Consistency et cetera is/should be handled by the solver and this object is only used for
 * storing and updating the found soutions for output later.
 *
 */
public class SolutionSet<T extends A_Model<T>> {

	public class Solution implements Comparable<Solution> {

		public int ID;
		public String originalName;
		public String example;
		public A_Model<T> model; // model included for debugging i guess
		public A_Model<T> comp;

		public Solution(int ID, String originalName, String solution, T model, T comp) {
			this.ID = ID;
			this.originalName = originalName.replace("_SYMSTRING", ""); // just for spf
			this.example = solution;
			this.model = model;
			this.comp = comp;
		}

		public String toString() {
			return originalName + ": \"" + example + "\"";
		}

		public int compareTo(Solution s) {
			return Integer.compare(ID, s.ID);
		}
	}

	private List<Solution> solutions;
	private boolean SAT;
	private int numInputs;

	public SolutionSet(int numInputs) {
		solutions = new ArrayList<>();
		SAT = false;
		this.numInputs = numInputs;
	}

	public void setSAT(boolean sat) {
		this.SAT = sat;
	}

	public void add(int id, String originalName, T solution, T comp) {
		Solution sol = new Solution(id, originalName, solution.getAcceptedStringExample(), solution, comp);
		for (Solution s : solutions) {
			if (s.ID == id) {
				solutions.remove(s);
				break;
			}
		}
		solutions.add(sol);
		if (solutions.size() == numInputs) SAT = true;
	}

	public String getResult() {
		if (!SAT) return "unsat";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("sat,");
			solutions.sort(null);
			for (Solution s : solutions) {
				sb.append("\n").append(s.toString());
			}
			return sb.toString();
		}
	}

	public List<Solution> getSolutions() {
		return solutions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SAT: ").append(SAT).append("\n");
		sb.append("Expected Solutions: ").append(numInputs).append("\n");
		sb.append("Solutions: (").append(solutions.size()).append(")\n");
		for (Solution s : solutions) {
			sb.append("\t").append(s.toString()).append("\n");
		}
		return sb.toString();
	}

	public boolean isSAT() {
		return SAT;
	}

	public SolutionSet<T> clone() {
		SolutionSet<T> newSet = new SolutionSet<>(this.numInputs);
		newSet.setSAT(this.SAT);
		for (Solution s : this.solutions) {
			Solution newSol = new Solution(s.ID, s.originalName, s.example, s.model.clone(), s.comp.clone());
			newSet.solutions.add(newSol);
		}
		return newSet;
	}

	public Solution getSolutionForVar(String originalName) {
		for (Solution s : solutions) {
			if (s.originalName.equals(originalName)) {
				return s;
			}
		}
		return null;
	}
}
