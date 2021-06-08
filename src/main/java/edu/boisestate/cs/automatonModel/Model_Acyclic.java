package edu.boisestate.cs.automatonModel;

import java.math.BigInteger;
import java.util.Set;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicAutomata;
import dk.brics.string.stringoperations.Postfix;
import dk.brics.string.stringoperations.Prefix;
import dk.brics.string.stringoperations.Replace1;
import dk.brics.string.stringoperations.Replace2;
import dk.brics.string.stringoperations.Replace3;
import dk.brics.string.stringoperations.Replace4;
import dk.brics.string.stringoperations.Replace6;
import dk.brics.string.stringoperations.Reverse;
import dk.brics.string.stringoperations.Substring;
import dk.brics.string.stringoperations.ToLowerCase;
import dk.brics.string.stringoperations.ToUpperCase;
import edu.boisestate.cs.Alphabet;
import edu.boisestate.cs.automatonModel.operations.IgnoreCase;
import edu.boisestate.cs.automatonModel.operations.PreciseDelete;
import edu.boisestate.cs.automatonModel.operations.PreciseInsert;
import edu.boisestate.cs.automatonModel.operations.PreciseSetCharAt;
import edu.boisestate.cs.automatonModel.operations.PreciseSetLength;
import edu.boisestate.cs.automatonModel.operations.PreciseSubstring;
import edu.boisestate.cs.automatonModel.operations.PreciseSuffix;
import edu.boisestate.cs.automatonModel.operations.PreciseTrim;
import edu.boisestate.cs.automatonModel.operations.StringModelCounter;

public class Model_Acyclic extends A_Model<Model_Acyclic> {

	private Automaton automaton;

	public String getAutomaton() {
		return automaton.toString();
	}

	Model_Acyclic(Automaton automaton, Alphabet alphabet, int boundLength) {
		super(alphabet, boundLength);

		this.automaton = automaton;
		this.modelManager = new Model_Acyclic_Manager(alphabet, boundLength);
	}

	Model_Acyclic(Automaton automaton, Alphabet alphabet) {
		super(alphabet, 0);

		this.automaton = automaton;
	}

	private static Automaton getAutomatonFromBoundedModel(Model_Acyclic model) {
		return model.automaton;
	}

	@Override
	public Model_Acyclic assertContainedInOther(Model_Acyclic containingModel) {
		ensureBoundedModel(containingModel);

		// get containing automaton
		Automaton containing = getAutomatonFromBoundedModel(containingModel);

		// if either automata is empty
		if (this.automaton.isEmpty() || containing.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get all substrings
		Automaton substrings = performUnaryOperation(containing, new Substring(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(substrings);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertContainsOther(Model_Acyclic containedModel) {
		ensureBoundedModel(containedModel);

		// create any string automata
		Automaton anyString1 = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();
		Automaton anyString2 = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

		// concatenate with contained automaton
		Automaton contained = getAutomatonFromBoundedModel(containedModel);
		Automaton x = anyString1.concatenate(contained).concatenate(anyString2);

		// get resulting automaton
		Automaton result = this.automaton.intersection(x);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertEmpty() {
		// get resulting automaton
		Automaton result = this.automaton.intersection(BasicAutomata.makeEmptyString());

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, 0);
	}

	@Override
	public Model_Acyclic assertEndsOther(Model_Acyclic containingModel) {
		ensureBoundedModel(containingModel);

		// get containing automaton
		Automaton containing = getAutomatonFromBoundedModel(containingModel);

		// if either automata is empty
		if (this.automaton.isEmpty() || containing.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get all suffixes
		Automaton suffixes = performUnaryOperation(containing, new Postfix(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(suffixes);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertEndsWith(Model_Acyclic endingModel) {
		ensureBoundedModel(endingModel);

		// create any string automata
		Automaton anyString = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

		// concatenate with ending automaton
		Automaton end = getAutomatonFromBoundedModel(endingModel);
		Automaton x = anyString.concatenate(end);

		// get bounded resulting automaton
		Automaton result = this.automaton.intersection(x);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertEquals(Model_Acyclic equalModel) {
		ensureBoundedModel(equalModel);

		// concatenate with contained automaton
		Automaton equal = getAutomatonFromBoundedModel(equalModel);

		// get resulting automaton
		Automaton result = this.automaton.intersection(equal);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertEqualsIgnoreCase(Model_Acyclic equalModel) {
		ensureBoundedModel(equalModel);

		// concatenate with contained automaton
		Automaton equal = getAutomatonFromBoundedModel(equalModel);
		Automaton equalIgnoreCase = performUnaryOperation(equal, new IgnoreCase(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(equalIgnoreCase);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertHasLength(int min, int max) {
		// check min and max
		if (min > max) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), this.alphabet, 0);
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
		return new Model_Acyclic(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic assertNotContainedInOther(Model_Acyclic notContainingModel) {
		ensureBoundedModel(notContainingModel);

		// get containing automaton
		Automaton notContaining = getAutomatonFromBoundedModel(notContainingModel);

		// if not containing automaton is empty
		if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// get automaton of required chars from not containing automaton
		notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

		Automaton result = automaton;
		if (!notContaining.isEmpty()) {

			// get all substrings
			Automaton substrings = performUnaryOperation(notContaining, new Substring(), this.alphabet);

			// get resulting automaton
			result = this.automaton.minus(substrings);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotContainsOther(Model_Acyclic notContainedModel) {
		ensureBoundedModel(notContainedModel);

		// get not contained automaton
		Automaton notContained = getAutomatonFromBoundedModel(notContainedModel);

		// if not containing automaton is empty
		if (notContained.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		notContained = getRequiredCharAutomaton(notContained, alphabet, boundLength);

		Automaton result = automaton;
		if (!notContained.isEmpty()) {
			// create any string automata
			Automaton anyString1 = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();
			Automaton anyString2 = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

			// concatenate with not contained automaton
			Automaton x = anyString1.concatenate(notContained).concatenate(anyString2);

			// get resulting automaton
			result = this.automaton.minus(x);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotEmpty() {
		// get resulting automaton
		Automaton result = this.automaton.minus(BasicAutomata.makeEmptyString());

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotEndsOther(Model_Acyclic notContainingModel) {
		ensureBoundedModel(notContainingModel);

		// get containing automaton
		Automaton notContaining = getAutomatonFromBoundedModel(notContainingModel);

		// if not containing automaton is empty
		if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// get automaton of required chars from not containing automaton
		notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

		Automaton result = automaton;
		if (!notContaining.isEmpty()) {

			// get all suffixes
			Automaton suffixes = performUnaryOperation(notContaining, new Postfix(), this.alphabet);

			// get resulting automaton
			result = this.automaton.minus(suffixes);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotEndsWith(Model_Acyclic notEndingModel) {
		ensureBoundedModel(notEndingModel);

		Automaton notEnding = getAutomatonFromBoundedModel(notEndingModel);

		// if not containing automaton is empty
		if (notEnding.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		notEnding = getRequiredCharAutomaton(notEnding, alphabet, boundLength);

		Automaton result = automaton;
		if (!notEnding.isEmpty()) {

			// create any string automata
			Automaton anyString = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

			// concatenate with not ending automaton
			Automaton x = anyString.concatenate(notEnding);

			// get resulting automaton
			result = this.automaton.minus(x);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotEquals(Model_Acyclic notEqualModel) {
		ensureBoundedModel(notEqualModel);

		// get not equal automaton
		Automaton notEqual = getAutomatonFromBoundedModel(notEqualModel);

		// if not containing automaton is empty
		if (notEqual.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// if not equal automaton is a singleton
		Automaton result = automaton;
		if (notEqual.getFiniteStrings(1) != null) {
			// get resulting automaton
			result = this.automaton.minus(notEqual);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotEqualsIgnoreCase(Model_Acyclic notEqualModel) {
		ensureBoundedModel(notEqualModel);

		// get not equal automaton
		Automaton notEqual = getAutomatonFromBoundedModel(notEqualModel);

		// if not containing automaton is empty
		if (notEqual.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// if not equal automaton is a singleton
		Automaton result = automaton;
		if (notEqual.getFiniteStrings(1) != null) {
			Automaton equalIgnoreCase = performUnaryOperation(notEqual, new IgnoreCase(), this.alphabet);

			// get resulting automaton
			result = this.automaton.minus(equalIgnoreCase);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotStartsOther(Model_Acyclic notContainingModel) {
		ensureBoundedModel(notContainingModel);

		// get containing automaton
		Automaton notContaining = getAutomatonFromBoundedModel(notContainingModel);

		// if not containing automaton is empty
		if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		// get automaton of required chars from not containing automaton
		notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

		Automaton result = automaton;
		if (!notContaining.isEmpty()) {

			// get all prefixes
			Automaton prefixes = performUnaryOperation(notContaining, new Prefix(), this.alphabet);

			// get resulting automaton
			result = this.automaton.minus(prefixes);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertNotStartsWith(Model_Acyclic notStartsModel) {
		ensureBoundedModel(notStartsModel);

		Automaton notStarting = getAutomatonFromBoundedModel(notStartsModel);

		// if not containing automaton is empty
		if (notStarting.isEmpty() || automaton.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), alphabet, 0);
		}

		notStarting = getRequiredCharAutomaton(notStarting, alphabet, boundLength);

		Automaton result = automaton;
		if (!notStarting.isEmpty()) {
			// create any string automata
			Automaton anyString = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

			// concatenate with not starts automaton
			Automaton x = notStarting.concatenate(anyString);

			// get resulting automaton
			result = this.automaton.minus(x);
		}

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertStartsOther(Model_Acyclic containingModel) {
		ensureBoundedModel(containingModel);

		// get containing automaton
		Automaton containing = getAutomatonFromBoundedModel(containingModel);

		// if either automata is empty
		if (this.automaton.isEmpty() || containing.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// get all prefixes
		Automaton prefixes = performUnaryOperation(containing, new Prefix(), this.alphabet);

		// get resulting automaton
		Automaton result = this.automaton.intersection(prefixes);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic assertStartsWith(Model_Acyclic startingModel) {
		ensureBoundedModel(startingModel);

		// create any string automata
		Automaton anyString = BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

		// concatenate with contained automaton
		Automaton start = getAutomatonFromBoundedModel(startingModel);
		Automaton x = start.concatenate(anyString);

		// get resulting automaton
		Automaton result = this.automaton.intersection(x);
		result.minimize();

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic clone() {
		// create new model from existing automata
		Automaton cloneAutomaton = this.automaton.clone();
		return new Model_Acyclic(cloneAutomaton, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic concatenate(Model_Acyclic argModel) {
		ensureBoundedModel(argModel);

		// get arg automaton
		Automaton arg = getAutomatonFromBoundedModel(argModel);

		// get concatenation of automata
		Automaton result = this.automaton.concatenate(arg);

		// minimize result automaton
		result.minimize();

		// calculate new bound length
		int boundLength = this.boundLength + argModel.boundLength;

		// return bounded model from automaton
		return new Model_Acyclic(result, this.alphabet, boundLength);
	}

	@Override
	public boolean containsString(String actualValue) {
		return this.automaton.run(actualValue);
	}

	@Override
	public Model_Acyclic delete(int start, int end) {

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
		return new Model_Acyclic(result, this.alphabet, newBoundLength);
	}

	@Override
	public boolean equals(Model_Acyclic argModel) {

		// check if arg model is bounded automaton model
		if (argModel instanceof Model_Acyclic) {

			// get arg automaton
			Automaton arg = getAutomatonFromBoundedModel(argModel);

			// check underlying automaton models for equality
			return this.automaton.equals(arg);
		}

		return false;
	}

	@Override
	public String getAcceptedStringExample() {
		return this.automaton.getShortestExample(true);
	}

	@Override
	public Set<String> getFiniteStrings() {

		// return finite strings from automaton
		return automaton.getFiniteStrings();
	}

	@Override
	public Model_Acyclic insert(int offset, Model_Acyclic argModel) {
		ensureBoundedModel(argModel);

		// get automata for operations
		Automaton arg = getAutomatonFromBoundedModel(argModel);

		// get resulting automaton
		PreciseInsert insert = new PreciseInsert(offset);
		Automaton result = insert.op(automaton, arg);
		result.minimize();

		// calculate new bound length
		int newBoundLength = this.boundLength + argModel.boundLength;

		// return unbounded model from automaton
		return new Model_Acyclic(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic intersect(Model_Acyclic argModel) {
		ensureBoundedModel(argModel);

		// get arg automaton
		Automaton arg = getAutomatonFromBoundedModel(argModel);

		// get intersection of automata
		Automaton result = this.automaton.intersection(arg);

		// minimize result automaton
		result.minimize();

		// calculate new bound length
		int boundLength = this.boundLength;
		if (argModel.boundLength < this.boundLength) {
			boundLength = argModel.boundLength;
		}

		// return bounded model from automaton
		return new Model_Acyclic(result, this.alphabet, boundLength);
	}

	@Override
	public boolean isEmpty() {
		/* eas 10-31-18 why, why is EmptyString() ??? */
		// return this.automaton.isEmptyString();
		// the correct code
		return this.automaton.isEmpty();

	}

	@Override
	public boolean isSingleton() {
		// get one finite string, null if more
		Set<String> strings = this.automaton.getFiniteStrings(1);

		// return if single non-null string in automaton
		return strings != null && strings.size() == 1 && strings.iterator().next() != null;
	}

	@Override
	public BigInteger modelCount() {
		// return model count of automaton
		return StringModelCounter.ModelCount(automaton);
	}

	@Override
	public Model_Acyclic replace(char find, char replace) {
		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace1(find, replace), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic replace(String find, String replace) {

		// perform operation
		Replace6 replaceOp = new Replace6(find, replace);
		Automaton result = performUnaryOperation(automaton, replaceOp, this.alphabet);

		// determine new bound length
		int boundDiff = find.length() - replace.length();
		int newBoundLength = this.boundLength - boundDiff;

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic replaceChar() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace4(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic replaceFindKnown(char find) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace2(find), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic replaceReplaceKnown(char replace) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Replace3(replace), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic reverse() {
		// if automaton is empty
		if (this.automaton.isEmpty()) {
			return new Model_Acyclic(BasicAutomata.makeEmpty(), this.alphabet, 0);
		}

		// perform operation
		Automaton result = performUnaryOperation(automaton, new Reverse(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic setCharAt(int offset, Model_Acyclic argModel) {
		ensureBoundedModel(argModel);

		// get automata for operations
		Automaton arg = getAutomatonFromBoundedModel(argModel);

		// get resulting automaton
		PreciseSetCharAt operation = new PreciseSetCharAt(offset);
		Automaton result = operation.op(automaton, arg);
		result.minimize();

		// return unbounded model from automaton
		return new Model_Acyclic(result, this.alphabet, boundLength);
	}

	@Override
	public Model_Acyclic setLength(int length) {

		// add null to new alphabet
		Set<Character> symbolSet = alphabet.getSymbolSet();
		symbolSet.add('\u0000');
		Alphabet newAlphabet = new Alphabet(symbolSet);

		// get resulting automaton
		Automaton result = performUnaryOperation(automaton, new PreciseSetLength(length), newAlphabet);

		// return unbounded model from automaton
		return new Model_Acyclic(result, this.alphabet, length);
	}

	@Override
	public Model_Acyclic substring(int start, int end) {
		// get resulting automaton
		Automaton result = performUnaryOperation(automaton, new PreciseSubstring(start, end), this.alphabet);

		// get new bound length
		int newBoundLength = end - start;

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic suffix(int start) {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new PreciseSuffix(start), this.alphabet);

		// determine new bound length
		int newBoundLength = this.boundLength - start;

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, newBoundLength);
	}

	@Override
	public Model_Acyclic toLowercase() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new ToLowerCase(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic toUppercase() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new ToUpperCase(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	@Override
	public Model_Acyclic trim() {

		// perform operation
		Automaton result = performUnaryOperation(automaton, new PreciseTrim(), this.alphabet);

		// return new model from resulting automaton
		return new Model_Acyclic(result, this.alphabet, this.boundLength);
	}

	private void ensureBoundedModel(Model_Acyclic arg) {
		// check if automaton model is bounded
		if (!(arg instanceof Model_Acyclic)) {

			throw new UnsupportedOperationException("The BoundedAutomatonModel only supports binary "
					+ "operations with other BoundedAutomatonModels.");
		}
	}
}