package edu.boisestate.cs.automatonModel;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicAutomata;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import dk.brics.string.stringoperations.*;
import edu.boisestate.cs.Alphabet;
import edu.boisestate.cs.automatonModel.operations.*;
import edu.boisestate.cs.util.Tuple;

import java.math.BigInteger;
import java.util.*;

/**
 * 
 * @author
 *
 */
public class Model_Acyclic_Inverse extends A_Model_Inverse <Model_Acyclic_Inverse> {
	
	
	private Automaton automaton;

   /**
    * Constructor 1: Requires *ACYCLIC* automata as argument. <br>
    * For use within Model_Acyclic_Manager <br>
    * Has no safeguards against incorrect automata being passed. <br>
    * 
    * @param automaton - ACYCLIC Automaton
    * @param alphabet - Alphabet
    * @param boundLength - Initial bound, should match Automaton length
    */
	protected Model_Acyclic_Inverse(Automaton automaton, Alphabet alphabet, int boundLength) {
       
    	super(alphabet, boundLength);

        this.automaton = automaton;
        
        this.modelManager = new Model_Acyclic_Inverse_Manager (alphabet, boundLength);
    }
	
	/**
	 * 
	 * @param automaton
	 * @param alphabet
	 */
	protected Model_Acyclic_Inverse(Automaton automaton, Alphabet alphabet) {
        
		super (alphabet, 0);

        this.automaton = automaton;
        
        this.modelManager = new Model_Acyclic_Inverse_Manager (alphabet, 0);
    }
    
   	public String getAutomaton(){
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
        Automaton result =  this.automaton.intersection(substrings);

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
    }

    @Override
    public Model_Acyclic_Inverse assertContainsOther(Model_Acyclic_Inverse containedModel) {
        //ensureAcyclicModel(containedModel);

        // create any string automata
        Automaton anyString1 =
                BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();
        Automaton anyString2 =
                BasicAutomata.makeCharSet(this.alphabet.getCharSet()).repeat();

        // concatenate with contained automaton
        Automaton contained = getAutomatonFromAcyclicModel(containedModel);
        Automaton x = anyString1.concatenate(contained).concatenate(anyString2);

        // get resulting automaton
        Automaton result =  this.automaton.intersection(x);
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
        Automaton result =  this.automaton.intersection(suffixes);

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
        Automaton result =  this.automaton.intersection(x);
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
        Automaton result =  this.automaton.intersection(equal);

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
    }

    @Override
    public Model_Acyclic_Inverse assertEqualsIgnoreCase(Model_Acyclic_Inverse equalModel) {
        //ensureAcyclicModel(equalModel);

        // concatenate with contained automaton
        Automaton equal = getAutomatonFromAcyclicModel(equalModel);
        Automaton equalIgnoreCase = performUnaryOperation(equal, new IgnoreCase(), this.alphabet);

        // get resulting automaton
        Automaton result =  this.automaton.intersection(equalIgnoreCase);

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
        Automaton result =  this.automaton.intersection(minMax);

        // get new bound length
        int newBoundLength = max;
        if (this.boundLength < max) {
            newBoundLength = this.boundLength;
        }

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, newBoundLength);
    }

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
        notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

        Automaton result = automaton;
        if (!notContaining.isEmpty()) {

            // get all substrings
            Automaton substrings = performUnaryOperation(notContaining,
                                                         new Substring(),
                                                         this.alphabet);

            // get resulting automaton
            result = this.automaton.minus(substrings);
        }

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
    }

    @Override
    public Model_Acyclic_Inverse assertNotContainsOther(Model_Acyclic_Inverse notContainedModel) {
        //ensureAcyclicModel(notContainedModel);

        // get not contained automaton
        Automaton notContained = getAutomatonFromAcyclicModel(notContainedModel);

        // if not containing automaton is  empty
        if (notContained.isEmpty() || automaton.isEmpty()) {
            return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
        }

//        if (notContainedModel.isSingleton() && notContainedModel.boundLength > this.boundLength) { // if contained is longer than this then impossible to be contained
//            return new Model_Acyclic_Inverse(this.automaton, this.alphabet, this.boundLength);
//        }


        // gets automaton of required chars if any
        notContained = getRequiredCharAutomaton(notContained, alphabet, boundLength);

        Automaton result = automaton;
        if (!notContained.isEmpty()) {
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
        }

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

        // if not containing automaton is  empty
        if (notContaining.isEmpty() || automaton.isEmpty() || automaton.isEmptyString()) {
            return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
        }

        // get automaton of required chars from not containing automaton
        notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

        Automaton result = automaton;
        if (!notContaining.isEmpty()) {

            // get all suffixes
            Automaton suffixes = performUnaryOperation(notContaining,
                                                       new Postfix(),
                                                       this.alphabet);

            // get resulting automaton
            result = this.automaton.minus(suffixes);
        }

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
    }


    @Override
    public Model_Acyclic_Inverse assertNotEndsWith(Model_Acyclic_Inverse notEndingModel) {
        //ensureAcyclicModel(notEndingModel);

        Automaton notEnding = getAutomatonFromAcyclicModel(notEndingModel);

        // if not containing automaton is  empty
        if (notEnding.isEmpty() || automaton.isEmpty()) {
            return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
        }

        notEnding = getRequiredCharAutomaton(notEnding, alphabet, boundLength);

        Automaton result = automaton;
        if (!notEnding.isEmpty()) {

            // create any string automata
            Automaton anyString =
                    BasicAutomata.makeCharSet(this.alphabet.getCharSet())
                                 .repeat();

            // concatenate with not ending automaton
            Automaton x = anyString.concatenate(notEnding);

            // get resulting automaton
            result = this.automaton.minus(x);
        }

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
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

        // if not equal automaton is a singleton
        Automaton result = automaton;
        if (notEqual.getFiniteStrings(1) != null) {
            // get resulting automaton
            result = this.automaton.minus(notEqual);
        }

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
        notContaining = getRequiredCharAutomaton(notContaining, alphabet, boundLength);

        Automaton result = automaton;
        if (!notContaining.isEmpty()) {

            // get all prefixes
            Automaton prefixes = performUnaryOperation(notContaining,
                                                       new Prefix(),
                                                       this.alphabet);

            // get resulting automaton
            result = this.automaton.minus(prefixes);
        }

        // return new model from resulting automaton
        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
    }


    @Override
    public Model_Acyclic_Inverse assertNotStartsWith(Model_Acyclic_Inverse notStartsModel) {
        //ensureAcyclicModel(notStartsModel);

        Automaton notStarting = getAutomatonFromAcyclicModel(notStartsModel);

        // if not containing automaton is  empty
        if (notStarting.isEmpty() || automaton.isEmpty()) {
            return new Model_Acyclic_Inverse(BasicAutomata.makeEmpty(), alphabet, 0);
        }

        notStarting = getRequiredCharAutomaton(notStarting, alphabet, boundLength);

        Automaton result = automaton;
        if (!notStarting.isEmpty()) {
            // create any string automata
            Automaton anyString =
                    BasicAutomata.makeCharSet(this.alphabet.getCharSet())
                                 .repeat();

            // concatenate with not starts automaton
            Automaton x = notStarting.concatenate(anyString);

            // get resulting automaton
            result = this.automaton.minus(x);
        }

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
        Automaton result =  this.automaton.intersection(prefixes);

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
        Automaton result =  this.automaton.intersection(x);
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
        return automaton.getFiniteStrings();
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
        //ensureAcyclicModel(argModel);

        // get automata for operations
        Automaton arg = getAutomatonFromAcyclicModel(argModel);

        // get resulting automaton
        PreciseInsert insert = new PreciseInsert(offset);
        Automaton result = insert.op(automaton, arg);
        result.minimize();

        // calculate new bound length
        int newBoundLength = this.boundLength + argModel.boundLength;

        // return unbounded model from automaton
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
    

    /**
     * 
     * Currently assumes a concrete argument.
     * 
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
		for (int i = argLength + 1; i <= bound ; i++) {
			argModel = this.modelManager.createAnyString(i);
			argModel = this.intersect(argModel);
			argModel = argModel.substring(0, i - argLength);
			resModel = resModel.union(argModel);
		}
		
		return resModel;
	}
	
	
    /**
     * 
     * will return a valid prefix of concated base.arg from this model.
     * 
     * OVER-ESTIMATION - .
     * UNDER-ESTIMATION - .
     * 
     * @author Marlin Roberts
     * 05/24/2020
     */
	@Override
	public  Model_Acyclic_Inverse inv_concatenate(Model_Acyclic_Inverse base, Model_Acyclic_Inverse arg) {
		
		//int suffixBound = arg.getBoundLength();			// symbolic, could be any length up to bound
		int resultLength = this.getBoundLength();		// should be a single string, length = bound
		int prefixBound = base.getBoundLength();		// symbolic, could be any length up to bound
		
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
     * 
     * will return a valid prefix of concated base.arg from this model.
     * 
     * OVER-ESTIMATION - .
     * UNDER-ESTIMATION - .
     * 
     * @author Marlin Roberts
     * 05/24/2020
     */
	@Override
	public  Tuple<Model_Acyclic_Inverse, Model_Acyclic_Inverse> inv_concatenate_sym (Model_Acyclic_Inverse base, Model_Acyclic_Inverse arg) {
		
		//int suffixBound = arg.getBoundLength();			// symbolic, could be any length up to bound
		int resultLength = this.getBoundLength();		// should be a single string, length = bound
		int prefixBound = base.getBoundLength();		// symbolic, could be any length up to bound

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
     * 
     * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
     * UNDER-ESTIMATION - None.
     * 
     * @author Marlin Roberts
     * 05/24/2020
     */
	@Override
	public Model_Acyclic_Inverse inv_delete(int start, int end) {
		
		Model_Acyclic_Inverse anyString = modelManager.createAnyString(end - start);
		Model_Acyclic_Inverse result = this.insert(start, anyString);
		
		return result;
	}



    /**
     * 
     * Currently assumes a concrete argument.
     * 
     * OVER-ESTIMATION - None with concrete argument
     * UNDER-ESTIMATION - None with concrete argument
     * 
     * @author Marlin Roberts
     * 05/24/2020
     */
	@Override
	public Model_Acyclic_Inverse inv_insert(int offset, Model_Acyclic_Inverse argModel) {
				
		Model_Acyclic_Inverse result = this.delete(offset, (offset + argModel.getBoundLength()));
				
		return result;
	}



    /**
     * 
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
     * 
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
     * 
     * The string returned from this operation will be the original string with
     * a new prefix and suffix of lengths start and maxStringPadding. The result should be 
     * intersected with the previous state to obtain strings with proper length.
     * 
     * This can be improved if we examine the previous state and pass this the 
     * length as well as the start and end. This would eliminate the possibility of
     * under-estimation.
     * 
     * OVER-ESTIMATION - Yes, results need to be intersected with previous state.
     * UNDER-ESTIMATION - Yes, if original had a longer suffix than maxStringPadding
     * 
     * @author Marlin Roberts
     * 05/24/2020
     */
	@Override
	public Model_Acyclic_Inverse inv_substring(int start, int end) {
		
		// we know the length of the prefix, it is equal to start.
		Model_Acyclic_Inverse prefix = modelManager.createAnyString(start,start);
		
		// we do not know the length of the original string, so we do no know the length of the suffix.
		// if the original had a longer suffix, we will lose string possibilities, under-estimation.
		Model_Acyclic_Inverse suffix = modelManager.createAnyString(0,maxStringPadding);
		
		Model_Acyclic_Inverse result = prefix.concatenate(this);
		result = result.concatenate(suffix);
		
		return result;
	}

	@Override
	public Model_Acyclic_Inverse inv_substring(int start) {
		
		// we know the length of the prefix, it is equal to start.
		Model_Acyclic_Inverse prefix = modelManager.createAnyString(start,start);
		
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
		
		Model_Acyclic_Inverse suffix = modelManager.createAnyString(0,maxStringPadding);
		Model_Acyclic_Inverse result = this.concatenate(suffix);
		
		return result;
	}




	@Override
	public Model_Acyclic_Inverse inv_suffix(int start) {
		// TODO Auto-generated method stub
		return null;
	}



    /**
     * 
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
     * 
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
		
		Alphabet padAlphabet = new Alphabet(" ");
		Model_Acyclic_Inverse_Manager manager = new Model_Acyclic_Inverse_Manager (padAlphabet, maxStringPadding);
		Model_Acyclic_Inverse padModel = manager.createAnyString();
		Model_Acyclic_Inverse resultModel = padModel.concatenate(this).concatenate(padModel);
		return resultModel;
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
    
    public Model_Acyclic_Inverse union (Model_Acyclic_Inverse argModel) {
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
		
		int suffixBound = arg.getBoundLength();			// symbolic, could be any length up to bound
		int resultLength = this.getBoundLength();		// should be a single string, length = bound
		int prefixBound = base.getBoundLength();		// symbolic, could be any length up to bound

        System.out.println("Bounds: S " + suffixBound + " P " + prefixBound + " RL " + resultLength);
//		System.out.format("Bounds: S %s P %s RL %s \n", suffixBound, prefixBound, resultLength);
		
		
		List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>> results = new ArrayList<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>();
		
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
		List<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>> results = new ArrayList<Tuple<Model_Acyclic_Inverse,Model_Acyclic_Inverse>>();
		
		Model_Acyclic_Inverse prefixModelInit = this.clone();
		//clear all final states in the prefix model
		Set<State> accepting = prefixModelInit.automaton.getAcceptStates();
		for(State s : accepting) {
			s.setAccept(false);
		}
		Model_Acyclic_Inverse suffixModelInit = this.clone();
		
		//System.out.println("prefixModelInit " + prefixModelInit.getAutomaton());
		//System.out.println("suffixModelInit " + suffixModelInit.getAutomaton());
						
		boolean noMatch = true;
		//iterate over each state of this automata
		int indx = 0;
		for(State s: suffixModelInit.getStatesOrdered()) {
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
			for(State ps : prefixModel.getStatesOrdered()) {
				if(indx == pindx) {
					ps.setAccept(true);
					//System.out.println("prefix state " + ps);
					break;
				}
				pindx++;
			}
			
			//update and check if this split works with base
			prefixModel = base.intersect(prefixModel);
			if (prefixModel.isEmpty()) {
                System.out.println("Going to the next split, prefix failed");
				//does not work, go to the next split
				continue;
			}
			
			Model_Acyclic_Inverse suffixModel = suffixModelInit.clone();
			//make the state s(ss) the start state, i.e.,
			//where the prefix ends this suffix should start
			int sindx=1;
			for(State ss : suffixModel.getStatesOrdered()) {
				if(indx == sindx) {//need try compare also
					//System.out.println("sufix state " + ss);
					suffixModel.automaton.setInitialState(ss);
					break;
				}
				sindx++;
			}
			
			//update and check if this split worked for suffix
			suffixModel = arg.intersect(suffixModel);
			if(suffixModel.isEmpty()) {
                System.out.println("Going to the next split, suffix failed");
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
		
		if(noMatch) {
            System.out.println("No match found, returning empty set of tuples");
		}
		
		return results;
	}
	
	
	/** 
	 * Returns the set of states that are reachable from the initial state.
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
        if (this.containsString("")){
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

    @Override
    public Model_Acyclic_Inverse replaceFirst(String find, String replace) {
        System.err.println("Shuoldnt be using `concrete` replaceFirst");
        System.exit(1);
        return null;
    }
    // Note that we do not use an operations class as we need visiblity of the solver instance
    //
    /**
     * Replaces the first occurrence of a substring matching the regex with the replacement string.
     *
     * @param regexString the regex to match and replace
     * @param replacementString the string to replace with
     * @return a new Model_Acyclic_Inverse with the replaced string
     */
//	@Override
	public Model_Acyclic_Inverse replaceFirst(Model_Acyclic_Inverse regexString, Model_Acyclic_Inverse replacementString) {
		// wrote this algorithm when arguments were a regexString and replacement string, now they are themselves models
        // potentially its extra work to convert them to automata :shrug:
//        Automaton regexAut = new RegExp(regexString).toAutomaton();
        Model_Acyclic_Inverse bruteModel = null;
        if(debug) {
            bruteModel = this.replaceFirstBruteForce(regexString, replacementString);
            printDebug("This: " + this.automaton.getFiniteStrings());
            printDebug("Find: " + regexString.automaton.getFiniteStrings());
            printDebug("Replace: " + replacementString.automaton.getFiniteStrings());
            printDebug("Brute: " + bruteModel.automaton.getFiniteStrings());
        }

        Automaton regexAut = regexString.automaton;
		Automaton origAut = Automaton.minimize(automaton.clone());
        Automaton anyPrefixAndSuffix = Automaton.makeCharSet(this.alphabet.getCharSet()).repeat().concatenate(regexAut)
                .concatenate(Automaton.makeCharSet(this.alphabet.getCharSet()).repeat()); // nps 5.13.25 - TODO: should this take into account the Bound?
		// Automaton containing all Strings in the originalAutomaton's language which
		// contain a substring which satisfies the regex
		Automaton intersection = Automaton.minimize(origAut.intersection(anyPrefixAndSuffix));
		// if there are no matches to operate on, return the originalAutomaton
		if (intersection.isEmpty()) {
            printDebug("No pattern to match, returning same model");
            return new Model_Acyclic_Inverse(automaton, this.alphabet, this.boundLength);
		}

		//separate out the unchanged by replaceFirst portion of the original automaton
		origAut= Automaton.minimize(origAut.minus(anyPrefixAndSuffix));


        HashMap<State, Automaton> prefixMap = new HashMap<>();
        // suffix is the pattern match etc. so need to check suffixes later
        HashMap<State, Automaton> suffixMap = new HashMap<>();

        // PREFIX MATCHIING:
        // find all possible prefixes in the automaton that precede a pattern match
        printDebug("prefix matching for intersection: " + intersection.getFiniteStrings());
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
                for (State state: newStartFinal.getStates()) {
                    state.setAccept(true);
                }
                // intersection with current state as the initial state mathcing a pattern
                Automaton temp = newStartFinal.intersection(regexAut.concatenate(Automaton.makeCharSet(this.alphabet.getCharSet()).repeat()));
                Automaton suffix = newStart.intersection(regexAut.concatenate(Automaton.makeCharSet(this.alphabet.getCharSet()).repeat()));
                // note this suffix represents a pattern and its suffix and we still need ot identify the actual suffixes
                //push children of start state, prefix not yet found
                if (!temp.isEmpty()) { // pattern found!
                    Stack<State> stackCopy = (Stack<State>) stack.clone();
                    Automaton prefix = automatonFromStack(stackCopy);
                    prefixMap.put(start, prefix);
                    suffixMap.put(start, suffix);
                    //remove the prefix suffix pair from our search
                    intersection = Automaton.minimize(intersection.minus(prefix.concatenate(suffix)));
                    break;
                } else { //no prefix found
                    // add a child to explore another path
                    // note: all paths shold have a pattern match at some point due to nature of intersection
                    stack.push(start.getTransitions().iterator().next().getDest());
                    // this shuold also never be null otherwise intersection would be empty
                }
            }
        }
        printDebug("This: " + this.automaton.getFiniteStrings());
        printDebug("Without regex: " + origAut.getFiniteStrings());
        printDebug("Find: " + regexString.automaton.getFiniteStrings());
        printDebug("Replace: " + replacementString.automaton.getFiniteStrings());
        printDebug("Splits: ");
        for (State s : prefixMap.keySet()) {
            printDebug("    Prefix: " + prefixMap.get(s).getFiniteStrings());
            printDebug("    PatternSuffix: " + suffixMap.get(s).getFiniteStrings());
        }

        // SUFFIX ENUMERATING:
        // now we have a map of prefixes and suffixes
        // but we need to enumerate possible suffix matches for each prefix
        Set<Automaton> results = new HashSet<>();
        for (State sufStart : suffixMap.keySet()) {
//            Automaton patternSuffix = suffixMap.get(sufStart).clone();
            Tuple<Automaton, HashMap<State,State>> tuple = cloneAndGetStateMap(suffixMap.get(sufStart));
            Automaton patternSuffix = tuple.get1();
            HashMap<State, State> ogToPsStateMap = tuple.get2();
            printDebug("---Searching for suffixes in " + patternSuffix.getFiniteStrings());
            // find unique suffix patterns
            Automaton anySuffix = regexAut.concatenate(Automaton.makeCharSet(this.alphabet.getCharSet()).repeat());
            while (!patternSuffix.intersection(anySuffix).isEmpty()) { // paths to find
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

                    printDebug("Find: " + regexAut.getFiniteStrings());
                    printDebug("PatternSuffix with new pivot: " + patternSuffix.getFiniteStrings());

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
                        Automaton pattern = automatonFromStack(stackCopy);
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
                        printDebug("Found a match, intersection: " + inter.getFiniteStrings());
                        printDebug("    Prefix: " + prefix.getFiniteStrings());
                        printDebug("    Pattern: " + pattern.getFiniteStrings());
                        printDebug("    Suffix: " + suffix.getFiniteStrings());
                        Automaton result = prefix.concatenate(replacementString.automaton.concatenate(suffix));
                        result.minimize();
                        printDebug("    Result: " + result.getFiniteStrings());
                        results.add(result);
                        // set minus of what was removed from patternSuffix to continue searching
                        // have to get original patternsuffix so acceptin gstates are correct
                        printDebug("Prefix: " + prefix + "Pattern: " + pattern + " Suffix: " + suffix + " -> " + pattern.concatenate(suffix));
                        printDebug("    Removing found pattern-Suffix: " + pattern.concatenate(suffix).getFiniteStrings());
                        printDebug("    From patternSuffix: " + patternSuffix.getFiniteStrings());
                        printDebug("    Original pattern-suffix?:" + suffixMap.get(sufStart).getFiniteStrings());

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
        if(debug){
        if (bruteModel.equals(result)) {
            printDebug("Brute force and result match");
        } else {
            System.err.println("Brute force and result do not match");
            System.err.println("Brute force: " + bruteModel.getFiniteStrings());
            System.err.println("    model: " + bruteModel.automaton);
            System.err.println("Result: " + result.getFiniteStrings());
            System.err.println("    model: " + result.automaton);
            System.exit(1);
        }}
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
        Model_Acyclic_Inverse bruteForce = this.replaceAllBruteForce(regexString, replacementString);
        Model_Acyclic_Inverse result, next;
        next = this.replaceFirst(regexString, replacementString);
        do {
            result = next;
            next = result.replaceFirst(regexString, replacementString);

        } while (!result.equals(next));
        if (bruteForce.equals(result)) {
            printDebug("Brute force and result match");
        } else {
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
    public static Automaton automatonFromStack(Stack<State> stack){
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
            stateMap.put(s,new State());
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

}
