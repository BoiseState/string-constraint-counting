/**
 * An extended EJSA operation for a more precise prefix operation.
 */
package edu.boisestate.cs.model.operations;

import dk.brics.automaton.*;
import dk.brics.string.charset.CharSet;
import dk.brics.string.stringoperations.BinaryOperation;

import java.util.*;

@SuppressWarnings("Duplicates")
public class PreciseInsert
        extends BinaryOperation {
    private int offset;

    public PreciseInsert(int offset) {
        this.offset = offset;
    }

    @Override
    public CharSet charsetTransfer(CharSet arg0, CharSet arg1) {
        return arg0.union(arg1);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public Automaton op(Automaton baseAutomaton, Automaton argAutomaton) {
        //eas per documentation:
        //StringIndexOutOfBoundsException - if start is negative, greater
        // than length(), or greater than end.

        // if start is greater than end or automaton is empty
        if (this.offset < 0 || baseAutomaton.isEmpty()) {
            // return empty automaton (exception)
            return BasicAutomata.makeEmpty();
        }

        // clone base automaton
        Automaton clone1 = baseAutomaton.clone();

        //eas: even though start = end and per documentation the 
        //string will not be changes, the shorter strings that
        //the automaton represents will throw an exception, thus
        //we have to remove those string from this DFA.

        // create new initial state
        State initial = new State();

        // initialize state set
        Set<State> states = new HashSet<>();
        states.add(initial);

        // create return automaton from initial state
        Automaton returnAutomaton = new Automaton();
        returnAutomaton.setInitialState(initial);

        // initialize state map
        Map<State, State> stateMap = new HashMap<>();
        stateMap.put(initial, clone1.getInitialState());

        // create copy of automaton before start
        for (int i = 0; i < this.offset; i++) {
            // if automaton not long enough
            if (states.isEmpty()) {
                return BasicAutomata.makeEmpty();
            }

            // initialize next state set
            Set<State> nextStates = new HashSet<>();

            // get all transistions from each state
            for (State state : states) {
                // get original state from clone
                State originalState = stateMap.get(state);

                // add transitions to copied states
                for (Transition transition : originalState.getTransitions()) {
                    // create a copy of the destination state and add to map
                    // nps - 6/26/25: no need to create a whoel new state for every transition.... this leads to length^|sigma|
                    State origDest = transition.getDest();
                    State newDest = null;
                    if (stateMap.containsValue(origDest)){ // get the key of the already created new state
                        for (Map.Entry<State, State> entry : stateMap.entrySet()) {
                            if (entry.getValue().equals(origDest)) {
                                newDest = entry.getKey();
                                break;
                            }
                        }
                    } else {
                        newDest = new State();
                        stateMap.put(newDest, origDest);
                    }

                    // add destination state as next state
                    nextStates.add(newDest);

                    // create a transition from the previous state copy
                    state.addTransition(new Transition(transition.getMin(),
                                                       transition.getMax(),
                                                       newDest));
                }
            }

            // update states with new states
            states = nextStates;
        }

        // add epsilon transitions
        List<StatePair> epsilons = new ArrayList<>();
        for (State state : states) {
            Automaton argClone = argAutomaton.clone();
            epsilons.add(new StatePair(state, argClone.getInitialState()));
            for (State argAccept : argClone.getAcceptStates()) {
                argAccept.setAccept(false);
                epsilons.add(new StatePair(argAccept, stateMap.get(state)));
            }
        }

        // add epsilons to automaton
        returnAutomaton.addEpsilons(epsilons);

        // return the deleted automaton
        return returnAutomaton;
    }

    @Override
    public String toString() {
        return "PreciseInsert";
    }

}
