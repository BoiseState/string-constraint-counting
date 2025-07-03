package edu.boisestate.cs.automatonModel;

import dk.brics.automaton.*;
import dk.brics.string.stringoperations.*;
import edu.boisestate.cs.Alphabet;
import edu.boisestate.cs.automatonModel.operations.*;
import edu.boisestate.cs.util.Tuple;

import java.math.BigInteger;
import java.util.*;

/**
 * @author
 */
public class Model_Acyclic_Inverse extends A_Model_Inverse<Model_Acyclic_Inverse> {


    private Automaton automaton;
    private static int maxBoundLength = 32;// actaulyl unecessary? may be useful to have a static initBoundLength though?

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

    public Model_Acyclic_Inverse inv_charAt(int index) {
        // take incoming model and return new model that fills bound lengths from index with anyStrings
        // create two automaton, one from 0 to index, and one from index to bound length

        String alphabetCharSet = this.alphabet.getCharSet();
        Automaton start = Automaton.makeCharSet(alphabetCharSet).repeat(index, index);// looks at repeat docs
        Automaton end = Automaton.makeCharSet(alphabetCharSet).repeat(0, this.boundLength - index);
        // concatenate the two automata with the incoming model
        Automaton result = start.concatenate(this.getAutomatonObject()).concatenate(end);

        return new Model_Acyclic_Inverse(result, this.alphabet, this.boundLength);
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
     * @author Nat Steven
     * 6-23-25
     */
    @Override
    public Model_Acyclic_Inverse inv_insert(Model_Acyclic_Inverse baseModel, int offset, Model_Acyclic_Inverse insertModel) {
        //TODO: bound length stuff?
        this.minimize();

        // figure out prefix/suffix from backModel and index
        // note substring forces strings of the length specified
        // also it uses prefix and suffix so may not be precise enough, definitley not for the suffix
        Model_Acyclic_Inverse prefixIn = this.substring(0, offset);
        Model_Acyclic_Inverse suffixIn = this.anySuffix(offset);

        // do the same but for the forward prop source model
        Model_Acyclic_Inverse prefixSource = baseModel.substring(0, offset);
        Model_Acyclic_Inverse suffixSource = baseModel.anySuffix(offset);

        Model_Acyclic_Inverse prefix = prefixIn.intersect(prefixSource); // this should be the start of the return model

        // find common suffixes of Automata
        Automaton suffIn = suffixIn.getAutomatonObject();
        Automaton suffSource = suffixSource.getAutomatonObject();
        HashMap<State, Boolean> prevAccept = new HashMap<>();

        Automaton suffInRev = performUnaryOperation(suffIn, new Reverse(), this.alphabet);
        Automaton suffSourceRev = performUnaryOperation(suffSource, new Reverse(), this.alphabet);
        // note we remember the Reverse states
        for (State state : suffInRev.getStates()) {
            prevAccept.put(state, state.isAccept());
            state.setAccept(true);
        }
        for (State state : suffSourceRev.getStates()) {
            prevAccept.put(state, state.isAccept());
            state.setAccept(true);
        }
        Automaton intersection = suffInRev.intersection(suffSourceRev);
        Tuple<HashMap<State, Tuple<State, State>>, Automaton> mapAutTuple = intersectWithMap(suffInRev, suffSourceRev);
        Automaton suffixRev = mapAutTuple.get2();
        HashMap<State, Tuple<State, State>> stateMap = mapAutTuple.get1();
        if (!intersection.minus(suffixRev).isEmpty()){
            System.err.println("WARNING: intersection check failed");
            System.exit(1);
        }


        // now need to make sure our suffix has the correct accept states.
        for (State state : suffixRev.getStates()) {
            Tuple<State, State> inputs = stateMap.get(state);
            State s1 = inputs.get1();
            State s2 = inputs.get2();
            state.setAccept(prevAccept.get(s1) && prevAccept.get(s2));
            //epsilon transitions to accept? i.e. throuhg the remains
            if (state.getTransitions().isEmpty()) {
                state.setAccept(true);
            }
        }
        for (State state : suffInRev.getStates()) {
            state.setAccept(prevAccept.get(state));
        }

        suffixRev.minimize();
        suffInRev.minimize();
        //before reversing back suffix aut we want to construct the remainder aut.
        // we do this by removing (replacing with "") the suffix we found
        Model_Acyclic_Inverse suffixRevModel = new Model_Acyclic_Inverse(suffixRev, this.alphabet, calculateBoundLength(suffixRev));
        Model_Acyclic_Inverse suffixInRevModel = new Model_Acyclic_Inverse(suffInRev, this.alphabet, this.boundLength - offset);
        Model_Acyclic_Inverse remainSuffix = suffixInRevModel.replaceFirst(suffixRevModel, new Model_Acyclic_Inverse(BasicAutomata.makeEmptyString(), this.alphabet, 0));
        Automaton remains = performUnaryOperation(remainSuffix.getAutomatonObject(), new Reverse(), this.alphabet);
        // output back to base is then the prefix conc suffix model
        Model_Acyclic_Inverse result = new Model_Acyclic_Inverse(prefix.getAutomatonObject().concatenate(remains), this.alphabet, this.boundLength);


        Automaton insert = performUnaryOperation(suffixRev, new Reverse(), this.alphabet);
        insertModel.setAutomaton(insert);

        return result;
    }

    public Model_Acyclic_Inverse anySuffix(int start) {
        if (start < 0 || start > this.boundLength) {
            throw new IndexOutOfBoundsException("Start index " + start + " is out of bounds for model with bound length " + this.boundLength);
        }
        return new Model_Acyclic_Inverse(anySuffix(this.automaton, start), this.alphabet, this.boundLength - start);
    }

    // constructs automata starting at start, without changing any accept states.
    public Automaton anySuffix(Automaton a, int start) {
        if (a.isEmpty() || start <0) {
            // warn?
            return BasicAutomata.makeEmpty();
        }
        Automaton result = a.clone();
        HashSet<State> states = new HashSet<>();
        states.add(result.getInitialState());
        for (int i = 0; i < start; i++) {
            HashSet<State> nextStates = new HashSet<>();
            for (State state : states) {
                for (Transition transition : state.getTransitions()) {
                    nextStates.add(transition.getDest());
                }
            }
            states = nextStates;
        }
        // now we have all reachable states after start and will create new start the epsilons to those states
        State newStart = new State();
        result.setInitialState(newStart);
        Set<StatePair> epsilons = new HashSet<>();
        for (State state: states) {
            epsilons.add(new StatePair(newStart, state));
        }
        result.addEpsilons(epsilons);
        result.minimize();
        return result;
    }

    // this is mostly copied from dk.brics
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

            resultState.setAccept(s1.isAccept() && s2.isAccept());

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
                        if (debug){
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

    public static Automaton alsoAutomatonFromStack(Stack<State> stack){
        Automaton automaton = new Automaton();
        HashMap<State,State> stateMap = new HashMap<>();
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

    public void setAutomaton(Automaton a) {
        this.automaton = a;
    }

    public static void setMaxBoundLength(int length) {
        maxBoundLength = length;
    }

    public void minimize() {
        this.automaton.minimize();
    }

}
