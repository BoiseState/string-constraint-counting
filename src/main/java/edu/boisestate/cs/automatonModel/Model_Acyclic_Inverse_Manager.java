package edu.boisestate.cs.automatonModel;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicAutomata;
//import dk.brics.string.stringoperations.*;
import edu.boisestate.cs.Alphabet;
//import edu.boisestate.cs.automatonModel.operations.*;

//import java.math.BigInteger;
//import java.util.Set;

public class Model_Acyclic_Inverse_Manager {

    protected Alphabet alphabet;
    protected int initialBoundLength;

    private final int boundLength;

    public Alphabet getAlphabet() {
        return this.alphabet;
    }

    /**
     *
     * @param alphabet
     * @param boundLength
     */
   public Model_Acyclic_Inverse_Manager(Alphabet alphabet, int boundLength) {

       	this.alphabet = alphabet;
       	this.initialBoundLength = boundLength;
        this.alphabet = alphabet;
        this.boundLength = boundLength;

        // set automaton minimization as hopcroft
        Automaton.setMinimization(2);
    }

//    static void setInstance(Alphabet alphabet, int initialBoundLength) {
//        instance =
//                new Model_Acyclic_Manager(alphabet, initialBoundLength);
//    }

    /**
     * Create a new symbolic string from 0 to up to a certain length
     * @param initialBound the upper bound of the lenght (inlcusive)
     * @return
     */
    public Model_Acyclic_Inverse createAnyString(int initialBound) {
        return this.createAnyString(0, initialBound);
    }

    /**
     * Creates a symbolic string with length from min to max (both inclusive)
     * @param min
     * @param max
     * @return
     */
    public Model_Acyclic_Inverse createAnyString(int min, int max) {

        // create any string automaton from alphabet
        String charSet = this.alphabet.getCharSetString();
        Automaton anyChar = BasicAutomata.makeCharSet(charSet);

        // create bounded automaton
        Automaton acyclicAutomaton = anyChar.repeat(min, max);

        // return model from bounded automaton
        return new Model_Acyclic_Inverse(acyclicAutomaton, this.alphabet, this.boundLength);
    }

    /**
     * A string with no upper bound - for unbounded models
     * @return
     */
    public Model_Acyclic_Inverse createAnyString() {

        // create any string automaton from alphabet
        String charSet = this.alphabet.getCharSetString();
        Automaton anyString = BasicAutomata.makeCharSet(charSet).repeat();

        // return model from automaton
        return new Model_Acyclic_Inverse(anyString, this.alphabet);
    }

    /**
     * Create a new automaton model from a concrete string
     * @param string
     * @return
     */
    public Model_Acyclic_Inverse createString(String string) {
    	
    	// check for null string, set to empty. 
    	// in case intersection with previous is empty. not the best fix. 
    	// better to make sure that inverse operations only return valid strings!
    	if (string == null) {
    		string = "";
    	}
    	
        // create string automaton
        Automaton stringAutomaton = BasicAutomata.makeString(string);
        // get string length as bound length
        int length = string.length();
        // return model from automaton
        return new Model_Acyclic_Inverse(stringAutomaton, this.alphabet, length); // changed this for indexOf to use bound length of all models otherwise can cause issues
    }
}
