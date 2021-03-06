/*
 * dk.brics.automaton
 * 
 * Copyright (c) 2001-2011 Anders Moeller
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.boisestate.cs.automaton;

import java.io.*;
import java.net.URL;
import java.util.Set;

/**
 * Finite-state automaton with fast run operation.
 *
 * @author Anders M&oslash;ller &lt;
 * <a href="mailto:amoeller@cs.au.dk">amoeller@cs.au.dk</a>&gt;
 */
public class RunAutomaton
        implements Serializable {

    static final long serialVersionUID = 20001;
    boolean[] accept;
    int[] classmap; // map from char number to class class
    int initial;
            // delta(state,c) = transitions[state*points.length +
			// getCharClass(c)]
    char[] points; // char interval start points
    int size;
    int[] transitions;

    /**
     * Returns array of character class interval start points. The array should
     * not be modified by the caller.
     */
    public char[] getCharIntervals() {
        return points.clone();
    }

    /**
     * Returns initial state.
     */
    public int getInitialState() {
        return initial;
    }

    /**
     * Returns number of states in automaton.
     */
    public int getSize() {
        return size;
    }

    @SuppressWarnings("unused")
    private RunAutomaton() {
    }

    /**
     * Constructs a new <code>RunAutomaton</code> from a deterministic
     * <code>Automaton</code>. Same as <code>RunAutomaton(a, true)</code>.
     *
     * @param a
     *         an automaton
     */
    public RunAutomaton(WeightedAutomaton a) {
        this(a, true);
    }

    /**
     * Constructs a new <code>RunAutomaton</code> from a deterministic
     * <code>Automaton</code>. If the given automaton is not deterministic,
     * it is determinized first.
     *
     * @param a
     *         an automaton
     * @param tableize
     *         if true, a transition table is created which makes the
     *         <code>run</code> method faster in return of a higher memory
     *         usage
     */
    public RunAutomaton(WeightedAutomaton a, boolean tableize) {
        a.determinize();
        points = a.getStartPoints();
        Set<WeightedState> states = a.getStates();
        WeightedAutomaton.setStateNumbers(states);
        initial = a.initial.getNumber();
        size = states.size();
        accept = new boolean[size];
        transitions = new int[size * points.length];
        for (int n = 0; n < size * points.length; n++) {
            transitions[n] = -1;
        }
        for (WeightedState s : states) {
            int n = s.getNumber();
            accept[n] = s.isAccept();
            for (int c = 0; c < points.length; c++) {
                WeightedState q = s.step(points[c]).getState();
                if (q != null) {
                    transitions[n * points.length + c] = q.getNumber();
                }
            }
        }
        if (tableize) {
            setAlphabet();
        }
    }

    /**
     * Sets alphabet table for optimal run performance.
     */
    final void setAlphabet() {
        classmap = new int[Character.MAX_VALUE - Character.MIN_VALUE + 1];
        int i = 0;
        for (int j = 0; j <= Character.MAX_VALUE - Character.MIN_VALUE; j++) {
            if (i + 1 < points.length && j == points[i + 1]) {
                i++;
            }
            classmap[j] = i;
        }
    }

    /**
     * Retrieves a serialized <code>RunAutomaton</code> located by a URL.
     *
     * @param url
     *         URL of serialized automaton
     *
     * @throws IOException
     *         if input/output related exception occurs
     * @throws OptionalDataException
     *         if the data is not a serialized object
     * @throws InvalidClassException
     *         if the class serial number does not match
     * @throws ClassCastException
     *         if the data is not a serialized <code>RunAutomaton</code>
     * @throws ClassNotFoundException
     *         if the class of the serialized object cannot be found
     */
    public static RunAutomaton load(URL url)
            throws IOException,
                   OptionalDataException,
                   ClassCastException,
                   ClassNotFoundException, InvalidClassException {
        return load(url.openStream());
    }

    /**
     * Retrieves a serialized <code>RunAutomaton</code> from a stream.
     *
     * @param stream
     *         input stream with serialized automaton
     *
     * @throws IOException
     *         if input/output related exception occurs
     * @throws OptionalDataException
     *         if the data is not a serialized object
     * @throws InvalidClassException
     *         if the class serial number does not match
     * @throws ClassCastException
     *         if the data is not a serialized <code>RunAutomaton</code>
     * @throws ClassNotFoundException
     *         if the class of the serialized object cannot be found
     */
    public static RunAutomaton load(InputStream stream)
            throws
            IOException,
            OptionalDataException,
            ClassCastException,
            ClassNotFoundException,
            InvalidClassException {
        ObjectInputStream s = new ObjectInputStream(stream);
        return (RunAutomaton) s.readObject();
    }

    /**
     * Returns a string representation of this automaton.
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("initial state: ").append(initial).append("\n");
        for (int i = 0; i < size; i++) {
            b.append("state " + i);
            if (accept[i]) {
                b.append(" [accept]:\n");
            } else {
                b.append(" [reject]:\n");
            }
            for (int j = 0; j < points.length; j++) {
                int k = transitions[i * points.length + j];
                if (k != -1) {
                    char min = points[j];
                    char max;
                    if (j + 1 < points.length) {
                        max = (char) (points[j + 1] - 1);
                    } else {
                        max = Character.MAX_VALUE;
                    }
                    b.append(" ");
                    WeightedTransition.appendCharString(min, b);
                    if (min != max) {
                        b.append("-");
                        WeightedTransition.appendCharString(max, b);
                    }
                    b.append(" -> ").append(k).append("\n");
                }
            }
        }
        return b.toString();
    }

    /**
     * Returns acceptance status for given state.
     */
    public boolean isAccept(int state) {
        return accept[state];
    }

    /**
     * Creates a new automaton matcher for the given input.
     *
     * @param s
     *         the CharSequence to search
     *
     * @return A new automaton matcher for the given input
     */
    public WeightedAutomatonMatcher newMatcher(CharSequence s) {
        return new WeightedAutomatonMatcher(s, this);
    }

    /**
     * Creates a new automaton matcher for the given input.
     *
     * @param s
     *         the CharSequence to search
     * @param startOffset
     *         the starting offset of the given character sequence
     * @param endOffset
     *         the ending offset of the given character sequence
     *
     * @return A new automaton matcher for the given input
     */
    public WeightedAutomatonMatcher newMatcher(CharSequence s, int startOffset, int
            endOffset) {
        return new WeightedAutomatonMatcher(s.subSequence(startOffset, endOffset),
                                            this);
    }

    /**
     * Returns true if the given string is accepted by this automaton.
     */
    public boolean run(String s) {
        int p = initial;
        int l = s.length();
        for (int i = 0; i < l; i++) {
            p = step(p, s.charAt(i));
            if (p == -1) {
                return false;
            }
        }
        return accept[p];
    }

    /**
     * Returns the state obtained by reading the given char from the given
     * state. Returns -1 if not obtaining any such state. (If the original
     * <code>Automaton</code> had no dead states, -1 is returned here if and
     * only if a dead state is entered in an equivalent automaton with a total
     * transition function.)
     */
    public int step(int state, char c) {
        if (classmap == null) {
            return transitions[state * points.length + getCharClass(c)];
        } else {
            return transitions[state * points.length +
                               classmap[c - Character.MIN_VALUE]];
        }
    }

    /**
     * Gets character class of given char.
     */
    int getCharClass(char c) {
        return SpecialWeightedOperations.findIndex(c, points);
    }

    /**
     * Returns the length of the longest accepted run of the given string
     * starting at the given offset.
     *
     * @param s
     *         the string
     * @param offset
     *         offset into <code>s</code> where the run starts
     *
     * @return length of the longest accepted run, -1 if no run is accepted
     */
    public int run(String s, int offset) {
        int p = initial;
        int l = s.length();
        int max = -1;
        for (int r = 0; offset <= l; offset++, r++) {
            if (accept[p]) {
                max = r;
            }
            if (offset == l) {
                break;
            }
            p = step(p, s.charAt(offset));
            if (p == -1) {
                break;
            }
        }
        return max;
    }

    /**
     * Writes this <code>RunAutomaton</code> to the given stream.
     *
     * @param stream
     *         output stream for serialized automaton
     *
     * @throws IOException
     *         if input/output related exception occurs
     */
    public void store(OutputStream stream)
            throws IOException {
        ObjectOutputStream s = new ObjectOutputStream(stream);
        s.writeObject(this);
        s.flush();
    }
}
