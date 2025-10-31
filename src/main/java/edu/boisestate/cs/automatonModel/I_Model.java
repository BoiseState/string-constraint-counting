package edu.boisestate.cs.automatonModel;

import java.math.BigInteger;
import java.util.Set;

//import dk.brics.automaton.Automaton;
//import dk.brics.string.stringoperations.UnaryOperation;
//import edu.boisestate.cs.Alphabet;

public interface I_Model<T extends I_Model<T>> {
	

	String getAcceptedStringExample();

	int getBoundLength();

	int getLowerBoundLength();

	Set<String> getFiniteStrings();

	Set<String> getFiniteStrings(int limit);

	boolean isEmpty();

	boolean isSingleton();

	void setBoundLength(int boundLength);

	T assertContainedInOther(T containingModel);

	T assertContainsOther(T containedModel);

	T assertEmpty();

	T assertEndsOther(T baseModel);

	T assertEndsWith(T endingModel);

	T assertEquals(T equalModel);

	T assertEqualsIgnoreCase(T equalModel);

	T assertHasLength(int min, int max);

	T assertNotContainedInOther(T notContainingModel);

	T assertNotContainsOther(T notContainedModel);

	T assertNotEmpty();

	T assertNotEndsOther(T notEndingModel);

	T assertNotEndsWith(T notEndingModel);

	T assertNotEquals(T notEqualModel);

	T assertNotEqualsIgnoreCase(T notEqualModel);

	T assertNotStartsOther(T notStartingModel);

	T assertNotStartsWith(T notStartsModel);

	T assertStartsOther(T startingModel);

	T assertStartsWith(T startingModel);

	T concatenate(T arg);

	boolean containsString(String actualValue);

	T delete(int start, int end);

	boolean equals(T arg);

	T intersect(T arg);

	T insert(int offset, T argModel);

	BigInteger modelCount();

	T replace(char find, char replace);

	T replace(String find, String replace);

	T replaceFirst(String find, String replace); // for all the non inverse stuff
	T replaceFirst(T find, T replace);

	T replaceChar();

	T replaceFindKnown(char find);

	T replaceReplaceKnown(char replace);

	T reverse();

	T substring(int start, int end);

	T setCharAt(int offset, T argModel);

	T setLength(int length);

	T suffix(int start);

	T toLowercase();

	T toUppercase();

	T trim();

	T charAt(int index);

	T clone();

	String getAutomaton();
	
	T replaceAll(String arg1String, String arg2String);

	T replaceAll(T find, T replace);

	T removeString(String aExample);

	T assertContains(T argModel, boolean result);
}