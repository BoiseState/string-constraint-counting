package edu.boisestate.cs.automaton;

import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class AutomatonHelper {

    public static class LogicalTransition {
        private final State destination;
        private final List<Transition> transitions;
        private final int characterCount;

        public LogicalTransition(State destination, List<Transition> transitions) {
            this.destination = destination;
            this.transitions = transitions;
            this.characterCount = transitions.stream()
                    .mapToInt(t -> t.getMax() - t.getMin() + 1)
                    .sum();
        }

        public State getDestination() { return destination; }
        public List<Transition> getTransitions() { return transitions; }
        public int getCharacterCount() { return characterCount; }
    }

    public static List<LogicalTransition> getLogicalTransitions(State state) {
        // Group transitions by destination
        Map<State, List<Transition>> transitionsByDest = new HashMap<>();
        for (Transition t : state.getTransitions()) {
            transitionsByDest.computeIfAbsent(t.getDest(), k -> new ArrayList<>()).add(t);
        }

        // Convert to LogicalTransition objects and sort by character count
        return transitionsByDest.entrySet().stream()
                .map(entry -> new LogicalTransition(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(LogicalTransition::getCharacterCount).reversed())
                .collect(Collectors.toList());
    }
}