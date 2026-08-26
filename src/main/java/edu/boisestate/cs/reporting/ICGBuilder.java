package edu.boisestate.cs.reporting;

import edu.boisestate.cs.automatonModel.A_Model_Inverse;
import edu.boisestate.cs.graph.*;
import edu.boisestate.cs.solvers.Solver_Inverse;

import org.jgrapht.DirectedGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Builds the transposed graph of inverse constraints (the ICG) from a forward
 * constraint graph.
 *
 * @param <T> - Automata model that implements inverse operations.
 */
public class ICGBuilder<T extends A_Model_Inverse<T>> {

	private final DirectedGraph<PrintConstraint, SymbolicEdge> graph;
	private final Map<Integer, PrintConstraint> allConstraints;
	private final Map<Integer, I_Inv_Constraint<T>> allInverseConstraints;
	private final Solver_Inverse<T> invSolver;
	private final boolean debug;

	public ICGBuilder(DirectedGraph<PrintConstraint, SymbolicEdge> graph,
					   Map<Integer, PrintConstraint> allConstraints,
					   Map<Integer, I_Inv_Constraint<T>> allInverseConstraints,
					   Solver_Inverse<T> invSolver,
					   boolean debug) {
		this.graph = graph;
		this.allConstraints = allConstraints;
		this.allInverseConstraints = allInverseConstraints;
		this.invSolver = invSolver;
		this.debug = debug;
	}

	private void printDebug(String message) {
		if (debug) System.out.println(message);
	}

	/*
	 * builds the transposed graph of inverse constraints.
	 * first, creates an inverse constraint for every print constraint.
	 * second, sets the internal next and arg references to the correct inverse constraint.
	 */
	public void build() {

		boolean localDebug = false;

		List<Integer> args;

		// create inverse constraint for every print constraint
		// op was set during the forward graph construction
		for (PrintConstraint pc : allConstraints.values()) {

			int ID = pc.getId();
			Operation op = pc.getOp();
			String value = pc.getActualVal();
			List<Integer> argList = pc.getArgList();
			printDebug("ID " + ID + " op " + op);
			I_Inv_Constraint<T> newConstraint;

			switch (op) {

				case INIT:

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}


					break;

				case INIT_CON:

					newConstraint = new InvConstraintConcreteValue<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case INIT_SYM:

					newConstraint = new InvConstraintInput<T>(ID, invSolver, value);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				//TODO: handle other predicates
				case IS_EMPTY:
				case STARTS_WITH:
				case ENDS_WITH:
				case PREDICATE:

					boolean result = value.equals("true") ? true : false;
					newConstraint = new InvConstraintPredicate<T>(ID, invSolver, result);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case EQUALS:
					boolean output = value.equals("true") ? true : false;
					newConstraint = new InvConstraintEquals<T>(ID, invSolver, output);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case CONTAINS:
					newConstraint = new InvConstraintContains<T>(ID, invSolver, value.equals("true"));
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case PROPAGATION:

					newConstraint = new InvConstraintPropagation<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case CONCAT_SYM:

					newConstraint = new InvConstraintConcatSym<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case CONCAT_CON:

					// FIX
					// **** Using symbolic code for now, needs concrete ported to r3
					newConstraint = new InvConstraintConcatSym<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case TOUPPERCASE:

					newConstraint = new InvConstraintToUpperCase<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case TOLOWERCASE:

					newConstraint = new InvConstraintToLowerCase<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case SUBSTR_STRT_END:

					args = pc.getArgList();
					newConstraint = new InvConstraintSubStringStartEnd<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case SET_LENGTH:

					args = pc.getArgList();
					newConstraint = new InvConstraintSetLength<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case SUBSTRING_START:

					args = pc.getArgList();
					newConstraint = new InvConstraintSubStringStart<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;


				case DELETE_START_END:

					args = pc.getArgList();
					newConstraint = new InvConstraintDeleteStartEnd<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case DELETE_CHAR_AT:

					args = pc.getArgList();
					newConstraint = new InvConstraintDeleteCharAt<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;

				case REPLACE_CHAR_CHAR:

					args = pc.getArgList();
					newConstraint = new InvConstraintReplaceCharChar<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
					}

					break;

				case REPLACE_FIRST:

					args = pc.getArgList();
					newConstraint = new InvConstraintReplaceFirst<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
					}

					break;
				case REPLACE_ALL: // unsure why we need seperate cases for these?
					args = pc.getArgList();
					newConstraint = new InvConstraintReplaceAll<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
					}

					break;
				case CHAR_AT:
					args = pc.getArgList();
					newConstraint = new InvConstraintCharAt<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
					}

					break;
				case INDEX_OF:
					args = pc.getArgList();
					newConstraint = new InvConstraintIndexOf<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
					}

					break;
				case TRIM:
					newConstraint = new InvConstraintTrim<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);
					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}
					break;
				case INSERT:
					args = pc.getArgList();
					newConstraint = new InvConstraintInsert<T>(ID, invSolver, args);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId() + " " + args);
					}

					break;
				case REVERSE:
					newConstraint = new InvConstraintReverse<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;
				case LENGTH:
					newConstraint = new InvConstraintLength<T>(ID, invSolver);
					allInverseConstraints.put(ID, newConstraint);

					if (localDebug) {
						System.out.println("processed " + op.toString() + "  " + pc.getId());
					}

					break;
				default:

					if (localDebug) {
						System.out.println("WARNING: Unhandled constraint type... " + op.toString() + "  " + pc.getId() + "  " + pc.getValue());
					}
					System.err.println("WARNING: Unhandled constraint in Reporter_Inverse.buildICG_r3() of type... " + op.toString() + "  " + pc.getId() + "  " + pc.getValue());


			} // end switch


		} // end for each printconstraint


		// all inverse constraints have been created, now we set the next and arg constraint references
		for (PrintConstraint pc : allConstraints.values()) {

			if (pc.getOp() != Operation.UNDEFINED) {
				// each print constraint has a corresponding inverse constraint, get a reference to it
				I_Inv_Constraint<T> invConstraint = allInverseConstraints.get(pc.getId());

				// the next inverse constraint to evaluate is the base of printconstraint
				I_Inv_Constraint<T> nextConstraint = allInverseConstraints.get(pc.getBase());
				if (nextConstraint != null) {
					invConstraint.setNext(nextConstraint);
				}


				// get the arglist and check if not empty
				List<Integer> argList = pc.getArgList();


				// MJR some operations only have concrete arguments and the value in args are the actual values and NOT constraint IDs.
				// nps: this is no longer true, we handle all args symbolically , though integers not fully/properly yet
				// TODO: revisit this and always handle constraint
				if (!argList.isEmpty() && pc.getOp() != Operation.SUBSTR_STRT_END &&
						pc.getOp() != Operation.SUBSTRING_START &&
						pc.getOp() != Operation.SET_LENGTH &&
//                        pc.getOp() != Operation.REPLACE_CHAR_CHAR &&
						pc.getOp() != Operation.DELETE_START_END) {


					// argument present, set invConstraint argument reference.
					int arg = argList.get(0);
					if (arg != -1) {

						if (localDebug) {
							System.out.println("pcID: " + pc.getId() + " getting arg: " + arg);
						}


						if (invConstraint.getOp() != Operation.CHAR_AT) {// TODO: nps i should probably revisit this
							if (invConstraint.getOp() == Operation.INSERT) {
								invConstraint.setArg(allInverseConstraints.get(argList.get(1)));
							} else {
								invConstraint.setArg(allInverseConstraints.get(arg));
							}
						}
					} // end if
					if (argList.size() == 2) { // this shouldnt ever happen anymore as we handle replace first and all symboliclally - nps - 04/16/2025
						if (pc.getOp() == Operation.REPLACE_FIRST || pc.getOp() == Operation.REPLACE_ALL) {
							int arg2 = argList.get(1);
							if (arg2 != -1) {
								invConstraint.setArg2(allInverseConstraints.get(arg2));
							}
						}
					}
//					if (pc.getOp() == Operation.REPLACE_CHAR_CHAR) {
//						int arg2 = argList.get(1);
//						if (arg2 != -1) {
//							invConstraint.setArg2(allInverseConstraints.get(arg2));
//						}
//					}

				} // end if

			} // end if
			else {
				System.err.println("WARNING: UNDEFINED constraint in Reporter_Inverse.buildICG_r3() of type... " + pc.getId() + "  " + pc.getValue());
//                System.exit(1);
			}

		}  // end for each printconstraint

		//populate previous constraints -- need for BFS
		//get all parents for the particular node
		for (I_Inv_Constraint<T> p : allInverseConstraints.values()) {

			HashSet<I_Inv_Constraint<T>> invParents = new HashSet<I_Inv_Constraint<T>>();
			for (SymbolicEdge e : graph.outgoingEdgesOf(allConstraints.get(p.getID()))) {
				PrintConstraint source = (PrintConstraint) e.getATarget();
				//add to p's incoming set
				I_Inv_Constraint<T> invSource = allInverseConstraints.get(source.getId());
				//it could be null since it has not been processed yet
				if (invSource != null) {
					invParents.add(allInverseConstraints.get(source.getId()));
				}

			}
			printDebug("Parents  " + p + " are " + invParents);
			I_Inv_Constraint<T> invP = allInverseConstraints.get(p.getID());
			invP.setPrev(invParents);
		}

	} // end build
}
