package edu.boisestate.cs.automatonModel;

import dk.brics.automaton.*;
import dk.brics.string.stringoperations.*;
import edu.boisestate.cs.Alphabet;
import edu.boisestate.cs.automaton.AutomatonHelper;

import static edu.boisestate.cs.automaton.AutomatonHelper.LogicalTransition;

import edu.boisestate.cs.automatonModel.operations.*;
import edu.boisestate.cs.util.Quadruple;
import edu.boisestate.cs.util.Tuple;

import java.math.BigInteger;
import java.util.*;

/**
 * @author
 */
public class Model_Acyclic_Inverse extends A_Model_Inverse<Model_Acyclic_Inverse> {


	private Automaton automaton;

	/**
	 * Constructor 1: Requires *ACYCLIC* automata as argument. <br>
	 * For use within Model_Acyclic_Manager <br>
	 * Has no safeguards against incorrect automata being passed. <br>
	 *
	 * @param automaton   - ACYCLIC Automaton
	 * @param alphabet    - Alphabet
	 * @param boundLength - Initial bound, should match Automaton length
	 */
	protected Model_Acyclic_Inverse(Automaton automaton, Alphabet alphabet, int boundLength) {

		super(alphabet, boundLength);

		this.automaton = automaton;

		this.modelManager = new Model_Acyclic_Inverse_Manager(alphabet, boundLength);
	}

	/**
	 * @param automaton
	 * @param alphabet
	 */
	protected Model_Acyclic_Inverse(Automaton automaton, Alphabet alphabet) {

		super(alphabet, 0);

		this.automaton = automaton;

		this.modelManager = new Model_Acyclic_Inverse_Manager(alphabet, 0);
	}

	public String getAutomaton() {
		return automaton.toString();
	}

	private static Automaton getAutomatonFromAcyclicModel(Model_Acyclic_Inverse model) {
		return model.automaton;
	}


	@Override
	public Model_Acyclic_Inverse assertContainedInOther(Model_Acyclic_Inverse containingModel) {
		//ensureAcyclicModel(containingModel);

		// get containing automaton
		Automaton containing = getAutomatonFromAcyclicModel(containingModel);

		// if either automata is  empty
		if (this.automaton.isEmpty() || containing.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get all substrings
		Automaton substrings = performUnaryOperation(containing, new Substring(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(substrings);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertContainsOther(Model_Acyclic_Inverse containedModel) {
		//ensureAcyclicModel(containedModel);

//		int padding = Math.max(0, this.boundLength - containedModel.boundLength);
		// create any string automata
		Automaton anyString1 =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();
		Automaton anyString2 =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();
		// concatenate with contained automaton
		Automaton contained = getAutomatonFromAcyclicModel(containedModel);
		Automaton x = anyString1.concatenate(contained).concatenate(anyString2);

		// get resulting automaton
		Automaton result = this.automaton.intersection(x);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertEmpty() {
		// get resulting automaton
		Automaton result = this.automaton.intersection(BasicAutomata.makeEmptyString());

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, 0);
	}

	@Override
	public Model_Acyclic_Inverse assertEndsOther(Model_Acyclic_Inverse containingModel) {
		//ensureAcyclicModel(containingModel);

		// get containing automaton
		Automaton containing = getAutomatonFromAcyclicModel(containingModel);

		// if either automata is  empty
		if (this.automaton.isEmpty() || containing.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get all suffixes
		Automaton suffixes = performUnaryOperation(containing, new Postfix(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(suffixes);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse assertEndsWith(Model_Acyclic_Inverse endingModel) {
		//ensureAcyclicModel(endingModel);

		// create any string automata
		Automaton anyString =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

		// concatenate with ending automaton
		Automaton end = getAutomatonFromAcyclicModel(endingModel);
		Automaton x = anyString.concatenate(end);

		// get bounded resulting automaton
		Automaton result = this.automaton.intersection(x);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertEquals(Model_Acyclic_Inverse equalModel) {
		//ensureAcyclicModel(equalModel);

		// concatenate with contained automaton
		Automaton equal = getAutomatonFromAcyclicModel(equalModel);

		// get resulting automaton
		Automaton result = this.automaton.intersection(equal);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, Math.min(this.boundLength, equalModel.boundLength));
	}

	@Override
	public Model_Acyclic_Inverse assertEqualsIgnoreCase(Model_Acyclic_Inverse equalModel) {
		//ensureAcyclicModel(equalModel);

		// concatenate with contained automaton
		Automaton equal = getAutomatonFromAcyclicModel(equalModel);
		Automaton equalIgnoreCase = performUnaryOperation(equal, new IgnoreCase(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(equalIgnoreCase);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertHasLength(int min, int max) {
		// check min and max
		if (min > max) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get any string with length between min and max
		Automaton minMax = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat(min, max);

		// get resulting automaton
		Automaton result = this.automaton.intersection(minMax);

		// get new bound length
		int newBoundLength = max;
		if (this.boundLength < max) {
			newBoundLength = this.boundLength;
		}

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	@Override
	public void removeEmptyString() {
		this.automaton.getInitialState().setAccept(false);
	}

	@Override
	public Model_Acyclic_Inverse resolveNotContains(Model_Acyclic_Inverse arg) {
		Model_Acyclic_Inverse temp = arg.clone();
		temp.createDisjunct();
		return this.assertNotContainsOther(temp);
	}

	// Returns a model where no substring from notContainingModel is present in this model
//	@Override
//	public Model_Acyclic_Inverse assertNotContainedInOther(Model_Acyclic_Inverse notContainingModel) {
//		Automaton notContaining = getAutomatonFromAcyclicModel(notContainingModel);
//
//		if (notContaining.isEmpty() || automaton.isEmpty()) {
//			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
//		}
//
//		// Step 1: Get all substrings of notContainingModel
//		Automaton substrings = performUnaryOperation(notContaining, new Substring(), this.alphabet);
//
//		// Step 2: Find all strings in this.automaton that contain any forbidden substring
//		Automaton forbidden = this.automaton.intersection(substrings);
//
//		// Step 3: Remove those from the original automaton
//		Automaton result = this.automaton.minus(forbidden);
//
//		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
//	}
	@Override
	public Model_Acyclic_Inverse assertNotContainedInOther(Model_Acyclic_Inverse notContainingModel) {
		//ensureAcyclicModel(notContainingModel);

		// get containing automaton
		Automaton notContaining = getAutomatonFromAcyclicModel(notContainingModel);

		// if not containing automaton is  empty
		if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// get automaton of required chars from not containing automaton
//		notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

		Automaton result = automaton;
//		if (!notContaining.isEmpty()) {

		// get all substrings
		Automaton substrings = performUnaryOperation(notContaining,
				new Substring(),
				this.alphabet);

		// get resulting automaton
		result = this.automaton.minus(substrings);
//		}

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

//	@Override
//	public Model_Acyclic_Inverse assertNotContainsOther(Model_Acyclic_Inverse notContainedModel) {
//		Automaton notContained = getAutomatonFromAcyclicModel(notContainedModel);
//
//		if (notContained.isEmpty() || automaton.isEmpty()) {
//			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
//		}
//
//		int bound = this.boundLength;
//		int minPatternLength = notContainedModel.calculateMinBoundLength();
//		Automaton forbidden = BasicAutomata.makeEmpty();
//
//		for (int prefixLen = 0; prefixLen <= bound - minPatternLength; prefixLen++) {
//			int suffixLen = bound - minPatternLength - prefixLen;
//			Automaton prefix = BasicAutomata.makeCharSet(alphabet.getCharSet()).repeat(prefixLen, prefixLen);
//			Automaton suffix = BasicAutomata.makeCharSet(alphabet.getCharSet()).repeat(prefixLen+minPatternLength, suffixLen);
//			Automaton candidate = prefix.concatenate(notContained).concatenate(suffix);
//			forbidden = forbidden.union(candidate);
//		}
//		forbidden.minimize();
//
//		Automaton result = this.automaton.minus(forbidden);
//		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
//	}

	public Model_Acyclic_Inverse assertNotContainsOther(Model_Acyclic_Inverse notContainedModel) {
		//ensureAcyclicModel(notContainedModel);

		// get not contained automaton
		Automaton notContained = getAutomatonFromAcyclicModel(notContainedModel);

		// if not containing automaton is  empty
		if (notContained.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		if (notContainedModel.isSingleton() && notContainedModel.boundLength > this.boundLength) { // if contained is longer than this then impossible to be contained
			return new Model_Acyclic_Inverse(this.automaton, this.alphabet, this.boundLength);
		}


		// gets automaton of required chars if any
//        notContained = getRequiredCharAutomaton(notContained, alphabet, boundLength);

		Automaton result = automaton;
//        if (!notContained.isEmpty()) {
		// create any string automata
		Automaton anyString1 =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet())
						.repeat();
		Automaton anyString2 =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet())
						.repeat();

		// concatenate with not contained automaton
		Automaton x = anyString1.concatenate(notContained)
				.concatenate(anyString2);

		// get resulting automaton
		result = this.automaton.minus(x);
//        }

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertNotEmpty() {
		// get resulting automaton
		Automaton result = this.automaton.minus(BasicAutomata.makeEmptyString());

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertNotEndsOther(Model_Acyclic_Inverse notContainingModel) {
		//ensureAcyclicModel(notContainingModel);

		// get containing automaton
		Automaton notContaining = getAutomatonFromAcyclicModel(notContainingModel);
		this.automaton.getInitialState().setAccept(false);

		// if not containing automaton is  empty
		if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// get automaton of required chars from not containing automaton
//		notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

		Automaton result = automaton;
//		if (!notContaining.isEmpty()) {

		// get all suffixes
		Automaton suffixes = performUnaryOperation(notContaining,
				new Postfix(),
				this.alphabet);

		// get resulting automaton
		result = this.automaton.minus(suffixes);
//		} else {
//			Automaton choice = notContainingModel.clone().createDisjunct().automaton; // chooose some model
//			Automaton suffixes = performUnaryOperation(choice,
//					new Postfix(),
//					this.alphabet);
//			result = this.automaton.minus(suffixes);
//		}
		result.getInitialState().setAccept(false);
		result.minimize();
		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse assertNotEndsWith(Model_Acyclic_Inverse notEndingModel) {
		//ensureAcyclicModel(notEndingModel);

		Automaton notEnding = getAutomatonFromAcyclicModel(notEndingModel);
		notEnding.getInitialState().setAccept(false); // by definition

		// if not containing automaton is  empty
		if (notEnding.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

//		notEnding = getRequiredCharAutomaton(notEnding, alphabet, boundLength);

		Automaton result = automaton;
//		if (!notEnding.isEmpty()) {

		// create any string automata
		Automaton anyString =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet())
						.repeat(0, this.boundLength);

		// concatenate with not ending automaton
		Automaton x = anyString.concatenate(notEnding);

		// get resulting automaton
		result = this.automaton.minus(x);
//		} else {
//			Automaton choice = notEndingModel.clone().createDisjunct().automaton; // chooose some model
//			Automaton anyString =
//					BasicAutomata.makeCharSet(this.alphabet.getCharSet())
//							.repeat(0,this.boundLength);
//			Automaton x = anyString.concatenate(choice);
//			result = this.automaton.minus(x);
//		}
		result.minimize();
		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	/**
	 * Creates a disjunct pair of models from this model.
	 * Manipulates this model in place and returns the remainder
	 *
	 * @return Model_Acyclic_Inverse - the other disjunct model
	 */
	public Model_Acyclic_Inverse createDisjunct() {
		Tuple<Automaton, HashMap<State, State>> a = this.cloneWithMap();
		Automaton other = a.get1();
		HashMap<State, State> stateMap = a.get2();
		LinkedList<State> search = new LinkedList<>();
		HashSet<State> visited = new HashSet<>();
		search.add(automaton.getInitialState());
		boolean found = false;
		// we'll iterate through the states until there is a branch and split there.
		while (!search.isEmpty()) {
			State current = search.removeFirst();
			List<LogicalTransition> lTs = AutomatonHelper.getLogicalTransitions(current);
			if (lTs.size() > 1) {
				// we can branch, so have one model take branch and remove branch from other
				LogicalTransition branch = lTs.get(0); // also the largest transition
				Set<Transition> currTrans = current.getTransitions();
				for (Transition t : branch.getTransitions()) {
					currTrans.remove(t);
				}
				// now we've remove transition in current and need to remove all toher transitions from other
				State onlyDest = stateMap.get(branch.getDestination());
				Set<Transition> otherTrans = stateMap.get(current).getTransitions();
				Set<Transition> removals = new HashSet<>();
				for (Transition t : otherTrans) {
					if (t.getDest() != onlyDest) {
						removals.add(t);
					}
				}
				otherTrans.removeAll(removals);
				found = true;
				break;
			}
			visited.add(current);
			// if we are here then there was only one branch of transitions i.e. one dest
			if (current.getTransitions().iterator().hasNext()) {
				search.add(current.getTransitions().iterator().next().getDest());
			}
		}
		// this means theres only one edge at each state so we will split as soon as we can on character
		// the logical is also easier
		if (!found) {
			printDebug("No Logical Disjunct Pair found, using characters instead");
			search.add(automaton.getInitialState());
			Set<Transition> newTrans = new HashSet<>();
			Set<Transition> otherNewTrans = new HashSet<>();
			while (!search.isEmpty()) {
				State current = search.removeFirst();
				Set<Transition> transitions = current.getTransitions();
				Set<Transition> otherTransitions = stateMap.get(current).getTransitions();
				Iterator<Transition> it = transitions.iterator();
				Iterator<Transition> it2 = otherTransitions.iterator();
				Transition sample = (Transition) (transitions.toArray()[0]);
				if (transitions.size() == 1 && sample.getMin() == sample.getMax()) {
					//no null check as aut is not singleton
					search.add(sample.getDest());
					continue;
				}
				int charTotal = 0;
				for (Transition t : transitions) {
					charTotal += (t.getMax() - t.getMin() + 1);
				}
				int charCount = 0;
				while (it.hasNext()) {
					Transition t = it.next();
					Transition ot = it2.next();
					int charT = t.getMax() - t.getMin() + 1;
					if (charCount < charTotal / 2) {
						if (charCount + charT < charTotal / 2) {
							newTrans.add(t);
							charCount += charT;
						} else {
							int diff = (charTotal / 2) - charCount;
							Transition newT = new Transition(t.getMin(), (char) (t.getMin() + diff - 1), t.getDest());
							Transition newOT = new Transition((char) (ot.getMin() + diff), ot.getMax(), ot.getDest());
							newTrans.add(newT);
							otherNewTrans.add(newOT);
							charCount += diff;
						}
					} else {
						otherNewTrans.add(ot);
					}
				}
				current.setAccept(false);// if they accept empty need one not to
				transitions.clear();
				otherTransitions.clear();
				transitions.addAll(newTrans);
				otherTransitions.addAll(otherNewTrans);
			}
		}
		//sanity check (testing!)
		// TODO: remove
		Automaton check = this.automaton.intersection(other);
		if (!check.isEmpty()) {
			System.err.println("NOT A DISJUNCT PAIR in createDisjunct");
			System.exit(1);
		}

		return new Model_Acyclic_Inverse(other, alphabet, boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertNotEquals(Model_Acyclic_Inverse notEqualModel) {
		//ensureAcyclicModel(notEqualModel);

		// get not equal automaton
		Automaton notEqual = getAutomatonFromAcyclicModel(notEqualModel);

		// if not containing automaton is  empty
		if (notEqual.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		Automaton result = automaton.minus(notEqual);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertNotEqualsIgnoreCase(Model_Acyclic_Inverse notEqualModel) {
		//ensureAcyclicModel(notEqualModel);

		// get not equal automaton
		Automaton notEqual = getAutomatonFromAcyclicModel(notEqualModel);

		// if not containing automaton is  empty
		if (notEqual.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// if not equal automaton is a singleton
		Automaton result = automaton;
		if (notEqual.getFiniteStrings(1) != null) {
			Automaton equalIgnoreCase = performUnaryOperation(notEqual,
					new IgnoreCase(),
					this.alphabet);

			// get resulting automaton
			result = this.automaton.minus(equalIgnoreCase);
		}

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertNotStartsOther(Model_Acyclic_Inverse notContainingModel) {
		//ensureAcyclicModel(notContainingModel);

		// get containing automaton
		Automaton notContaining = getAutomatonFromAcyclicModel(notContainingModel);

		// if not containing automaton is  empty
		if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// get automaton of required chars from not containing automaton
//		notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

		Automaton result = automaton;
//		if (!notContaining.isEmpty()) {

		// get all prefixes
		Automaton prefixes = performUnaryOperation(notContaining,
				new Prefix(),
				this.alphabet);

		// get resulting automaton
		result = this.automaton.minus(prefixes);
//		} else {
//			Automaton choice = notContainingModel.clone().createDisjunct().automaton; // chooose some model
//			Automaton prefixes = performUnaryOperation(choice,
//					new Prefix(),
//					this.alphabet);
//			result = this.automaton.minus(prefixes);
//		}
		result.getInitialState().setAccept(false);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse assertNotStartsWith(Model_Acyclic_Inverse notStartsModel) {
		//ensureAcyclicModel(notStartsModel);

		Automaton notStarting = getAutomatonFromAcyclicModel(notStartsModel);
		notStarting.getInitialState().setAccept(false); // by definition

		// if not containing automaton is  empty
		if (notStarting.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
		}

//		notStarting = getRequiredCharAutomaton(notStarting, alphabet, boundLength);

		Automaton result = automaton;
//		if (!notStarting.isEmpty()) {
		// create any string automata
		Automaton anyString =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet())
						.repeat();

		// concatenate with not starts automaton
		Automaton x = notStarting.concatenate(anyString);

		// get resulting automaton
		result = this.automaton.minus(x);
//		} else {
//			Automaton choice = notStartsModel.clone().createDisjunct().automaton; // chooose some model
//			Automaton anyString =
//					BasicAutomata.makeCharSet(this.alphabet.getCharSet())
//							.repeat(0,this.boundLength);
//			Automaton x = choice.concatenate(anyString);
//			result = this.automaton.minus(x);
//		}

		result.minimize();
		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse assertStartsOther(Model_Acyclic_Inverse containingModel) {
		//ensureAcyclicModel(containingModel);

		// get containing automaton
		Automaton containing = getAutomatonFromAcyclicModel(containingModel);

		// if either automata is  empty
		if (this.automaton.isEmpty() || containing.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get all prefixes
		Automaton prefixes = performUnaryOperation(containing, new Prefix(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(prefixes);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse assertStartsWith(Model_Acyclic_Inverse startingModel) {
		//ensureAcyclicModel(startingModel);

		// create any string automata
		Automaton anyString =
				BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

		// concatenate with contained automaton
		Automaton start = getAutomatonFromAcyclicModel(startingModel);
		Automaton x = start.concatenate(anyString);

		// get resulting automaton
		Automaton result = this.automaton.intersection(x);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse clone() {
		// create new model from existing automata
		Automaton cloneAutomaton = this.automaton.clone();
		return new Model_Acyclic_Inverse(cloneAutomaton,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse concatenate(Model_Acyclic_Inverse argModel) {
		//ensureAcyclicModel(argModel);

		// get arg automaton
		Automaton arg = getAutomatonFromAcyclicModel(argModel);

		// get concatenation of automata
		Automaton result = this.automaton.concatenate(arg);

		// minimize result automaton
//        result.reduce(); //?
		result.determinize();
		result.minimize();

		// calculate new bound length
		int boundLength = this.boundLength + argModel.boundLength;

		// return bounded model from automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, boundLength);
	}

	@Override
	public boolean containsString(String actualValue) {
		return this.automaton.run(actualValue);
	}


	@Override
	public Model_Acyclic_Inverse delete(int start, int end) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new PreciseDelete(start, end), this.alphabet);

		// determine new bound length
		int newBoundLength;
		if (this.boundLength < start) {
			newBoundLength = 0;
		} else if (this.boundLength < end) {
			newBoundLength = start;
		} else {
			int charsDeleted = end - start;
			newBoundLength = this.boundLength - charsDeleted;
		}

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	@Override
	public boolean equals(Model_Acyclic_Inverse arg) {

//        // check if arg model is bounded automaton model
//        if (arg instanceof Model_Acyclic_Inverse) {
//
//            // cast arg model
//            Model_Acyclic_Inverse argModel = (Model_Acyclic_Inverse) arg;
//
//            // check underlying automaton models for equality
//            return this.automaton.equals(argModel.automaton);
//        }
//
//        return false;

		// MJR casts are no longer needed...
		return this.automaton.equals(arg.automaton);


	}

	@Override
	public String getAcceptedStringExample() {
		return this.automaton.getShortestExample(true);
	}

	@Override
	public Set<String> getFiniteStrings() {
		// return finite strings from automaton
		System.out.println("WARNING: calling getFiniteStrings()");
		return automaton.getFiniteStrings();
	}

	public Set<String> getFiniteStrings(int limit) {
		// return finite strings from automaton
		return automaton.getFiniteStrings(limit);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Model_Acyclic_Inverse: ");
		sb.append("alphabet: ");
		sb.append(this.alphabet.toString());
		sb.append(" boundLength: ");
		sb.append(this.boundLength);
		sb.append(" automaton: ");
		sb.append(this.automaton.toString());
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public Model_Acyclic_Inverse insert(int offset, Model_Acyclic_Inverse argModel) {

		PreciseInsert insert = new PreciseInsert(offset);
		Automaton arg = getAutomatonFromAcyclicModel(argModel);

		// calculate new bound length
		// note this is rudimentary and also underapproximates the possibilities
		// could/should be analyzing the combinations of the two models and their lengths that are possible.
		// i guess that would be concatenating them and then limiting the length (need new method)
//        if (argModel.boundLength + this.boundLength > maxBoundLength) {
//            // if new bound length exceeds max bound length
//            Model_Acyclic_Inverse any = new Model_Acyclic_Inverse(BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat(0, maxBoundLength), this.alphabet, maxBoundLength);
//            if (argModel.equals(any)) {
//                arg = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat(0, maxBoundLength - this.boundLength);
//                argModel = new Model_Acyclic_Inverse(arg, this.alphabet, maxBoundLength - this.boundLength);
//            } else if (this.equals(any)) {
//                // if this model is any, return arg model
//                Automaton result = insert.op(BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat(0, maxBoundLength - argModel.boundLength), arg);
//                result.minimize();
//                return new Model_Acyclic_Inverse(result, this.alphabet, maxBoundLength);
//            } else {
//                System.err.println("WARNING: Model_Acyclic_Inverse.insert() exceeds max bound");
//                System.exit(1);
//            }
//        }

		// get automata for operations

		// get resulting automaton
		Automaton result = insert.op(automaton, arg);
		result.minimize();

		int newBoundLength = this.boundLength + argModel.boundLength;

		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic_Inverse intersect(Model_Acyclic_Inverse arg) {
		//ensureAcyclicModel(arg);

		// cast arg model
		Model_Acyclic_Inverse argModel = (Model_Acyclic_Inverse) arg;

		// get intersection of automata
		Automaton result = this.automaton.intersection(argModel.automaton);

		// minimize result automaton
		result.minimize();

		// calculate new bound length
		int boundLength = this.boundLength;
		if (argModel.boundLength < this.boundLength) {
			boundLength = argModel.boundLength;
		}

		// return bounded model from automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, boundLength);
	}

	@Override
	public boolean isEmpty() {
		/* eas 10-31-18 why, why is EmptyString() ??? */
		//return this.automaton.isEmptyString();
		//the correct code
		return this.automaton.isEmpty();

	}

	@Override
	public boolean isSingleton() {
		// get one finite string, null if more
		Set<String> strings = this.automaton.getFiniteStrings(1);

		// return if single non-null string in automaton
		return strings != null &&
				strings.size() == 1 &&
				strings.iterator().next() != null;
	}

	@Override
	public BigInteger modelCount() {
		// return model count of automaton
		return StringModelCounter.ModelCount(automaton);
	}

	@Override
	public Model_Acyclic_Inverse replace(char find, char replace) {
		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace1(find, replace), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse replace(String find, String replace) {

		// perform operation
		Replace6 replaceOp = new Replace6(find, replace);
		Automaton result = performUnaryOperation(automaton, replaceOp, this.alphabet);

		// determine new bound length
		int boundDiff = find.length() - replace.length();
		int newBoundLength = this.boundLength - boundDiff;

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic_Inverse replaceChar() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace4(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse replaceFindKnown(char find) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace2(find), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse replaceReplaceKnown(char replace) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace3(replace), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse reverse() {
		// if automaton is empty
		if (this.automaton.isEmpty()) {
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Reverse(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse setCharAt(int offset, Model_Acyclic_Inverse argModel) {
		//ensureAcyclicModel(argModel);

		// get automata for operations
		Automaton arg = getAutomatonFromAcyclicModel(argModel);

		// get resulting automaton
		PreciseSetCharAt operation = new PreciseSetCharAt(offset);
		Automaton result = operation.op(automaton, arg);
		result.minimize();

		// return unbounded model from automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, boundLength);
	}

	@Override
	public Model_Acyclic_Inverse setLength(int length) {

		// add null to new alphabet
		Set<Character> symbolSet = alphabet.getSymbolSet();
		symbolSet.add('\u0000');
		Alphabet newAlphabet = new Alphabet(symbolSet);

		// get resulting automaton
		Automaton result = performUnaryOperation(automaton,
				new PreciseSetLength(length),
				newAlphabet);

		// return unbounded model from automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, length);
	}


	@Override
	public Model_Acyclic_Inverse substring(int start, int end) {
		// get resulting automaton
		Automaton result = performUnaryOperation(automaton, new PreciseSubstring(start, end), this.alphabet);

		// get new bound length
		int newBoundLength = end - start;

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic_Inverse suffix(int start) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new PreciseSuffix(start), this.alphabet);

		// determine new bound length
		int newBoundLength = this.boundLength - start;

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	public Model_Acyclic_Inverse prefix(int end) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new PrecisePrefix(end), this.alphabet);

		// determine new bound length
		int newBoundLength = end;
		if (this.boundLength < end) {
			newBoundLength = this.boundLength;
		}

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic_Inverse toLowercase() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new ToLowerCase(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse toUppercase() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new ToUpperCase(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse trim() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new PreciseTrim(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result,
				this.alphabet,
				this.boundLength);
	}

	@Override
	public Model_Acyclic_Inverse charAt(int index) {
		if (this.boundLength <= index) {
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for model with bound length " + this.boundLength);
		}

		// traverse automaton from start state to find each possible character at the given index
		Set<State> currentStates = new HashSet<>();
		currentStates.add(this.getAutomatonObject().getInitialState());
		for (int i = 0; i < index; i++) {
			Set<State> nextStates = new HashSet<>();
			for (State state : currentStates) {
				for (Transition transition : state.getTransitions()) {
					nextStates.add(transition.getDest());
				}
			}
			currentStates = nextStates;
		}
		//now we have each state after traversing index number of transitions
		// lets manually construct a new automaton
		Automaton result = new Automaton();
		State initialState = new State();
		result.setInitialState(initialState);
		State finalState = new State();

		finalState.setAccept(true);

		for (State state : currentStates) {
			for (Transition transition : state.getTransitions()) {
				Transition newTransition = new Transition(transition.getMin(), transition.getMax(), finalState);
				initialState.addTransition(newTransition);
			}
		}

		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	// this will output an index. so i guess we'll just treat it as a string but know that its an int because of it's source.. shuold probably have diff objects but we dont for concrete int args.
	public Model_Acyclic_Inverse indexOf(Model_Acyclic_Inverse find) {
		// TODO: actaully find possible index algo, similar to the replace search, look for every possible first match index
		Automaton numRange = BasicAutomata.makeEmpty();
		State start = numRange.getInitialState();
		State end = new State();
		end.setAccept(true);
		if (this.automaton.intersection(find.getAutomatonObject()).isEmpty()) {
			start.addTransition(new Transition('\uFFFF', end));// shuold return not found, i.e. -1 so using 65535
		} else {
			// similar to search of substring
			// one issue im seeing is how we know which is 'first' index, i.e. how we would choose between multiple matches at the same index

			// but for now we will just return the range of 0 to bound length as an automaton...
			for (int i = 0; i < this.boundLength; i++) {
				char c = (char) (i + '0'); // convert to char
				start.addTransition(new Transition(c, end));
			}
			// also have possiblity of no match, could check intersection is not equal to original but for now just propagate '-1'
			start.addTransition(new Transition('\uFFFF', end));
		}

		return new Model_Acyclic_Inverse(numRange, this.alphabet, find.boundLength);
	}

	// so this would take an index (in theory a range) and resolve a model that makes sure that is where find is first located.
	public Model_Acyclic_Inverse inv_indexOf(Model_Acyclic_Inverse find, int bound) {
		Automaton indexRange = this.getAutomatonObject();
		// search for empty possiblity, i.e. no match, i.e. -1 (i.e. 65535 is what we use for that)
		if (indexRange.isEmpty()) {
			throw new RuntimeException("Index range is empty in inv_indexOf");
		}
		// if find includes the emptyString we can just

		Transition choice = null;
		boolean includesNotFound = false;
		for (Transition t : indexRange.getInitialState().getTransitions()) {
			if (t.getMin() == '\uFFFF') { // i.e. 65535, i guess in theory it could be the max with a range, but thats basicaly impossiblein this frameowkr
				choice = t;
				includesNotFound = true;
				break;
			}
		}

		Automaton findAut = find.getAutomatonObject();
		String found = findAut.getShortestExample(true);// choose a simple find model, TODO: write max and min length helper methods for acyclic automata

		// the simplest solution when find is not found is for result to be the empty string, and find to be anything but the empty string
		if (includesNotFound && !found.isEmpty()) {
			indexRange.getInitialState().getTransitions().remove(choice); // remove for possible future use
			// find can be anyting
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmptyString(), this.alphabet, 0);
		} else if (includesNotFound && found.isEmpty()) { // i.e. find has empty
			findAut.getInitialState().setAccept(false); //remove empty (will still be kept in InvConstraint for later backtrackin gif necessary
			return new Model_Acyclic_Inverse(BasicAutomata.makeEmptyString(), this.alphabet, 0);
		}

		// just create a dummy automaton with find and padding that can be propogated and intersected, we assume it is not a range for now :), otherwise we could iterate and backtrack, yuck
		// TODO: similar to substring/replace algo, needs to allow anystring but a match, and then the findModel, and then anystring at all after.
		int index = (int) indexRange.getInitialState().getTransitions().iterator().next().getMin();// not sure how this will handle the -1 case tbh.
		index = index - 48; // proper conversion to int from
		if (index < 0 || index >= bound) {// not sure how to get the bound length of solving
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for model with bound length " + bound);
		}

		// TODO: need to actaully evaluate pre and suff given index choice and find choice. i.e. or result may be empty

		Automaton prefix = Automaton.makeCharSet(this.alphabet.getCharSet()).repeat(index, index);
		if (index != 0) { // find is at least of length 1, so length of result would be at least index + 1
			findAut.getInitialState().setAccept(false);
			found = findAut.getShortestExample(true);
		} else {
			prefix = BasicAutomata.makeEmptyString();
		}
		findAut = BasicAutomata.makeString(found);
		find.boundLength = found.length();
		// remove find from prefix, as it would otherwise have been found earlier
		// dont think this is proper, i.e. we do really need ot be like enumerating pairs of result/find
		if (index > 0) prefix = prefix.minus(findAut);
		// TODO: maybe should be doing length checks on everything and then for example adjusting find if index is high enough and find would go out of bounds....
		// if (index + find.boundLength > bound) {
		// we would want ot reomve the whoel findAut from find model because we already tried the shortest example. need ot backtrack on index?
		Automaton suffix = Automaton.makeCharSet(this.alphabet.getCharSet()).repeat(0, bound - index - find.boundLength);
		Automaton result = prefix.concatenate(findAut).concatenate(suffix);
		if (suffix.isEmpty()) { // i.e. becasuse find is long, it would make result empty
			result = prefix.concatenate(findAut); // maybe should be making prefix the correct size as well...
		}
		result.minimize();

		if (result.isEmpty()) {
			throw new RuntimeException("Resulting automaton is empty in inv_indexOf");
		}

		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}

	//TODO: init bound could still be too short if we've say done concatenation, would want nextConstraint bound
	public Model_Acyclic_Inverse inv_charAt(int index, int initBound) {
		// take incoming model and return new model that fills bound lengths from index with anyStrings
		// create two automaton, one from 0 to index, and one from index to bound length

		String alphabetCharSet = this.alphabet.getCharSet();
		Automaton start = Automaton.makeCharSet(alphabetCharSet).repeat(index, index);// looks at repeat docs
		Automaton end = Automaton.makeCharSet(alphabetCharSet).repeat(0, initBound - index);
		// concatenate the two automata with the incoming model
		Automaton result = start.concatenate(this.getAutomatonObject()).concatenate(end);

		return new Model_Acyclic_Inverse(result, this.alphabet, calculateBoundLength(result));
	}


	/**
	 * Currently assumes a concrete argument.
	 * <p>
	 * OVER-ESTIMATION - None with concrete argument, symbolic argument introduces spurious strings.
	 * UNDER-ESTIMATION - None with concrete argument
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_concatenate(Model_Acyclic_Inverse arg) {

		int argLength = arg.getBoundLength();
		int bound = this.getBoundLength();
		int originalLength = bound - argLength;

		Model_Acyclic_Inverse argModel;

		Automaton emptyAutomaton = Automaton.makeEmpty();
		Model_Acyclic_Inverse resModel = new Model_Acyclic_Inverse(emptyAutomaton, this.alphabet, originalLength);


		// isolate strings of length argLength + 1, + 2, etc.
		// argLength + 1 strings represent a 1 symbol prefix, so substring them and union to result, etc.
		for (int i = argLength + 1; i <= bound; i++) {
			argModel = this.modelManager.createAnyString(i);
			argModel = this.intersect(argModel);
			argModel = argModel.substring(0, i - argLength);
			resModel = resModel.union(argModel);
		}

		return resModel;
	}


	/**
	 * will return a valid prefix of concated base.arg from this model.
	 * <p>
	 * OVER-ESTIMATION - .
	 * UNDER-ESTIMATION - .
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_concatenate(Model_Acyclic_Inverse base, Model_Acyclic_Inverse arg) {

		//int suffixBound = arg.getBoundLength();			// symbolic, could be any length up to bound
		int resultLength = this.getBoundLength();        // should be a single string, length = bound
		int prefixBound = base.getBoundLength();        // symbolic, could be any length up to bound

		printDebug("attempting to split: ");
//		for (String s : this.getFiniteStrings()) {
//			System.out.println(s);
//		}

		Model_Acyclic_Inverse prefixModel = null;
		Model_Acyclic_Inverse suffixModel = null;

		boolean prefixFound = false;
		boolean noMatch = false;

		int prefixLength = 1;

		// slice up this.model looking for a valid combination of prefix/suffix
		// !! this has no checks for no match !!!
		while (!prefixFound && !noMatch) {
			prefixModel = this.substring(0, prefixLength);

//			for (String s : prefixModel.getFiniteStrings()) {
//				System.out.println("prefixModel: " + s);
//			}

			suffixModel = this.substring(prefixLength, resultLength);

//			for (String s : suffixModel.getFiniteStrings()) {
//				System.out.println("suffixModel: " + s);
//			}

			if (!base.intersect(prefixModel).isEmpty() && !arg.intersect(suffixModel).isEmpty()) {
				prefixFound = true;
			} else {
				prefixLength++;
				if (prefixLength > prefixBound) {
					noMatch = true;
				}
			}
		}

		if (noMatch) {
			System.out.println("ERROR: Could not find a valid prefix, returning last prefix tried...");
		}

		return prefixModel;
	}


	/**
	 * will return a valid prefix of concated base.arg from this model.
	 * <p>
	 * OVER-ESTIMATION - .
	 * UNDER-ESTIMATION - .
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse> inv_concatenate_sym(Model_Acyclic_Inverse base, Model_Acyclic_Inverse arg) {

		//int suffixBound = arg.getBoundLength();			// symbolic, could be any length up to bound
		int resultLength = this.getBoundLength();        // should be a single string, length = bound
		int prefixBound = base.getBoundLength();        // symbolic, could be any length up to bound

		System.out.println("attempting to split: ");
//		for (String s : this.getFiniteStrings()) {
//			System.out.println(s);
//		}

		Model_Acyclic_Inverse prefixModel = null;
		Model_Acyclic_Inverse suffixModel = null;

		boolean prefixFound = false;
		boolean noMatch = false;

		int prefixLength = 1;

		// slice up this.model looking for a valid combination of prefix/suffix
		// !! this has no checks for no match !!!
		while (!prefixFound && !noMatch) {
			prefixModel = this.substring(0, prefixLength);

//			for (String s : prefixModel.getFiniteStrings()) {
//				System.out.println("prefixModel: " + s);
//			}

			suffixModel = this.substring(prefixLength, resultLength);

//			for (String s : suffixModel.getFiniteStrings()) {
//				System.out.println("suffixModel: " + s);
//			}

			if (!base.intersect(prefixModel).isEmpty() && !arg.intersect(suffixModel).isEmpty()) {
				prefixFound = true;
			} else {
				prefixLength++;
				if (prefixLength > prefixBound) {
					noMatch = true;
				}
			}
		}

		if (noMatch) {
			System.out.println("ERROR: Could not find a valid prefix, returning last prefix tried...");
		}

		return new Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>(prefixModel, suffixModel);
	}


	/**
	 * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
	 * UNDER-ESTIMATION - None.
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_delete(int start, int end) {

		Model_Acyclic_Inverse anyString = modelManager.createAnyString(end - start);
		// set bound to be equal to actual length
		// make sure no index out of bounds, i.e. mark all states as reject
		anyString.setBoundLength(end - start);
		Set<State> states = anyString.getStatesOrdered();
		Automaton aut = anyString.getAutomatonObject();

		for (State state : states) {
			state.setAccept(false);
			if (state.getTransitions().isEmpty()) {
				state.setAccept(true);
			}
		}

		Model_Acyclic_Inverse result = this.insert(start, anyString);

		return result;
	}


	/**
	 * This inverse insert takes an offset which is the index the insert was performed at,
	 * and an argument model which is the model that was inserted. The 'this' model is the
	 * incoming model from backwards propogation. So we use the forward prop source model,
	 * the offset and the incoming model to determine prefixes, suffixes, and what remains, that give us
	 * what to propogate down to our source (result), and insertModel. So our suffix is what
	 * we are calling the model inserted, then we concatenate the prefix and remains models
	 * to get our result
	 *
	 * @param baseModel   forward model from source
	 * @param offset      index of insert
	 * @param insertModel forward model for insert
	 * @return Quadruple of prefix, insert, suffix, and remains models for propagation and backtracking for remains
	 * @author Nat Steven
	 * 6-23-25
	 */
	public Quadruple<Model_Acyclic_Inverse, Model_Acyclic_Inverse, Model_Acyclic_Inverse, Model_Acyclic_Inverse> inv_insert(Model_Acyclic_Inverse baseModel, Model_Acyclic_Inverse insertModel, int offset) {
		//TODO: bound length stuff?
		this.minimize();

		// get prefix and suffix pair for some pivot, and remove from this model
		Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse> preSuffPair = this.getPathConsistentPair(offset);
		Model_Acyclic_Inverse prefix = preSuffPair.get1();
		Model_Acyclic_Inverse suffixIn = preSuffPair.get2();

		prefix.minimize();
		suffixIn.minimize(); // neccesary?
		printDebug("INV_INSERT, trying: PREFIX (example): " + prefix.getShortestExampleString() + " SUFFIX (example): " + suffixIn.getShortestExampleString());

		if (!this.isEmpty()) {
			printDebug("MORE THAN ONE PATH FOUND IN INV_INSERT, CAN BACKTRACK");
			printDebug("Example string: " + this.getShortestExampleString());
		}

		//check prefix makes sense with baseModel

		Model_Acyclic_Inverse prefixTarg = baseModel.prefix(offset);
		prefix = prefix.intersect(prefixTarg); // TODO: intersects are expensive, check they are all necessary
		if (prefix.isEmpty()) {
			printDebug("PREFIX INTERSECTION FAILED IN INV_INSERT, BACKTRACKING"); //shouldn't happen?
			return null;
		}

		Model_Acyclic_Inverse suffixTarg = baseModel.suffix(offset);

		// so technically could do below as it performs same opp but it is indeed exhaustive for the original model, really should also adjust this to be greedy
//        List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>> insertCandidates = suffixIn.inv_concatenate_sym_all(insertModel, suffixTarg);
		Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse> insertCandidates = suffixIn.getPathConsistentPair(insertModel, suffixTarg);

		if (insertCandidates == null) {
			printDebug("NO CANDIDATES FOUND, given the prefix used, BACKTRACKING");
			return null;
		}

		Model_Acyclic_Inverse insert = insertCandidates.get1();
		Model_Acyclic_Inverse suffix = insertCandidates.get2();

//        suffixIn.minus(insert.concatenate(suffix)); // check there are other insert/suffix optinos to search
		// i guess a quad cause we do need the prefix that for later as well for suffixIn)
		Quadruple<Model_Acyclic_Inverse, Model_Acyclic_Inverse, Model_Acyclic_Inverse, Model_Acyclic_Inverse> ret
				= new Quadruple<>(prefix, insert, suffix, suffixIn);

		return ret;
	}

	/**
	 * Finds a pair of path-consistent prefix and suffix models at index i.
	 * This could be extended to find every path of length i that reaches that state to construct the prefix and corresonding suffix
	 * curretnly it just finds one path though in theory there could be multiple of the same length to the same state
	 *
	 * @param i index to pivot at
	 * @return tuple of prefix and suffix models, note also removes this pair from the underlying model/automaton
	 */
	public Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse> getPathConsistentPair(int i) {
		Automaton original = this.getAutomatonObject();
		Automaton suffix = original.clone();
		Automaton prefix = new Automaton();
		int index = 0;
		State current = suffix.getInitialState();
		State currPref = new State();
		prefix.setInitialState(currPref);
		while (index < i) {
			List<LogicalTransition> trans = AutomatonHelper.getLogicalTransitions(current);
			LogicalTransition t = trans.get(0); // should return logtrans with most trans
			State next = new State();
			for (Transition tr : t.getTransitions()) {
				currPref.addTransition(new Transition(tr.getMin(), tr.getMax(), next));
			}
			currPref = next;
			current = t.getDestination();
			index++;
		}
		currPref.setAccept(true);
		// now we have a prefix and the state to pivot at
		suffix.setInitialState(current); // this shuold leave us with only suffixes from this pivot
		suffix.minimize();
		Model_Acyclic_Inverse prefixModel = new Model_Acyclic_Inverse(prefix, this.alphabet, i);
		Model_Acyclic_Inverse suffixModel = new Model_Acyclic_Inverse(suffix, this.alphabet, calculateBoundLength(suffix));
		this.setAutomaton(original.minus(prefix.concatenate(suffix)));

		return new Tuple<>(prefixModel, suffixModel);
	}

	/**
	 * Returns a pair of path consistent automata representing a possible prefix and suffix pair
	 * of the original automaton that are subsets of the provided insert and suffix automata respectively.
	 * this is identical to inv_concatenate_sym_all but only returns one pair and removes it from the underlying model
	 *
	 * @param m1
	 * @param m2
	 * @return tuple of prefix and suffix models (note it removes this pair from the underlying model)
	 */
	@Override
	/// i use this from InvConstraintInsert (also is a greedy inv_concatenate_sym_all)
	public Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse> getPathConsistentPair(Model_Acyclic_Inverse m1, Model_Acyclic_Inverse m2) {
		Automaton aut1 = m1.getAutomatonObject();
		Automaton aut2 = m2.getAutomatonObject();
		Automaton original = this.getAutomatonObject();

		Tuple<Automaton, HashMap<State, State>> cloneMap = this.cloneWithMap();
		Automaton prefix = cloneMap.get1();
		HashMap<State, State> preMap = cloneMap.get2();
		Tuple<Automaton, HashMap<State, State>> cloneMap2 = this.cloneWithMap();
		Automaton suffix = cloneMap2.get1();
		HashMap<State, State> suffMap = cloneMap2.get2();

		for (State state : prefix.getStates()) {
			state.setAccept(false);
		}

		State current = original.getInitialState();
		ArrayList<State> search = new ArrayList<>();
		Set<State> visited = new HashSet<>();
		search.add(current);
		while (!search.isEmpty()) {
			current = search.remove(0);
			State preCurrent = preMap.get(current);
			preCurrent.setAccept(true);
			Automaton temp1 = prefix.clone();
			temp1.minimize();
			temp1 = temp1.intersection(aut1);

			if (!temp1.isEmpty()) {
				// good prefix so far
				State suffCurrent = suffMap.get(current);
				suffix.setInitialState(suffCurrent);
				Automaton temp2 = suffix.clone();
				temp2.minimize();
				temp2 = temp2.intersection(aut2);
				if (!temp2.isEmpty()) {
					// found a valid pair
					Model_Acyclic_Inverse prefixModel = new Model_Acyclic_Inverse(temp1, this.alphabet, calculateBoundLength(prefix));
					Model_Acyclic_Inverse suffixModel = new Model_Acyclic_Inverse(temp2, this.alphabet, calculateBoundLength(suffix));
					// setting underlying ot remove found pair
					this.setAutomaton(original.minus(prefix.concatenate(suffix)));
					//return found pair
					return new Tuple<>(prefixModel, suffixModel);
				} else {
					preCurrent.setAccept(false);
				}
				// no need to set back initial as we will just continue searching
			} else {
				preCurrent.setAccept(false);
			}

			visited.add(current);
//           List<LogicalTransition> trans = AutomatonHelper.getLogicalTransitions(current);
			for (Transition t : current.getTransitions()) {
				State dest = t.getDest();
				if (!visited.contains(dest) && !search.contains(dest)) {
					search.add(dest);
				}
			}


		}
		// TODO: only search within bounds corresponding to aut1 and aut2

		printDebug("NO CONSISTENT PAIR FOUND IN inv_insert");
		return null;
	}

	/**
	 * Clone with map from original states to new states
	 *
	 * @return cloned automaton and map
	 */
	Tuple<Automaton, HashMap<State, State>> cloneWithMap() {
		Automaton clone = new Automaton();
		HashMap<State, State> stateMap = new HashMap<>();
		for (State state : this.getAutomatonObject().getStates()) {
			State newState = new State();
			newState.setAccept(state.isAccept());
			stateMap.put(state, newState);
		}
		State init = this.getAutomatonObject().getInitialState();
		clone.setInitialState(stateMap.get(init));
		for (State state : this.getAutomatonObject().getStates()) {
			State newState = stateMap.get(state);
			for (Transition t : state.getTransitions()) {
				State dest = t.getDest();
				Transition newTrans = new Transition(t.getMin(), t.getMax(), stateMap.get(dest));
				newState.addTransition(newTrans);
			}
		}

		return new Tuple<>(clone, stateMap);
	}

//    /**
//     * Finds common suffixes between two automata, i.e. suffixes that are in both automata
//     * This is done by reversing both automata, intersecting them, and then reversing the result.
//     * need reference to underlying model to get alphabet
//     * @param a1 first automaton
//     * @param a2 second automaton
//     * @return automaton representing common suffixes
//     */
//    public Automaton commonSuffixes(Automaton a1, Automaton a2) {
//        HashMap<State, Boolean> prevAccept = new HashMap<>();
//
//        Automaton a1Rev = performUnaryOperation(a1, new Reverse(), this.alphabet);
//        Automaton a2Rev = performUnaryOperation(a2, new Reverse(), this.alphabet);
//
//        for (State state : a1Rev.getStates()) {
//            prevAccept.put(state, state.isAccept());
//            state.setAccept(true);
//        }
//        for (State state : a2Rev.getStates()) {
//            prevAccept.put(state, state.isAccept());
//            state.setAccept(true);
//        }
//
//        Tuple<HashMap<State,Tuple<State,State>>, Automaton> mapAutTuple = intersectWithMap(a1Rev, a2Rev);
//        Automaton intersection = a1Rev.intersection(a2Rev);
//        Automaton commonSuffixes = mapAutTuple.get2();
//        HashMap<State, Tuple<State, State>> stateMap = mapAutTuple.get1();
//        // sanity check TODO: remove, intersection take tiiime
//        if (!intersection.minus(commonSuffixes).isEmpty() || !commonSuffixes.minus(intersection).isEmpty()) {
//            System.err.println("WARNING: intersection check failed in inv_insert commonSuffixes");
//            System.exit(1);
//        }
//        for (State state : commonSuffixes.getStates()) {
//            Tuple<State, State> inputs = stateMap.get(state);
//            State s1 = inputs.get1();
//            State s2 = inputs.get2();
//            state.setAccept(prevAccept.get(s1) && prevAccept.get(s2));
//            //epsilon transitions to accept? i.e. throuhg the remains
//            if (state.getTransitions().isEmpty()) {
//                state.setAccept(true);
//            }
//        }
//        commonSuffixes = performUnaryOperation(commonSuffixes, new Reverse(), this.alphabet);
//        commonSuffixes.minimize();
//
//        return commonSuffixes;
//    }

//    /**
//     * Given a sub-automaton, an insert automaton, and the super-automaton (this), finds potential prefix suff pairs
//     * that are consistent with the insert and sub automata, pre concat suff = super
//     * where pre is a subset of insert and suff is a subset of sub
//     * Also removes the first instance found from the base/original automata (sup) for future search.
//     * @param sub sub-automaton to 'remove' from sup
//     * @return tuple of remaining prefix automaton and the corresponding suffix (potential insert, and targets suffix)
//     */
//    public Tuple<Automaton, Automaton> remainingPrefix(Automaton sub, Automaton insert) {
//        Automaton sup = this.getAutomatonObject().clone();
//
//        // one way to do this is iterate through our sup, and check if the prefix is in insert, and suffix is in sub?
//        // for now i will just check all but shuld be able to use insert bounds to limit search
//        // we will build our prefix as we go and then shuold be able to build the suffix after we find an insert match
//        LinkedList<State> stateSearch = new LinkedList<>();
//        stateSearch.add(sup.getInitialState());
//        while (!stateSearch.isEmpty()) {
//            State current = stateSearch.removeFirst();
//        }
//
//    }

	// this is mostly copied from dk.brics
// just adds the state map so we can track which states in the result correspond to which pairs of states in the inputs
// this is useful for setting the correct accept states
	public Tuple<HashMap<State, Tuple<State, State>>, Automaton> intersectWithMap(Automaton aut1, Automaton aut2) {
		Automaton result = new Automaton();
		HashMap<State, Tuple<State, State>> stateMap = new HashMap<>();
		HashMap<Tuple<State, State>, State> pairToResult = new HashMap<>();
		LinkedList<Tuple<State, State>> worklist = new LinkedList<>();

		Tuple<State, State> initialPair = new Tuple<>(aut1.getInitialState(), aut2.getInitialState());
		State resultInitial = new State();
		result.setInitialState(resultInitial);
		stateMap.put(resultInitial, initialPair);
		pairToResult.put(initialPair, resultInitial);
		worklist.add(initialPair);

		while (!worklist.isEmpty()) {
			Tuple<State, State> currentPair = worklist.removeFirst();
			State s1 = currentPair.get1();
			State s2 = currentPair.get2();
			State resultState = pairToResult.get(currentPair);

			// this is irrelevant for our case cause the intersections don't have the right accepts
			resultState.setAccept(s1.isAccept() && s2.isAccept());
			// but we keep it in so it can be used properly if necessary

			for (Transition t1 : s1.getTransitions()) {
				for (Transition t2 : s2.getTransitions()) {
					char min = (char) Math.max(t1.getMin(), t2.getMin());
					char max = (char) Math.min(t1.getMax(), t2.getMax());
					if (min <= max) {
						State dest1 = t1.getDest();
						State dest2 = t2.getDest();
						Tuple<State, State> destPair = new Tuple<>(dest1, dest2);

						State destResultState = pairToResult.get(destPair);
						if (destResultState == null) {
							destResultState = new State();
							pairToResult.put(destPair, destResultState);
							stateMap.put(destResultState, destPair);
							worklist.add(destPair);
						}
						resultState.addTransition(new Transition(min, max, destResultState));
					}
				}
			}
		}
		return new Tuple<>(stateMap, result);
	}

	/**
	 * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
	 * UNDER-ESTIMATION - None.
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_replace(char find, char replace) {
		// perform operation
		Automaton result = performUnaryOperation(automaton, new InverseReplaceCC(find, replace), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse inv_replace(String find, String replace) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Model_Acyclic_Inverse inv_replaceChar() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Model_Acyclic_Inverse inv_replaceFindKnown(char find) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Model_Acyclic_Inverse inv_replaceReplaceKnown(char replace) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * OVER-ESTIMATION - None.
	 * UNDER-ESTIMATION - None.
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_reverse() {

		Model_Acyclic_Inverse result = this.reverse();

		return result;
	}


	/**
	 * The string returned from this operation will be the original string with
	 * a new prefix and suffix of lengths start and maxStringPadding. The result should be
	 * intersected with the previous state to obtain strings with proper length.
	 * <p>
	 * This can be improved if we examine the previous state and pass this the
	 * length as well as the start and end. This would eliminate the possibility of
	 * under-estimation.
	 * <p>
	 * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
	 * UNDER-ESTIMATION - Yes, if original had a longer suffix than maxStringPadding
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_substring(int start, int end) {

		// we know the length of the prefix, it is equal to start.
		Model_Acyclic_Inverse prefix = modelManager.createAnyString(start, start);

		// we do not know the length of the original string, so we do no know the length of the suffix.
		// if the original had a longer suffix, we will lose string possibilities, under-estimation.
		Model_Acyclic_Inverse suffix = modelManager.createAnyString(0, maxStringPadding);

		Model_Acyclic_Inverse result = prefix.concatenate(this);
		result = result.concatenate(suffix);

		return result;
	}

	@Override
	public Model_Acyclic_Inverse inv_substring(int start) {

		// we know the length of the prefix, it is equal to start.
		Model_Acyclic_Inverse prefix = modelManager.createAnyString(start, start);

		// we do not know the length of the original string, so we do no know the length of the suffix.
		// if the original had a longer suffix, we will lose string possibilities, under-estimation.
		//Model_Acyclic_Inverse suffix = modelManager.createAnyString(0,maxStringPadding);

		Model_Acyclic_Inverse result = prefix.concatenate(this);
		//result = result.concatenate(suffix);

		return result;
	}


	@Override
	public Model_Acyclic_Inverse inv_setCharAt(int offset, Model_Acyclic_Inverse argModel) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Model_Acyclic_Inverse inv_setLength(int length) {

		Model_Acyclic_Inverse suffix = modelManager.createAnyString(0, maxStringPadding);
		Model_Acyclic_Inverse result = this.concatenate(suffix);

		return result;
	}


	@Override
	public Model_Acyclic_Inverse inv_suffix(int start) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
	 * UNDER-ESTIMATION - None.
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_toLowercase() {
		// perform operation
		Automaton result = performUnaryOperation(automaton, new InverseLowerCase(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	/**
	 * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
	 * UNDER-ESTIMATION - None.
	 *
	 * @author Marlin Roberts
	 * 05/24/2020
	 */
	@Override
	public Model_Acyclic_Inverse inv_toUppercase() {
		// perform operation
		Automaton result = performUnaryOperation(automaton, new InverseUpperCase(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
	}


	@Override
	public Model_Acyclic_Inverse inv_trim() {
//
//		Alphabet padAlphabet = new Alphabet(" ");
//		Model_Acyclic_Inverse_Manager manager = new Model_Acyclic_Inverse_Manager (padAlphabet, maxStringPadding);
//		Model_Acyclic_Inverse padModel = manager.createAnyString();
//		Model_Acyclic_Inverse resultModel = padModel.concatenate(this).concatenate(padModel);

		// above implementation had spaces loops
		Automaton afterTrim = this.getAutomatonObject();
		// pad trimmed object with variable length whitespaec to fill out bound length
		int minSize = afterTrim.getShortestExample(true).length();
		Automaton pad = Automaton.makeCharSet(" ").repeat(0, this.boundLength - minSize);
		Automaton result = pad.concatenate(afterTrim.concatenate(pad));

		//note that this technically allows paddign outside of boundLength (again shuold handle all the length stuff, and ideally just with Model_Acyclic_Inverse
		return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength); // fucking lengths
	}


	@Override
	public Automaton getAutomatonObject() {
		return this.automaton;
	}

	@Override
	public String getShortestExampleString() {
		return this.automaton.getShortestExample(true);
	}

	@Override
	public Model_Acyclic_Inverse getShortestModel() {
		State initial = this.automaton.getInitialState();
		State exampleStart = new State();
		exampleStart.setAccept(initial.isAccept());
		State currentExample = exampleStart;
		State current = initial;
		while (!current.isAccept()) {
			LogicalTransition trans = AutomatonHelper.getLogicalTransitions(current).get(0);
			// lt gives you the largest transition set first
			State next = trans.getDestination();
			State nextExample = new State();
			for (Transition t : trans.getTransitions()) {
				currentExample.addTransition(new Transition(t.getMin(), t.getMax(), nextExample));
			}
			nextExample.setAccept(next.isAccept());
			current = next;
			currentExample = nextExample;
		}
		Automaton exampleAut = new Automaton();
		exampleAut.setInitialState(exampleStart);
		return new Model_Acyclic_Inverse(exampleAut, this.alphabet, calculateBoundLength(exampleAut));
	}

	@Override
	public Model_Acyclic_Inverse getShortestExampleModel() {
		return modelManager.createString(this.automaton.getShortestExample(true));

	}

//    private void ensureAcyclicModel(Model_Acyclic_Inverse arg) {
// check if automaton model is bounded
//        if (!(arg instanceof Model_Acyclic_Inverse)) {
//
//            throw new UnsupportedOperationException(
//                    "The Acyclic Automaton Model only supports binary " +
//                    "operations with other Acyclic Automaton Models.");
//        }
//    }

	public Model_Acyclic_Inverse union(Model_Acyclic_Inverse argModel) {
		//ensureAcyclicModel(argModel);
		Automaton arg = getAutomatonFromAcyclicModel(argModel);
		Automaton result = this.automaton.union(arg);
		result.minimize();
		int boundLength = this.boundLength;
		if (argModel.boundLength > this.boundLength) {
			boundLength = argModel.boundLength;
		}
		return new Model_Acyclic_Inverse(result, this.alphabet, boundLength);
	}

	@Override
	public List<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>> inv_concatenate_sym_set(Model_Acyclic_Inverse base,
																							 Model_Acyclic_Inverse arg) {

		int suffixBound = arg.getBoundLength();            // symbolic, could be any length up to bound
		int resultLength = this.getBoundLength();        // should be a single string, length = bound
		int prefixBound = base.getBoundLength();        // symbolic, could be any length up to bound

		System.out.println("Bounds: S " + suffixBound + " P " + prefixBound + " RL " + resultLength);
//		System.out.format("Bounds: S %s P %s RL %s \n", suffixBound, prefixBound, resultLength);


		List<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>> results = new ArrayList<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>>();

		Model_Acyclic_Inverse prefixModel = null;
		Model_Acyclic_Inverse suffixModel = null;

		boolean noMatch = true;

		int prefixLength = 0;

		while (prefixLength <= prefixBound) {

			prefixModel = this.substring(0, prefixLength);

			suffixModel = this.substring(prefixLength, resultLength);

			System.out.println("SPLIT: P " + prefixModel.getShortestExampleString() + " S " + suffixModel.getShortestExampleString());
//			System.out.format("SPLIT: P %4s  S %6s ", prefixModel.getShortestExampleString(), suffixModel.getShortestExampleString());

			if (!base.intersect(prefixModel).isEmpty() && !arg.intersect(suffixModel).isEmpty()) {
				results.add(new Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>(prefixModel, suffixModel));
				noMatch = false;
				System.out.println("Accepted");
			} else {
				System.out.println(" Intersection of prfx or sffx empty - Rejected");
//				System.out.println(" base count: " + base.modelCount() + " arg count: " + arg.modelCount());
			}

			prefixLength++;

		}

		if (noMatch) {
			System.out.println("ERROR: Could not find a valid prefix / suffix ...");
		}

		return results;
	}

	@Override
	public List<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>> inv_concatenate_sym_all(Model_Acyclic_Inverse base,
																							 Model_Acyclic_Inverse arg) {
		List<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>> results = new ArrayList<Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>>();
		this.setBoundLength(calculateBoundLength(this.automaton));// safety as apparently sometimes dont properly propogate?
		if (base.isSingleton()) {
			String prefixStr = base.getAcceptedStringExample();
			int prefixLen = prefixStr.length();
			Model_Acyclic_Inverse prefixModel = this.substring(0, prefixLen);
//			if (!prefixModel.getFiniteStrings().contains(prefixStr)) return results;
			Model_Acyclic_Inverse suffixModel = this.substring(prefixLen, this.getBoundLength());
			suffixModel = arg.intersect(suffixModel);
			if (!suffixModel.isEmpty()) {
				prefixModel.automaton.minimize();
				suffixModel.automaton.minimize();
				results.add(new Tuple<>(prefixModel, suffixModel));
			}
			return results;
		}

		if (arg.isSingleton()) {
			String suffixStr = arg.getAcceptedStringExample();
			int suffixLen = suffixStr.length();
			int prefixLen = this.getBoundLength() - suffixLen;
			if (prefixLen < 0) return results;
			Model_Acyclic_Inverse prefixModel = this.substring(0, prefixLen);
			Model_Acyclic_Inverse suffixModel = this.substring(prefixLen, this.getBoundLength());
//			if (!suffixModel.getFiniteStrings().contains(suffixStr)) return results;
			prefixModel = base.intersect(prefixModel);
			if (!prefixModel.isEmpty()) {
				prefixModel.automaton.minimize();
				suffixModel.automaton.minimize();
				results.add(new Tuple<>(prefixModel, suffixModel));
			}
			return results;
		}
		Model_Acyclic_Inverse prefixModelInit = this.clone();
		//clear all final states in the prefix model
		Set<State> accepting = prefixModelInit.automaton.getAcceptStates();
		for (State s : accepting) {
			s.setAccept(false);
		}
		Model_Acyclic_Inverse suffixModelInit = this.clone();

		//System.out.println("prefixModelInit " + prefixModelInit.getAutomaton());
		//System.out.println("suffixModelInit " + suffixModelInit.getAutomaton());

		boolean noMatch = true;
		//iterate over each state of this automata
		int indx = 0;
		for (State s : suffixModelInit.getStatesOrdered()) {
			indx++;
			//find the same states in both models
			Model_Acyclic_Inverse prefixModel = prefixModelInit.clone();
			//System.out.println("prefixCurrent " + prefixModel.getAutomaton());

			//System.out.println("current state " + s);

			//in brics states are put into an ordered linkedlist set
			//the order is from the start state based on the transition id
			//so the same indx of the iteration would get the same state

			//make the state s(ps) the final states, i.e.,
			//where prefix would end
			int pindx = 1;
			for (State ps : prefixModel.getStatesOrdered()) {
				if (indx == pindx) {
					ps.setAccept(true);
					//System.out.println("prefix state " + ps);
					break;
				}
				pindx++;
			}

			//update and check if this split works with base
			prefixModel = base.intersect(prefixModel);
			if (prefixModel.isEmpty()) {
				printDebug("Going to the next split, prefix failed");
				//does not work, go to the next split
				continue;
			}

			Model_Acyclic_Inverse suffixModel = suffixModelInit.clone();
			//make the state s(ss) the start state, i.e.,
			//where the prefix ends this suffix should start
			int sindx = 1;
			for (State ss : suffixModel.getStatesOrdered()) {
				if (indx == sindx) {//need try compare also
					//System.out.println("sufix state " + ss);
					suffixModel.automaton.setInitialState(ss);
					break;
				}
				sindx++;
			}

			//update and check if this split worked for suffix
			suffixModel = arg.intersect(suffixModel);
			if (suffixModel.isEmpty()) {
				printDebug("Going to the next split, suffix failed");
				continue;
			}

			//eas since we just changing state attributes, there should be
			//no need for minimization? I think we need for the
			//second/suffix one where we change the start state since
			//some states might become unreachable
			//in the first/prefix one we just change what accepting states
			//are, which can also change how equivalent states can be collapsed.
			//So both of them needs to be minimized before adding the example set
			prefixModel.automaton.minimize();
			suffixModel.automaton.minimize();
			//if the split on state s is feasible for both base and arg then
			//add them into the list
			results.add(new Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse>(prefixModel, suffixModel));
			noMatch = false;
			//System.out.println(" Accepted on state s " + s);

		}

		if (noMatch) {
			printDebug("No match found, returning empty set of tuples");
		}

		return results;
	}


	/**
	 * Returns the set of states that are reachable from the initial state.
	 *
	 * @return set of {@link State} objects
	 */
	public Set<State> getStatesOrdered() {
		Set<State> visited = new LinkedHashSet<State>();

		LinkedList<State> worklist = new LinkedList<State>();
		State initial = automaton.getInitialState();
		worklist.add(initial);
		visited.add(initial);
		while (worklist.size() > 0) {
			State s = worklist.removeFirst();
			Collection<Transition> tr = s.getSortedTransitions(false);
			for (Transition t : tr)
				if (!visited.contains(t.getDest())) {
					visited.add(t.getDest());
					worklist.add(t.getDest());
				}
		}
		return visited;
	}


	/**
	 * Removes strings from argument mode from this model.
	 */
	@Override
	public void minus(Model_Acyclic_Inverse model) {

		Automaton remove = model.getAutomatonObject();
		automaton = automaton.minus(remove);

	}

	// helper method for replaceFirst to determine correctness
// enumerates all possible strings in model to perform operation
	public Model_Acyclic_Inverse replaceFirstBruteForce(Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace) {
		Model_Acyclic_Inverse result = new Model_Acyclic_Inverse(Automaton.makeEmpty(), this.alphabet, this.boundLength);
		// get all strings in model
		Set<String> thisStrings = this.automaton.getFiniteStrings();
		// get all strings in find model
		Set<String> findStrings = find.automaton.getFiniteStrings();
		// get all strings in replace model
		Set<String> replaceStrings = replace.automaton.getFiniteStrings();
		//construct result by calling replace on eaech string :D
		for (String s : thisStrings) {
			for (String f : findStrings) {
				for (String r : replaceStrings) {
					String newString = s.replaceFirst(f, r);
					result = result.union(new Model_Acyclic_Inverse(Automaton.makeString(newString), this.alphabet, newString.length()));
				}
			}
		}
		if (this.containsString("")) {
			// if the model contains the empty string, we need to add it to the result
			result = result.union(new Model_Acyclic_Inverse(Automaton.makeEmptyString(), this.alphabet, 0));
		}
		return result;
	}

	// helper method for replaceAll to determine correctness
// enumerates all possible strings in model to perform operation
	public Model_Acyclic_Inverse replaceAllBruteForce(Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace) {
		Model_Acyclic_Inverse result = new Model_Acyclic_Inverse(Automaton.makeEmpty(), this.alphabet, this.boundLength);
		// get all strings in model
		Set<String> thisStrings = this.automaton.getFiniteStrings();
		// get all strings in find model
		Set<String> findStrings = find.automaton.getFiniteStrings();
		// get all strings in replace model
		Set<String> replaceStrings = replace.automaton.getFiniteStrings();
		//construct result by calling replace on eaech string :D
		for (String s : thisStrings) {
			for (String f : findStrings) {
				for (String r : replaceStrings) {
					String newString = s.replaceAll(f, r);
					result = result.union(new Model_Acyclic_Inverse(Automaton.makeString(newString), this.alphabet, this.boundLength));
				}
			}
		}
		if (this.containsString("")) {
			// if the model contains the empty string, we need to add it to the result
			result = result.union(new Model_Acyclic_Inverse(Automaton.makeEmptyString(), this.alphabet, 0));
		}
		return result;
	}

	//singletons
	@Override
	public Model_Acyclic_Inverse replaceFirst(String find, String replace) {
		//both charsequence singleton/known
		Automaton base = this.automaton;
		base.determinize();
		System.err.println("replaceFirst(String, String) not implemetned");
		System.exit(1);
		Automaton result = performUnaryOperation(base, new Replace6(find, replace), this.alphabet);// not repalecfirst :)
		return new Model_Acyclic_Inverse(result, this.alphabet, calculateBoundLength(result));
	}
// Note that we do not use an operations class as we need visiblity of the solver instance


	/**
	 * Replaces the first occurrence of a substring matching the regex with the replacement string.
	 *
	 * @param regexString       the regex to match and replace
	 * @param replacementString the string to replace with
	 * @return a new Model_Acyclic_Inverse with the replaced string
	 */
//	@Override
	public Model_Acyclic_Inverse replaceFirst(Model_Acyclic_Inverse regexString, Model_Acyclic_Inverse replacementString) {
		// wrote this algorithm when arguments were a regexString and replacement string, now they are themselves models
		// potentially its extra work to convert them to automata :shrug:
//        Automaton regexAut = new RegExp(regexString).toAutomaton();
		Model_Acyclic_Inverse bruteModel = null;
		if (debug) {
			bruteModel = this.replaceFirstBruteForce(regexString, replacementString);
			printDebug("This: " + this.automaton.getFiniteStrings());
			printDebug("Find: " + regexString.automaton.getFiniteStrings());
			printDebug("Replace: " + replacementString.automaton.getFiniteStrings());
			printDebug("Brute: " + bruteModel.automaton.getFiniteStrings());
		}
//		if (regexString.isSingleton() && replacementString.isSingleton()) {
//			String find = regexString.getAcceptedStringExample();
//			String replace = replacementString.getAcceptedStringExample();
//			return this.replaceFirst(find, replace);
//		}

		Automaton regexAut = regexString.automaton;
		Automaton origAut = Automaton.minimize(automaton.clone());
		// nps 5.13.25 - Need to manually concatenate bounds as repeat uses loops
		int repeat = boundLength - regexString.getBoundLength();
		// to account for all possibilities add to front and back
		String charSet = this.alphabet.getCharSet();
		Automaton padding = Automaton.makeCharSet(charSet).repeat(0, repeat);
		Automaton anyPrefixAndSuffix = padding.concatenate(regexAut).concatenate(padding);

//        Automaton anyPrefixAndSuffix = Automaton.makeCharSet(this.alphabet.getCharSet()).repeat().concatenate(regexAut)
//                .concatenate(Automaton.makeCharSet(this.alphabet.getCharSet()).repeat());
		// Automaton containing all Strings in the originalAutomaton's language which
		// contain a substring which satisfies the regex
		Automaton intersection = Automaton.minimize(origAut.intersection(anyPrefixAndSuffix));
		// if there are no matches to operate on, return the originalAutomaton
		if (intersection.isEmpty()) {
			printDebug("No pattern to match, returning same model");
			return new Model_Acyclic_Inverse(automaton, this.alphabet, this.boundLength);
		}

		//separate out the unchanged by replaceFirst portion of the original automaton
		origAut = origAut.minus(anyPrefixAndSuffix);
		origAut.minimize();


		HashMap<State, Automaton> prefixMap = new HashMap<>();
		// suffix is the pattern match etc. so need to check suffixes later
		HashMap<State, Automaton> suffixMap = new HashMap<>();

		// PREFIX MATCHIING:
		// find all possible prefixes in the automaton that precede a pattern match
		if (debug) { // getFiniteStrings itself is expensive
			printDebug("prefix matching for intersection: " + intersection.getFiniteStrings());
		}
		while (!intersection.isEmpty()) {
			Stack<State> stack = new Stack<>();
			stack.push(intersection.getInitialState()); // dont need to clone because we don't ever manipulate the initial state?
			while (!stack.isEmpty()) {
				// looking the state that starts the prefix
				State start = stack.peek();
				// automaton for new start
				State oldStart = intersection.getInitialState();
				intersection.setInitialState(start);
				Automaton newStart = intersection.clone();
				// set new start
				intersection.setInitialState(oldStart); // set interseciton back to original start state

				// set all states to final to check for pattern existence at newStart
				Automaton newStartFinal = newStart.clone();
				for (State state : newStartFinal.getStates()) {
					state.setAccept(true);
				}
				// intersection with current state as the initial state mathcing a pattern
				Automaton temp = newStartFinal.intersection(regexAut.concatenate(padding));
				Automaton suffix = newStart.intersection(regexAut.concatenate(padding));
				// note this suffix represents a pattern and its suffix and we still need ot identify the actual suffixes
				//push children of start state, prefix not yet found
				if (!temp.isEmpty()) { // pattern found!
					Stack<State> stackCopy = (Stack<State>) stack.clone();
					Automaton prefix = alsoAutomatonFromStack(stackCopy);
					prefix.minimize();
					prefixMap.put(start, prefix);
					suffixMap.put(start, suffix);
					//remove the prefix suffix pair from our search
					Automaton found = prefix.concatenate(suffix);
					found.minimize();
					intersection = intersection.minus(found);
					intersection.minimize();
					break;
				} else { //no prefix found
					// add a child to explore another path
					// note: all paths shold have a pattern match at some point due to nature of intersection
//                    State next = start.getTransitions().iterator().next().getDest(); // need to mark visited cause can technically loop?
					stack.push(start.getTransitions().iterator().next().getDest());
					// this shuold also never be null otherwise intersection would be empty
				}
			}
		}
		if (debug) {
			printDebug("This: " + this.automaton.getFiniteStrings());
			printDebug("Without regex: " + origAut.getFiniteStrings());
			printDebug("Find: " + regexString.automaton.getFiniteStrings());
			printDebug("Replace: " + replacementString.automaton.getFiniteStrings());
			printDebug("Splits: ");
			for (State s : prefixMap.keySet()) {
				printDebug("    Prefix: " + prefixMap.get(s).getFiniteStrings());
				printDebug("    PatternSuffix: " + suffixMap.get(s).getFiniteStrings());
			}
		}

		// SUFFIX ENUMERATING:
		// now we have a map of prefixes and suffixes
		// but we need to enumerate possible suffix matches for each prefix
		Set<Automaton> results = new HashSet<>();
		for (State sufStart : suffixMap.keySet()) {
//            Automaton patternSuffix = suffixMap.get(sufStart).clone();
			Tuple<Automaton, HashMap<State, State>> tuple = cloneAndGetStateMap(suffixMap.get(sufStart));
			Automaton patternSuffix = tuple.get1();
			HashMap<State, State> ogToPsStateMap = tuple.get2();
			if (debug) {
				printDebug("---Searching for suffixes in " + patternSuffix.getFiniteStrings());
			}
			// find unique suffix patterns
			Automaton anySuffix = regexAut.concatenate(padding);
			while (!patternSuffix.isEmpty()) { // paths to find
				Stack<State> stack = new Stack<>();
				stack.push(patternSuffix.getInitialState()); // maybe need to clone as we manipulate the pivot.
				while (!stack.isEmpty()) {
					State pivot = stack.peek();
					boolean pivotAccept = pivot.isAccept();
					HashMap<State, Boolean> acceptMap = new HashMap<>();
					for (State s : patternSuffix.getStates()) {
						acceptMap.put(s, s.isAccept()); // needs to be saved to restore
						s.setAccept(false);
					}
					pivot.setAccept(true);

					if (debug) {
						printDebug("Find: " + regexAut.getFiniteStrings());
						printDebug("PatternSuffix with new pivot: " + patternSuffix.getFiniteStrings());
					}

					Automaton inter = patternSuffix.intersection(anySuffix);

					//restore patternSuffix?
					for (State s : patternSuffix.getStates()) {
						s.setAccept(acceptMap.get(s));
					}

					if (inter.isEmpty()) {
						printDebug("no match");
					}
					// we are looking for paths where the patternsuffix is not a match
					// this will be disjoint sets of patterns and a corresponding suffix
					if (!inter.isEmpty()) { //found a match
						// save this pattern suffix pair and remove from search
						//pivot.setAccept(pivotAccept);
						Stack<State> stackCopy = (Stack<State>) stack.clone();
						Automaton pattern = alsoAutomatonFromStack(stackCopy);
						Automaton prefix = prefixMap.get(sufStart);
						// get original patternsuffix and use pivot to construct suffix
						Automaton suffix = suffixMap.get(sufStart);
						// set the suffix initial state to the pivot
						for (State s : suffix.getStates()) {
							if (pivot.equals(ogToPsStateMap.get(s))) {
								suffix.setInitialState(s);
							}
						}

						suffix.minimize();
//                        suffixPrefixMap.put(suffix, prefix);
						// adding result to the set of results
						if (debug) {
							printDebug("Found a match, intersection: " + inter.getFiniteStrings());
							printDebug("    Prefix: " + prefix.getFiniteStrings());
							printDebug("    Pattern: " + pattern.getFiniteStrings());
							printDebug("    Suffix: " + suffix.getFiniteStrings());
						}
						Automaton result = prefix.concatenate(replacementString.automaton.concatenate(suffix));
						result.minimize();
						if (debug) {
							printDebug("    Result: " + result.getFiniteStrings());
						}
						results.add(result);
						// set minus of what was removed from patternSuffix to continue searching
						// have to get original patternsuffix so acceptin gstates are correct
						if (debug) {
							printDebug("Prefix: " + prefix + "Pattern: " + pattern + " Suffix: " + suffix + " -> " + pattern.concatenate(suffix));
							printDebug("    Removing found pattern-Suffix: " + pattern.concatenate(suffix).getFiniteStrings());
							printDebug("    From patternSuffix: " + patternSuffix.getFiniteStrings());
							printDebug("    Original pattern-suffix?:" + suffixMap.get(sufStart).getFiniteStrings());
						}

						patternSuffix = patternSuffix.minus(pattern.concatenate(suffix));
						patternSuffix.minimize();
						break;
					} else {
						// no match found, add child to stack
						pivot.setAccept(pivotAccept); // need ot also set back here
						stack.push(pivot.getTransitions().iterator().next().getDest()); // shuold also be non null etc. ?

					}
				}
			}


		}
		// now we shuold have a disjoint set of all automata that are possible prefix.replacement.suffix permutations.
		// where pattern was replaced in each

		Automaton res = Automaton.makeEmpty();
		for (Automaton r : results) {
			res = res.union(r);
		}
		res = res.union(origAut); // add back original without any patterns
		Model_Acyclic_Inverse result = new Model_Acyclic_Inverse(res, this.alphabet, this.boundLength);
		if (debug) {
			if (bruteModel.equals(result)) {
				printDebug("Brute force and result match");
			} else {
				System.err.println("Brute force and result do not match");
				System.err.println("Brute force: " + bruteModel.getFiniteStrings());
				System.err.println("    model: " + bruteModel.automaton);
				System.err.println("Result: " + result.getFiniteStrings());
				System.err.println("    model: " + result.automaton);
				System.exit(1);
			}
		}
		return result;
	}

	@Override
	public Model_Acyclic_Inverse inv_replaceFirst(Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace) {
		// union of replaceFirst with find/replace swtiched and original
		Model_Acyclic_Inverse result = this.replaceFirst(replace, find);
		return result.union(this);
	}

	@Override
	public Model_Acyclic_Inverse replaceAll(String find, String replace) {
		System.err.println("Shuold not be using this replaceAll");
		System.exit(1);
		return null;
	}

	@Override
	public Model_Acyclic_Inverse replaceAll(Model_Acyclic_Inverse regexString, Model_Acyclic_Inverse replacementString) {
		// assuming prefix seleciton is fairly straightforward calling replaceFirst iteratively shuoldnt be much less efficienct
		Model_Acyclic_Inverse bruteForce=null;
		if (debug) {
			bruteForce = this.replaceAllBruteForce(regexString, replacementString);
		}
		if (regexString.isSingleton() && replacementString.isSingleton()) {
			String find = regexString.getAcceptedStringExample();
			String replace = replacementString.getAcceptedStringExample();
			this.automaton.determinize();
			return this.replace(find, replace);
		}
		Model_Acyclic_Inverse result, next;
		next = this.replaceFirst(regexString, replacementString);
		do {
			result = next;
			next = result.replaceFirst(regexString, replacementString);

		} while (!result.equals(next));
		if (bruteForce!=null && bruteForce.equals(result)) {
			printDebug("Brute force and result match");
		} else if (bruteForce!=null){
			System.err.println("Brute force and result do not match");
			System.err.println("Brute force: " + bruteForce.getFiniteStrings());
			System.err.println("Result: " + result.getFiniteStrings());
			System.exit(1);
		}
		return result;
	}

	@Override
	public Model_Acyclic_Inverse inv_replaceAll(Model_Acyclic_Inverse find, Model_Acyclic_Inverse replace) {
		// union of replaceAll with find/replace swtiched and original
		Model_Acyclic_Inverse result = this.replaceAll(replace, find);
		return result.union(this);
//        return null;
	}


	// create automaton from stack that represents specific prefix path
	public static Automaton automatonFromStack(Stack<State> stack) {
		Automaton automaton = new Automaton();
		State myStart = new State();
		automaton.setInitialState(myStart);

		State current = myStart;
		while (!stack.isEmpty()) {
			State originalState = stack.remove(0);
			State nextState = stack.isEmpty() ? null : stack.get(0);

			for (Transition t : originalState.getTransitions()) {
				if (t.getDest().equals(nextState)) {
					State newState = new State();
					current.addTransition(new Transition(t.getMin(), t.getMax(), newState));
					current = newState;
					break;
				}
			}
		}

		// Set the last state in the path as the final state
		current.setAccept(true);
		automaton.minimize(); // jic
		return automaton;
	}

	public static Automaton alsoAutomatonFromStack(Stack<State> stack) {
		Automaton automaton = new Automaton();
		HashMap<State, State> stateMap = new HashMap<>();
		for (State s : stack) {
			stateMap.put(s, new State());
		}

		// Set the initial state
		State current = stateMap.get(stack.get(0));
		automaton.setInitialState(current);

		while (!stack.isEmpty()) {
			State originalState = stack.remove(0);
			current = stateMap.get(originalState);

			for (Transition t : originalState.getTransitions()) {
				if (stack.contains(t.getDest())) {
					State destState = stateMap.get(t.getDest());
					current.addTransition(new Transition(t.getMin(), t.getMax(), destState));
				}
			}

		}

		// Set the last state in the path as the final state
		current.setAccept(true);
		automaton.minimize(); // jic
		return automaton;
	}

//   /**
//    * Creates an acyclic automaton that matches strings containing the pattern,
//    * respecting the bound on string length.
//    *
//    * @param pattern The pattern to find
//    * @return An acyclic automaton matching strings containing the pattern
//    */
//   public Automaton makeAcyclicAnyPrefixAndSuffix(String pattern) {
//       Automaton patternAut = new RegExp(pattern).toAutomaton();
//       int patternLength = pattern.length();
//
//       // Result will be union of all valid prefix+pattern+suffix combinations
//       Automaton result = Automaton.makeEmpty();
//
//       // Generate all valid prefix+pattern+suffix combinations within the bound
//       for (int prefixLen = 0; prefixLen <= boundLength - patternLength; prefixLen++) {
//           for (int suffixLen = 0; suffixLen <= boundLength - patternLength - prefixLen; suffixLen++) {
//               // Create automaton for any string of length exactly prefixLen
//               Automaton prefix = makeExactLengthAnyString(prefixLen);
//
//               // Create automaton for any string of length exactly suffixLen
//               Automaton suffix = makeExactLengthAnyString(suffixLen);
//
//               // Combine: prefix + pattern + suffix
//               Automaton combined = prefix.concatenate(patternAut).concatenate(suffix);
//
//               // Add to result
//               result = result.union(combined);
//           }
//       }
//
//       return result;
//   }
//
//   /**
//    * Creates an acyclic automaton that matches any string of exactly the given length
//    * using the current alphabet.
//    */
//   private Automaton makeExactLengthAnyString(int length) {
//       if (length == 0) {
//           return BasicAutomata.makeEmptyString();
//       }
//
//       // Create automaton for any single character from the alphabet
//       Automaton anyChar = BasicAutomata.makeCharSet(alphabet.toString());
//
//       // For length 1, return the anyChar automaton
//       if (length == 1) {
//           return anyChar;
//       }
//
//       // Otherwise, create concatenation of anyChar automata
//       Automaton result = anyChar;
//       for (int i = 1; i < length; i++) {
//           result = result.concatenate(anyChar);
//       }
//
//       return result;
//   }
//
//    public int getStateNumber(State s) {
//        String[] parts = s.toString().split(" ");
//        return Integer.parseInt(parts[1]);
//    }

//    /**
//     * Creates a new automaton with a new start state and the same transitions as the original automaton.
//     *
//     * @param a The original automaton
//     * @param newStart The new start state
//     * @return A new automaton with the specified start state
//     */
//    public Automaton cloneWithNewStart(Automaton a, State newStart) {
//        Automaton ret = new Automaton();
//        // hashmap needed for transitions
//        HashMap<State, State> stateMap = new HashMap<>();
//        for (State s : a.getStates()) {
//            stateMap.put(s,new State());
//        }
//        for (State s : a.getStates()) {
//            State newState = stateMap.get(s);
//            newState.setAccept(s.isAccept());
//            if (s.equals(newState)) {
//                ret.setInitialState(newState);
//            }
//            for (Transition t : s.getTransitions()) {
//                State toState = stateMap.get(t.getDest());
//                newState.addTransition(new Transition(t.getMin(), t.getMax(), toState));
//            }
//        }
//        if (ret.getInitialState() == null) {
//            System.err.println("STATE NOT IN AUTOMATON");
//        }
//        return ret;
//    }

	/**
	 * Clones the automaton and returns a tuple containing the cloned automaton and a mapping of states.
	 * between the original and cloned automaton.
	 *
	 * @param a The original automaton
	 * @return A tuple containing the cloned automaton and a mapping of states
	 */
	public Tuple<Automaton, HashMap<State, State>> cloneAndGetStateMap(Automaton a) {
		Automaton ret = new Automaton();
		// hashmap needed for transitions
		HashMap<State, State> stateMap = new HashMap<>();
		for (State s : a.getStates()) {
			stateMap.put(s, new State());
		}
		for (State s : a.getStates()) {
			State newState = stateMap.get(s);
			newState.setAccept(s.isAccept());
			if (s.equals(a.getInitialState())) {
				ret.setInitialState(newState);
			}
			for (Transition t : s.getTransitions()) {
				State toState = stateMap.get(t.getDest());
				newState.addTransition(new Transition(t.getMin(), t.getMax(), toState));
			}
		}
		return new Tuple<Automaton, HashMap<State, State>>(ret, stateMap);
	}

	// finds largest possilbe string length in acyclic automaton
	public int calculateBoundLength(Automaton a) {
		a.minimize(); // in case there are dead end states
		int bound = -1;
		State init = a.getInitialState();
		Set<State> next = new HashSet<>();
		next.add(init);
		while (!next.isEmpty()) {
			Set<State> nextNext = new HashSet<>();
			for (State s : next) {
				for (Transition t : s.getTransitions()) {
					nextNext.add(t.getDest());
				}
			}
			next = nextNext;
			bound++;
		}
		return bound;
	}

	@Override
	public int getLowerBoundLength() {
		if (this.lowerBoundLength == -1) {
			this.lowerBoundLength = calculateMinBoundLength();
		}
		return this.lowerBoundLength;
	}

	public int calculateMinBoundLength() {
		State init = this.automaton.getInitialState();
		Set<State> curr = new HashSet<>();
		curr.add(init);
		int bound = 0;
		while (!curr.isEmpty()) {
			Set<State> next = new HashSet<>();
			for (State s : curr) {
				if (s.isAccept()) {
					return bound; // found an accepting state, set bound and return
				}
				for (Transition t : s.getTransitions()) {
					next.add(t.getDest());
				}
			}
			curr = next;
			bound++;
		}
		return bound;  /// shouldnt reach ehre though
	}
// could do a calculate both but in theory automaton arent ever too complex

	public void setAutomaton(Automaton a) {
		this.automaton = a;
	}

	public void minimize() {
		this.automaton.minimize();
	}


}
