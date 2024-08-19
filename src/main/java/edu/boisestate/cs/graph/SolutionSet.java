/**
 * 
 */
package edu.boisestate.cs.graph;

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
public class SolutionSet<T extends A_Model_Inverse<T>> {

	public class Solution implements Comparable<Solution> {

		public int ID;
		public String example;
		public A_Model_Inverse<T> model; // model included for debugging i guess

		public Solution(int ID, String solution, T model) {
			this.ID = ID;
			this.example = solution;
			this.model = model;
		}

		public String toString() {
			return ID + ": \"" + example + "\"";
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

	public void add(int id, T solution) {
		Solution sol = new Solution(id, solution.getShortestExampleString(), solution);
		for (Solution s : solutions) {
			if (s.ID == id) {
				solutions.remove(s);
				break;
			}
		}
		solutions.add(sol);
		if (solutions.size() == numInputs) SAT = true;
	}

	public String getSolutions() {
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
}
