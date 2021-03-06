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
import java.util.*;

/**
 * Basic automata for representing common datatypes
 * related to Unicode, XML, and XML Schema.
 */
final public class Datatypes {
	
	private static final Map<String,WeightedAutomaton> automata;
	
	private static final WeightedAutomaton ws;
	
	private static final Set<String> unicodeblock_names;

	private static final Set<String> unicodecategory_names;

	private static final Set<String> xml_names;

	private static final String[] unicodeblock_names_array = {
		"BasicLatin",
		"Latin-1Supplement",
		"LatinExtended-A",
		"LatinExtended-B",
		"IPAExtensions",
		"SpacingModifierLetters",
		"CombiningDiacriticalMarks",
		"Greek",
		"Cyrillic",
		"Armenian",
		"Hebrew",
		"Arabic",
		"Syriac",
		"Thaana",
		"Devanagari",
		"Bengali",
		"Gurmukhi",
		"Gujarati",
		"Oriya",
		"Tamil",
		"Telugu",
		"Kannada",
		"Malayalam",
		"Sinhala",
		"Thai",
		"Lao",
		"Tibetan",
		"Myanmar",
		"Georgian",
		"HangulJamo",
		"Ethiopic",
		"Cherokee",
		"UnifiedCanadianAboriginalSyllabics",
		"Ogham",
		"Runic",
		"Khmer",
		"Mongolian",
		"LatinExtendedAdditional",
		"GreekExtended",
		"GeneralPunctuation",
		"SuperscriptsandSubscripts",
		"CurrencySymbols",
		"CombiningMarksforSymbols",
		"LetterlikeSymbols",
		"NumberForms",
		"Arrows",
		"MathematicalOperators",
		"MiscellaneousTechnical",
		"ControlPictures",
		"OpticalCharacterRecognition",
		"EnclosedAlphanumerics",
		"BoxDrawing",
		"BlockElements",
		"GeometricShapes",
		"MiscellaneousSymbols",
		"Dingbats",
		"BraillePatterns",
		"CJKRadicalsSupplement",
		"KangxiRadicals",
		"IdeographicDescriptionCharacters",
		"CJKSymbolsandPunctuation",
		"Hiragana",
		"Katakana",
		"Bopomofo",
		"HangulCompatibilityJamo",
		"Kanbun",
		"BopomofoExtended",
		"EnclosedCJKLettersandMonths",
		"CJKCompatibility",
		"CJKUnifiedIdeographsExtensionA",
		"CJKUnifiedIdeographs",
		"YiSyllables",
		"YiRadicals",
		"HangulSyllables",
		"CJKCompatibilityIdeographs",
		"AlphabeticPresentationForms",
		"ArabicPresentationForms-A",
		"CombiningHalfMarks",
		"CJKCompatibilityForms",
		"SmallFormVariants",
		"ArabicPresentationForms-B",
		"Specials",
		"HalfwidthandFullwidthForms",
		"Specials",
		"OldItalic",
		"Gothic",
		"Deseret",
		"ByzantineMusicalSymbols",
		"MusicalSymbols",
		"MathematicalAlphanumericSymbols",
		"CJKUnifiedIdeographsExtensionB",
		"CJKCompatibilityIdeographsSupplement",
		"Tags"
	};
	
	private static final String[] unicodecategory_names_array = {
		"Lu",
		"Ll",
		"Lt",
		"Lm",
		"Lo",
		"L",
		"Mn",
		"Mc",
		"Me",
		"M",
		"Nd",
		"Nl",
		"No",
		"N",
		"Pc",
		"Pd",
		"Ps",
		"Pe",
		"Pi",
		"Pf",
		"Po",
		"P",
		"Zs",
		"Zl",
		"Zp",
		"Z",
		"Sm",
		"Sc",
		"Sk",
		"So",
		"S",
		"Cc",
		"Cf",
		"Co",
		"Cn",
		"C"
	};
	
	private static final String[] xml_names_array = {
		"NCName",
		"QName",
		"Char",
		"NameChar",
		"URI",
		"anyname",
		"noap",
		"whitespace",
		"whitespacechar",
		"string",
		"boolean",
		"decimal",
		"float",
		"integer",
		"duration",
		"dateTime",
		"time",
		"date",
		"gYearMonth",
		"gYear",
		"gMonthDay",
		"gDay",
		"hexBinary",
		"base64Binary",
		"NCName2",
		"NCNames",
		"QName2",
		"Nmtoken2",
		"Nmtokens",
		"Name2",
		"Names",
		"language"
	};
	
	static {
		automata = new HashMap<String,WeightedAutomaton>();
		ws = WeightedAutomaton.minimize(WeightedAutomaton.makeCharSet(" \t\n\r").repeat());
		unicodeblock_names = new HashSet<String>(Arrays.asList(unicodeblock_names_array));
		unicodecategory_names = new HashSet<String>(Arrays.asList(unicodecategory_names_array));
		xml_names = new HashSet<String>(Arrays.asList(xml_names_array));
	}
	
	private Datatypes() {}
	
	/**
	 * Invoke during compilation to pre-build automata.
	 * Automata are stored in the directory specified by the system property <tt>dk.brics.automaton.datatypes</tt>.
	 * (Default: <tt>build</tt>, relative to the current working directory.)
	 */
	public static void main(String[] args) {
		long t = System.currentTimeMillis();
		boolean b = WeightedAutomaton.setAllowMutate(true);
		buildAll();
		WeightedAutomaton.setAllowMutate(b);
		System.out.println("Storing automata...");
		for (Map.Entry<String,WeightedAutomaton> e : automata.entrySet())
			store(e.getKey(), e.getValue());
		System.out.println("Time for building automata: " + (System.currentTimeMillis() - t) + "ms");
	}
	
	/**
	 * Returns pre-built automaton.
 	 * Automata are loaded as resources from the class loader of the <tt>Datatypes</tt> class.
 	 * (Typically, the pre-built automata are stored in the same jar as this class.)
 	 * <p>
	 * The following automata are available:
	 * <table border=1>
	 * <tr><th>Name</th><th>Description</th></tr>
	 * <tr><td><tt>NCName</tt></td><td><a target="_top" href="http://www.w3.org/TR/REC-xml-names/#NT-NCName">NCName</a> from XML Namespaces 1.0</td></tr>
	 * <tr><td><tt>QName</tt></td><td><a target="_top" href="http://www.w3.org/TR/REC-xml-names/#NT-QName">QName</a> from XML Namespaces 1.0</td></tr>
	 * <tr><td><tt>Char</tt></td><td><a target="_top" href="http://www.w3.org/TR/REC-xml/#NT-Char">Char</a> from XML 1.0</td></tr>
	 * <tr><td><tt>NameChar</tt></td><td><a target="_top" href="http://www.w3.org/TR/REC-xml/#NT-NameChar">NameChar</a> from XML 1.0</td></tr>
	 * <tr><td><tt>URI</tt></td><td><a target="_top" href="http://rfc.net/rfc2396.html#sA%2e">URI</a> from RFC2396 with
	 * amendments from <a target="_top" href="http://www.faqs.org/rfcs/rfc2373.html">RFC2373</td></tr>
	 * <tr><td><tt>anyname</tt></td><td>optional URI enclosed by brackets, followed by NCName</td></tr>
	 * <tr><td><tt>noap</tt></td><td>strings not containing '@' and '%'</td></tr>
	 * <tr><td><tt>whitespace</tt></td><td>optional <a target="_top" href="http://www.w3.org/TR/REC-xml/#NT-S">S</a> from XML 1.0</td></tr>
	 * <tr><td><tt>whitespacechar</tt></td><td>a single <a target="_top" href="http://www.w3.org/TR/REC-xml/#NT-S">whitespace character</a> from XML 1.0</td></tr>
	 * <tr><td><tt>string</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#string">string</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>boolean</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#boolean">boolean</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>decimal</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#decimal">decimal</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>float</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#float">float</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>integer</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#integer">integer</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>duration</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#duration">duration</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>dateTime</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#dateTime">dateTime</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>time</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#time">time</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>date</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#date">date</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>gYearMonth</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#gYearMonth">gYearMonth</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>gYear</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#gYear">gYear</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>gMonthDay</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#gMonthDay">gMonthDay</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>gDay</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#gDay">gDay</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>hexBinary</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#hexBinary">hexBinary</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>base64Binary</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#base64Binary">base64Binary</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>NCName2</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#NCName">NCName</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>NCNames</tt></td><td>list of <a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#NCName">NCName</a>s from XML Schema Part 2</td></tr>
	 * <tr><td><tt>QName2</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#QName">QName</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>Nmtoken2</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#NMTOKEN">NMTOKEN</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>Nmtokens</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#NMTOKENS">NMTOKENS</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>Name2</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#Name">Name</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>Names</tt></td><td>list of <a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#Name">Name</a>s from XML Schema Part 2</td></tr>
	 * <tr><td><tt>language</tt></td><td><a target="_top" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#language">language</a> from XML Schema Part 2</td></tr>
	 * <tr><td><tt>BasicLatin</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">BasicLatin</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Latin-1Supplement</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Latin-1Supplement</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>LatinExtended-A</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">LatinExtended-A</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>LatinExtended-B</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">LatinExtended-B</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>IPAExtensions</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">IPAExtensions</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>SpacingModifierLetters</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">SpacingModifierLetters</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CombiningDiacriticalMarks</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CombiningDiacriticalMarks</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Greek</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Greek</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Cyrillic</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Cyrillic</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Armenian</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Armenian</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Hebrew</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Hebrew</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Arabic</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Arabic</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Syriac</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Syriac</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Thaana</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Thaana</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Devanagari</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Devanagari</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Bengali</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Bengali</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Gurmukhi</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Gurmukhi</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Gujarati</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Gujarati</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Oriya</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Oriya</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Tamil</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Tamil</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Telugu</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Telugu</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Kannada</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Kannada</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Malayalam</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Malayalam</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Sinhala</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Sinhala</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Thai</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Thai</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Lao</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Lao</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Tibetan</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Tibetan</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Myanmar</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Myanmar</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Georgian</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Georgian</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>HangulJamo</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">HangulJamo</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Ethiopic</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Ethiopic</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Cherokee</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Cherokee</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>UnifiedCanadianAboriginalSyllabics</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">UnifiedCanadianAboriginalSyllabics</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Ogham</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Ogham</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Runic</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Runic</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Khmer</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Khmer</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Mongolian</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Mongolian</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>LatinExtendedAdditional</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">LatinExtendedAdditional</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>GreekExtended</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">GreekExtended</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>GeneralPunctuation</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">GeneralPunctuation</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>SuperscriptsandSubscripts</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">SuperscriptsandSubscripts</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CurrencySymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CurrencySymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CombiningMarksforSymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CombiningMarksforSymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>LetterlikeSymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">LetterlikeSymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>NumberForms</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">NumberForms</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Arrows</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Arrows</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>MathematicalOperators</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">MathematicalOperators</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>MiscellaneousTechnical</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">MiscellaneousTechnical</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>ControlPictures</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">ControlPictures</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>OpticalCharacterRecognition</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">OpticalCharacterRecognition</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>EnclosedAlphanumerics</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">EnclosedAlphanumerics</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>BoxDrawing</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">BoxDrawing</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>BlockElements</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">BlockElements</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>GeometricShapes</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">GeometricShapes</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>MiscellaneousSymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">MiscellaneousSymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Dingbats</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Dingbats</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>BraillePatterns</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">BraillePatterns</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKRadicalsSupplement</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKRadicalsSupplement</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>KangxiRadicals</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">KangxiRadicals</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>IdeographicDescriptionCharacters</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">IdeographicDescriptionCharacters</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKSymbolsandPunctuation</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKSymbolsandPunctuation</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Hiragana</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Hiragana</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Katakana</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Katakana</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Bopomofo</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Bopomofo</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>HangulCompatibilityJamo</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">HangulCompatibilityJamo</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Kanbun</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Kanbun</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>BopomofoExtended</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">BopomofoExtended</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>EnclosedCJKLettersandMonths</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">EnclosedCJKLettersandMonths</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKCompatibility</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKCompatibility</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKUnifiedIdeographsExtensionA</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKUnifiedIdeographsExtensionA</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKUnifiedIdeographs</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKUnifiedIdeographs</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>YiSyllables</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">YiSyllables</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>YiRadicals</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">YiRadicals</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>HangulSyllables</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">HangulSyllables</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKCompatibilityIdeographs</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKCompatibilityIdeographs</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>AlphabeticPresentationForms</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">AlphabeticPresentationForms</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>ArabicPresentationForms-A</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">ArabicPresentationForms-A</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CombiningHalfMarks</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CombiningHalfMarks</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKCompatibilityForms</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKCompatibilityForms</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>SmallFormVariants</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">SmallFormVariants</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>ArabicPresentationForms-B</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">ArabicPresentationForms-B</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Specials</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Specials</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>HalfwidthandFullwidthForms</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">HalfwidthandFullwidthForms</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Specials</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Specials</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>OldItalic</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">OldItalic</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Gothic</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Gothic</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Deseret</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Deseret</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>ByzantineMusicalSymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">ByzantineMusicalSymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>MusicalSymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">MusicalSymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>MathematicalAlphanumericSymbols</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">MathematicalAlphanumericSymbols</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKUnifiedIdeographsExtensionB</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKUnifiedIdeographsExtensionB</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>CJKCompatibilityIdeographsSupplement</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">CJKCompatibilityIdeographsSupplement</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Tags</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/Blocks-4.txt">Tags</a> block from Unicode 3.1</td></tr>
	 * <tr><td><tt>Lu</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Lu</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Ll</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Ll</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Lt</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Lt</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Lm</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Lm</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Lo</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Lo</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>L</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">L</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Mn</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Mn</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Mc</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Mc</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Me</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Me</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>M</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">M</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Nd</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Nd</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Nl</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Nl</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>No</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">No</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>N</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">N</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Pc</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Pc</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Pd</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Pd</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Ps</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Ps</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Pe</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Pe</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Pi</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Pi</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Pf</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Pf</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Po</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Po</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>P</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">P</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Zs</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Zs</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Zl</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Zl</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Zp</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Zp</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Z</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Z</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Sm</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Sm</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Sc</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Sc</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Sk</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Sk</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>So</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">So</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>S</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">S</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Cc</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Cc</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Cf</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Cf</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Co</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Co</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>Cn</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">Cn</a> category from Unicode 3.1</td></tr>
	 * <tr><td><tt>C</tt></td><td><a target="_top" href="http://www.unicode.org/Public/3.1-Update/UnicodeData-3.1.0.txt">C</a> category from Unicode 3.1</td></tr>
	 * </table>
	 * <p>Loaded automata are cached in memory.
	 * @param name name of automaton
	 * @return automaton
	 */
	public static WeightedAutomaton get(String name) {
		WeightedAutomaton a = automata.get(name);
		if (a == null) {
			a = load(name);
			automata.put(name, a);
		}
		return a;
	}
	
	/**
	 * Checks whether the given string is the name of a Unicode block (see {@link #get(String)}).
	 */
	public static boolean isUnicodeBlockName(String name) {
		return unicodeblock_names.contains(name);
	}
	
	/**
	 * Checks whether the given string is the name of a Unicode category (see {@link #get(String)}).
	 */
	public static boolean isUnicodeCategoryName(String name) {
		return unicodecategory_names.contains(name);
	}
	
	/**
	 * Checks whether the given string is the name of an XML / XML Schema automaton (see {@link #get(String)}).
	 */
	public static boolean isXMLName(String name) {
		return xml_names.contains(name);
	}
	
	/**
	 * Checks whether a given automaton is available.
	 * @param name automaton name
	 * @return true if the automaton is available
	 */
	public static boolean exists(String name) {
		try {
			Datatypes.class.getClassLoader().getResource(name + ".aut").openStream().close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private static WeightedAutomaton load(String name) {
		try {
			URL url = Datatypes.class.getClassLoader().getResource(name + ".aut");
			return WeightedAutomaton.load(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void store(String name, WeightedAutomaton a) {
		String dir = System.getProperty("dk.brics.automaton.datatypes");
		if (dir == null)
			dir = "build";
		try {
			a.store((new FileOutputStream(dir + "/" + name + ".aut")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void buildAll() {
		String[] xmlexps = {
				"Extender",  
				"[\u3031-\u3035\u309D-\u309E\u30FC-\u30FE\u00B7\u02D0\u02D1\u0387\u0640\u0E46\u0EC6\u3005]",
				"CombiningChar",
				"[\u0300-\u0345\u0360-\u0361\u0483-\u0486\u0591-\u05A1\u05A3-\u05B9\u05BB-\u05BD\u05C1-\u05C2\u064B-\u0652" +
				"\u06D6-\u06DC\u06DD-\u06DF\u06E0-\u06E4\u06E7-\u06E8\u06EA-\u06ED\u0901-\u0903\u093E-\u094C\u0951-\u0954" +
				"\u0962-\u0963\u0981-\u0983\u09C0-\u09C4\u09C7-\u09C8\u09CB-\u09CD\u09E2-\u09E3\u0A40-\u0A42\u0A47-\u0A48" +
				"\u0A4B-\u0A4D\u0A70-\u0A71\u0A81-\u0A83\u0ABE-\u0AC5\u0AC7-\u0AC9\u0ACB-\u0ACD\u0B01-\u0B03\u0B3E-\u0B43" +
				"\u0B47-\u0B48\u0B4B-\u0B4D\u0B56-\u0B57\u0B82-\u0B83\u0BBE-\u0BC2\u0BC6-\u0BC8\u0BCA-\u0BCD\u0C01-\u0C03" +
				"\u0C3E-\u0C44\u0C46-\u0C48\u0C4A-\u0C4D\u0C55-\u0C56\u0C82-\u0C83\u0CBE-\u0CC4\u0CC6-\u0CC8\u0CCA-\u0CCD" +
				"\u0CD5-\u0CD6\u0D02-\u0D03\u0D3E-\u0D43\u0D46-\u0D48\u0D4A-\u0D4D\u0E34-\u0E3A\u0E47-\u0E4E\u0EB4-\u0EB9" +
				"\u0EBB-\u0EBC\u0EC8-\u0ECD\u0F18-\u0F19\u0F71-\u0F84\u0F86-\u0F8B\u0F90-\u0F95\u0F99-\u0FAD\u0FB1-\u0FB7" +
				"\u20D0-\u20DC\u302A-\u302F\u05BF\u05C4\u0670\u093C\u094D\u09BC\u09BE\u09BF\u09D7\u0A02\u0A3C\u0A3E\u0A3F" +
				"\u0ABC\u0B3C\u0BD7\u0D57\u0E31\u0EB1\u0F35\u0F37\u0F39\u0F3E\u0F3F\u0F97\u0FB9\u20E1\u3099\u309A]",
				"Digit",
				"[\u0030-\u0039\u0660-\u0669\u06F0-\u06F9\u0966-\u096F\u09E6-\u09EF\u0A66-\u0A6F\u0AE6-\u0AEF\u0B66-\u0B6F" + 
				"\u0BE7-\u0BEF\u0C66-\u0C6F\u0CE6-\u0CEF\u0D66-\u0D6F\u0E50-\u0E59\u0ED0-\u0ED9\u0F20-\u0F29]",
				"Ideographic", 
				"[\u4E00-\u9FA5\u3021-\u3029\u3007]",
				"BaseChar",
				"[\u0041-\u005A\u0061-\u007A\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u0131\u0134-\u013E\u0141-\u0148" + 
				"\u014A-\u017E\u0180-\u01C3\u01CD-\u01F0\u01F4-\u01F5\u01FA-\u0217\u0250-\u02A8\u02BB-\u02C1\u0388-\u038A" +
				"\u038E-\u03A1\u03A3-\u03CE\u03D0-\u03D6\u03E2-\u03F3\u0401-\u040C\u040E-\u044F\u0451-\u045C\u045E-\u0481" +
				"\u0490-\u04C4\u04C7-\u04C8\u04CB-\u04CC\u04D0-\u04EB\u04EE-\u04F5\u04F8-\u04F9\u0531-\u0556\u0561-\u0586" +
				"\u05D0-\u05EA\u05F0-\u05F2\u0621-\u063A\u0641-\u064A\u0671-\u06B7\u06BA-\u06BE\u06C0-\u06CE\u06D0-\u06D3" +
				"\u06E5-\u06E6\u0905-\u0939\u0958-\u0961\u0985-\u098C\u098F-\u0990\u0993-\u09A8\u09AA-\u09B0\u09B6-\u09B9" +
				"\u09DC-\u09DD\u09DF-\u09E1\u09F0-\u09F1\u0A05-\u0A0A\u0A0F-\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32-\u0A33" +
				"\u0A35-\u0A36\u0A38-\u0A39\u0A59-\u0A5C\u0A72-\u0A74\u0A85-\u0A8B\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0" +
				"\u0AB2-\u0AB3\u0AB5-\u0AB9\u0B05-\u0B0C\u0B0F-\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32-\u0B33\u0B36-\u0B39" +
				"\u0B5C-\u0B5D\u0B5F-\u0B61\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99-\u0B9A\u0B9E-\u0B9F\u0BA3-\u0BA4" +
				"\u0BA8-\u0BAA\u0BAE-\u0BB5\u0BB7-\u0BB9\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C33\u0C35-\u0C39" +
				"\u0C60-\u0C61\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CE0-\u0CE1\u0D05-\u0D0C" +
				"\u0D0E-\u0D10\u0D12-\u0D28\u0D2A-\u0D39\u0D60-\u0D61\u0E01-\u0E2E\u0E32-\u0E33\u0E40-\u0E45\u0E81-\u0E82" +
				"\u0E87-\u0E88\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EAA-\u0EAB\u0EAD-\u0EAE\u0EB2-\u0EB3\u0EC0-\u0EC4" +
				"\u0F40-\u0F47\u0F49-\u0F69\u10A0-\u10C5\u10D0-\u10F6\u1102-\u1103\u1105-\u1107\u110B-\u110C\u110E-\u1112" +
				"\u1154-\u1155\u115F-\u1161\u116D-\u116E\u1172-\u1173\u11AE-\u11AF\u11B7-\u11B8\u11BC-\u11C2\u1E00-\u1E9B" +
				"\u1EA0-\u1EF9\u1F00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F5F-\u1F7D\u1F80-\u1FB4" +
				"\u1FB6-\u1FBC\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC" +
				"\u212A-\u212B\u2180-\u2182\u3041-\u3094\u30A1-\u30FA\u3105-\u312C\uAC00-\uD7A3" +
				"\u0386\u038C\u03DA\u03DC\u03DE\u03E0\u0559\u06D5\u093D\u09B2\u0A5E\u0A8D\u0ABD\u0AE0\u0B3D\u0B9C\u0CDE\u0E30\u0E84\u0E8A" +
				"\u0E8D\u0EA5\u0EA7\u0EB0\u0EBD\u1100\u1109\u113C\u113E\u1140\u114C\u114E\u1150\u1159\u1163\u1165\u1167\u1169\u1175\u119E" +
				"\u11A8\u11AB\u11BA\u11EB\u11F0\u11F9\u1F59\u1F5B\u1F5D\u1FBE\u2126\u212E]",
				"Letter", "<BaseChar>|<Ideographic>",
				"NCNameChar", "<Letter>|<Digit>|[-._]|<CombiningChar>|<Extender>",
				"NameChar", "<NCNameChar>|:",
				"Nmtoken", "<NameChar>+",
				"NCName", "(<Letter>|_)<NCNameChar>*",
				"Name", "(<Letter>|[_:])<NameChar>*",
				"QName", "(<NCName>:)?<NCName>",
				"Char", "[\t\n\r\u0020-\uD7FF\ue000-\ufffd]|[\uD800-\uDBFF][\uDC00-\uDFFF]",
				"whitespacechar", "[ \t\n\r]"
		};
		
		System.out.println("Building XML automata...");
		Map<String,WeightedAutomaton> t = buildMap(xmlexps);
		putFrom("NCName", t);
		putFrom("QName", t);
		putFrom("Char", t);
		putFrom("NameChar", t);
		putFrom("Letter", t);
		putFrom("whitespacechar", t);
		
		put(automata, "whitespace", ws);

		String[] uriexps = {
				"digit", "[0-9]",
				"upalpha", "[A-Z]",
				"lowalpha", "[a-z]",
				"alpha", "<lowalpha>|<upalpha>",
				"alphanum", "<alpha>|<digit>",
				"hex", "<digit>|[a-f]|[A-F]",
				"escaped", "%<hex><hex>",
				"mark", "[-_.!~*'()]",
				"unreserved", "<alphanum>|<mark>",
				// "reserved", "[;/?:@&=+$,]",
				"reserved", "[;/?:@&=+$,\\[\\]]",// RFC 2732
				"uric", "<reserved>|<unreserved>|<escaped>",
				"fragment", "<uric>*",
				"query", "<uric>*",
				"pchar", "<unreserved>|<escaped>|[:@&=+$,]",
				"param", "<pchar>*",
				"segment", "<pchar>*(;<param>)*",
				"path_segments", "<segment>(/<segment>)*",
				"abs_path", "/<path_segments>",
				"uric_no_slash", "<unreserved>|<escaped>|[;?:@&=+$,]",
				"opaque_part", "<uric_no_slash><uric>*",
				//"path", "(<abs_path>|<opaque_part>)?",  // not used
				"port", "<digit>*",
				// "IPv4address", "(<digit>{1,}\\.){3}<digit>{1,}",
				"IPv4address", "(<digit>{1,3}\\.){3}<digit>{1,3}", // RFC 2732 / 2373
				"hexseq", "<hex>{1,4}(:<hex>{1,4})*", // RFC 2373
				"hexpart", "<hexseq>|<hexseq>::<hexseq>?|::<hexseq>", // RFC 2373
				"IPv6address", "<hexpart>(:<IPv4address>)?", // RFC 2373
				"toplabel", "<alpha>|(<alpha>(<alphanum>|-)*<alphanum>)",
				"domainlabel", "<alphanum>|(<alphanum>(<alphanum>|-)*<alphanum>)",
				"hostname", "(<domainlabel>\\.)*<toplabel>\\.?",
				// "host", "<hostname>|<IPv4address>", 
				"host", "<hostname>|<IPv4address>|\\[<IPv6address>\\]", // RFC 2732
				"hostport", "<host>(:<port>)?",
				"userinfo", "(<unreserved>|<escaped>|[;:&=+$,])*",
				"server", "((<userinfo>\\@)?<hostport>)?",
				"reg_name", "(<unreserved>|<escaped>|[$,;:@&=+])+",
				"authority", "<server>|<reg_name>",
				"scheme", "<alpha>(<alpha>|<digit>|[-+.])*",
				"rel_segment", "(<unreserved>|<escaped>|[;@&=+$,])+",
				"rel_path", "<rel_segment><abs_path>?",
				"net_path", "//<authority><abs_path>?",
				"hier_part", "(<net_path>|<abs_path>)(\\?<query>)?",
				"relativeURI", "(<net_path>|<abs_path>|<rel_path>)(\\?<query>)?",
				"absoluteURI", "<scheme>:(<hier_part>|<opaque_part>)",
				"URI", "(<absoluteURI>|<relativeURI>)?(\\#<fragment>)?"
		};
		System.out.println("Building URI automaton...");
		putFrom("URI", buildMap(uriexps));
		put(automata, "anyname", WeightedAutomaton.minimize(WeightedAutomaton.makeChar('{').concatenate(automata.get("URI").clone()).concatenate(


                WeightedAutomaton.makeChar('}')).optional().concatenate(automata.get("NCName").clone())));

		put(automata, "noap", new RegExp("~(@[@%]@)").toAutomaton());
		
		String[] xsdmisc = {
				"_", "[ \t\n\r]*",
				"d", "[0-9]",
				"Z", "[-+](<00-13>:<00-59>|14:00)|Z",
				"Y", "(<d>{4,})&~(0000)",
				"M", "<01-12>",
				"D", "<01-31>",
				"T", "<00-23>:<00-59>:<00-59>|24:00:00",
				"B64", "[A-Za-z0-9+/]",
				"B16", "[AEIMQUYcgkosw048]",
				"B04", "[AQgw]",
				"B04S", "<B04> ?",
				"B16S", "<B16> ?",
				"B64S", "<B64> ?",
		};
		String[] xsdexps = {
				"boolean", "<_>(true|false|1|0)<_>",
				"decimal", "<_>([-+]?<d>+(\\.<d>+)?)<_>",
				"float", "<_>([-+]?<d>+(\\.<d>+)?([Ee][-+]?<d>+)?|INF|-INF|NaN)<_>",
				"integer", "<_>[-+]?[0-9]+<_>",
				"duration", "<_>(-?P(((<d>+Y)?(<d>+M)?(<d>+D)?(T(((<d>+H)?(<d>+M)?(<d>+(\\.<d>+)?S)?)&~()))?)&~()))<_>",
				"dateTime", "<_>(-?<Y>-<M>-<D>T<T>(\\.<d>+)?<Z>?)<_>",
				"time", "<_>(<T>(\\.<d>+)?<Z>?)<_>",
				"date", "<_>(-?<Y>-<M>-<D><Z>?)<_>",
				"gYearMonth", "<_>(-?<Y>-<M><Z>?)<_>",
				"gYear", "<_>(-?<Y><Z>?)<_>",
				"gMonthDay", "<_>(--<M>-<D><Z>?)<_>",
				"gDay", "<_>(--<D><Z>?)<_>",
				"gMonth", "<_>(--<M><Z>?)<_>",
				"hexBinary", "<_>([0-9a-fA-F]{2}*)<_>",
				"base64Binary",	"<_>(((<B64S><B64S><B64S><B64S>)*((<B64S><B64S><B64S><B64>)|(<B64S><B64S><B16S>=)|(<B64S><B04S>= ?=)))?)<_>",
				"language", "<_>[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*<_>",
				"nonPositiveInteger", "<_>(0+|-<d>+)<_>",
				"negativeInteger", "<_>(-[1-9]<d>*)<_>",
				"nonNegativeInteger", "<_>(<d>+)<_>",
				"positiveInteger", "<_>([1-9]<d>*)<_>",
		};
		System.out.println("Building XML Schema automata...");
		Map<String,WeightedAutomaton> m = buildMap(xsdmisc);
		putWith(xsdexps, m);
		
		put(m, "UNSIGNEDLONG", WeightedAutomaton.makeMaxInteger("18446744073709551615"));
		put(m, "UNSIGNEDINT", WeightedAutomaton.makeMaxInteger("4294967295"));
		put(m, "UNSIGNEDSHORT", WeightedAutomaton.makeMaxInteger("65535"));
		put(m, "UNSIGNEDBYTE", WeightedAutomaton.makeMaxInteger("255"));
		put(m, "LONG", WeightedAutomaton.makeMaxInteger("9223372036854775807" +
                                                        ""));
		put(m, "LONG_NEG", WeightedAutomaton.makeMaxInteger("9223372036854775808"));
		put(m, "INT", WeightedAutomaton.makeMaxInteger("2147483647"));
		put(m, "INT_NEG", WeightedAutomaton.makeMaxInteger("2147483648"));
		put(m, "SHORT", WeightedAutomaton.makeMaxInteger("32767"));
		put(m, "SHORT_NEG", WeightedAutomaton.makeMaxInteger("32768"));
		put(m, "BYTE", WeightedAutomaton.makeMaxInteger("127"));
		put(m, "BYTE_NEG", WeightedAutomaton.makeMaxInteger("128"));
		
		Map<String,WeightedAutomaton> u = new HashMap<String,WeightedAutomaton>();
		u.putAll(t);
		u.putAll(m);
		String[] xsdexps2 = {
				"Nmtoken2", "<_><Nmtoken><_>",
				"Name2", "<_><Name><_>",
				"NCName2", "<_><NCName><_>",
				"QName2", "<_><QName><_>",
				"Nmtokens", "<_>(<Nmtoken><_>)+",
				"NCNames", "<_>(<NCName><_>)+",
				"Names", "<_>(<Name><_>)+",
				"unsignedLong", "<_><UNSIGNEDLONG><_>",
				"unsignedInt", "<_><UNSIGNEDINT><_>",
				"unsignedShort", "<_><UNSIGNEDSHORT><_>",
				"unsignedByte", "<_><UNSIGNEDBYTE><_>",
				"long", "<_>(<LONG>|-<LONG_NEG>)<_>",
				"int", "<_>(<INT>|-<INT_NEG>)<_>",
				"short", "<_>(<SHORT>|-<SHORT_NEG>)<_>",
				"byte", "<_>(<BYTE>|-<BYTE_NEG>)<_>",
				"string", "<Char>*"
		};
		putWith(xsdexps2, u);

		System.out.println("Building Unicode block automata...");
		put(automata, "BasicLatin", WeightedAutomaton.makeCharRange('\u0000', '\u007F'));
		put(automata, "Latin-1Supplement", WeightedAutomaton.makeCharRange('\u0080', '\u00FF'));
		put(automata, "LatinExtended-A", WeightedAutomaton.makeCharRange('\u0100', '\u017F'));
		put(automata, "LatinExtended-B", WeightedAutomaton.makeCharRange('\u0180', '\u024F'));
		put(automata, "IPAExtensions", WeightedAutomaton.makeCharRange('\u0250', '\u02AF'));
		put(automata, "SpacingModifierLetters", WeightedAutomaton.makeCharRange('\u02B0', '\u02FF'));
		put(automata, "CombiningDiacriticalMarks", WeightedAutomaton.makeCharRange('\u0300', '\u036F'));
		put(automata, "Greek", WeightedAutomaton.makeCharRange('\u0370', '\u03FF'));
		put(automata, "Cyrillic", WeightedAutomaton.makeCharRange('\u0400', '\u04FF'));
		put(automata, "Armenian", WeightedAutomaton.makeCharRange('\u0530', '\u058F'));
		put(automata, "Hebrew", WeightedAutomaton.makeCharRange('\u0590', '\u05FF'));
		put(automata, "Arabic", WeightedAutomaton.makeCharRange('\u0600', '\u06FF'));
		put(automata, "Syriac", WeightedAutomaton.makeCharRange('\u0700', '\u074F'));
		put(automata, "Thaana", WeightedAutomaton.makeCharRange('\u0780', '\u07BF'));
		put(automata, "Devanagari", WeightedAutomaton.makeCharRange('\u0900', '\u097F'));
		put(automata, "Bengali", WeightedAutomaton.makeCharRange('\u0980', '\u09FF'));
		put(automata, "Gurmukhi", WeightedAutomaton.makeCharRange('\u0A00', '\u0A7F'));
		put(automata, "Gujarati", WeightedAutomaton.makeCharRange('\u0A80', '\u0AFF'));
		put(automata, "Oriya", WeightedAutomaton.makeCharRange('\u0B00', '\u0B7F'));
		put(automata, "Tamil", WeightedAutomaton.makeCharRange('\u0B80', '\u0BFF'));
		put(automata, "Telugu", WeightedAutomaton.makeCharRange('\u0C00', '\u0C7F'));
		put(automata, "Kannada", WeightedAutomaton.makeCharRange('\u0C80', '\u0CFF'));
		put(automata, "Malayalam", WeightedAutomaton.makeCharRange('\u0D00', '\u0D7F'));
		put(automata, "Sinhala", WeightedAutomaton.makeCharRange('\u0D80', '\u0DFF'));
		put(automata, "Thai", WeightedAutomaton.makeCharRange('\u0E00', '\u0E7F'));
		put(automata, "Lao", WeightedAutomaton.makeCharRange('\u0E80', '\u0EFF'));
		put(automata, "Tibetan", WeightedAutomaton.makeCharRange('\u0F00', '\u0FFF'));
		put(automata, "Myanmar", WeightedAutomaton.makeCharRange('\u1000', '\u109F'));
		put(automata, "Georgian", WeightedAutomaton.makeCharRange('\u10A0', '\u10FF'));
		put(automata, "HangulJamo", WeightedAutomaton.makeCharRange('\u1100', '\u11FF'));
		put(automata, "Ethiopic", WeightedAutomaton.makeCharRange('\u1200', '\u137F'));
		put(automata, "Cherokee", WeightedAutomaton.makeCharRange('\u13A0', '\u13FF'));
		put(automata, "UnifiedCanadianAboriginalSyllabics", WeightedAutomaton.makeCharRange('\u1400', '\u167F'));
		put(automata, "Ogham", WeightedAutomaton.makeCharRange('\u1680', '\u169F'));
		put(automata, "Runic", WeightedAutomaton.makeCharRange('\u16A0', '\u16FF'));
		put(automata, "Khmer", WeightedAutomaton.makeCharRange('\u1780', '\u17FF'));
		put(automata, "Mongolian", WeightedAutomaton.makeCharRange('\u1800', '\u18AF'));
		put(automata, "LatinExtendedAdditional", WeightedAutomaton.makeCharRange('\u1E00', '\u1EFF'));
		put(automata, "GreekExtended", WeightedAutomaton.makeCharRange('\u1F00', '\u1FFF'));
		put(automata, "GeneralPunctuation", WeightedAutomaton.makeCharRange('\u2000', '\u206F'));
		put(automata, "SuperscriptsandSubscripts", WeightedAutomaton.makeCharRange('\u2070', '\u209F'));
		put(automata, "CurrencySymbols", WeightedAutomaton.makeCharRange('\u20A0', '\u20CF'));
		put(automata, "CombiningMarksforSymbols", WeightedAutomaton.makeCharRange('\u20D0', '\u20FF'));
		put(automata, "LetterlikeSymbols", WeightedAutomaton.makeCharRange('\u2100', '\u214F'));
		put(automata, "NumberForms", WeightedAutomaton.makeCharRange('\u2150', '\u218F'));
		put(automata, "Arrows", WeightedAutomaton.makeCharRange('\u2190', '\u21FF'));
		put(automata, "MathematicalOperators", WeightedAutomaton.makeCharRange('\u2200', '\u22FF'));
		put(automata, "MiscellaneousTechnical", WeightedAutomaton.makeCharRange('\u2300', '\u23FF'));
		put(automata, "ControlPictures", WeightedAutomaton.makeCharRange('\u2400', '\u243F'));
		put(automata, "OpticalCharacterRecognition", WeightedAutomaton.makeCharRange('\u2440', '\u245F'));
		put(automata, "EnclosedAlphanumerics", WeightedAutomaton.makeCharRange('\u2460', '\u24FF'));
		put(automata, "BoxDrawing", WeightedAutomaton.makeCharRange('\u2500', '\u257F'));
		put(automata, "BlockElements", WeightedAutomaton.makeCharRange('\u2580', '\u259F'));
		put(automata, "GeometricShapes", WeightedAutomaton.makeCharRange('\u25A0', '\u25FF'));
		put(automata, "MiscellaneousSymbols", WeightedAutomaton.makeCharRange('\u2600', '\u26FF'));
		put(automata, "Dingbats", WeightedAutomaton.makeCharRange('\u2700', '\u27BF'));
		put(automata, "BraillePatterns", WeightedAutomaton.makeCharRange('\u2800', '\u28FF'));
		put(automata, "CJKRadicalsSupplement", WeightedAutomaton.makeCharRange('\u2E80', '\u2EFF'));
		put(automata, "KangxiRadicals", WeightedAutomaton.makeCharRange('\u2F00', '\u2FDF'));
		put(automata, "IdeographicDescriptionCharacters", WeightedAutomaton.makeCharRange('\u2FF0', '\u2FFF'));
		put(automata, "CJKSymbolsandPunctuation", WeightedAutomaton.makeCharRange('\u3000', '\u303F'));
		put(automata, "Hiragana", WeightedAutomaton.makeCharRange('\u3040', '\u309F'));
		put(automata, "Katakana", WeightedAutomaton.makeCharRange('\u30A0', '\u30FF'));
		put(automata, "Bopomofo", WeightedAutomaton.makeCharRange('\u3100', '\u312F'));
		put(automata, "HangulCompatibilityJamo", WeightedAutomaton.makeCharRange('\u3130', '\u318F'));
		put(automata, "Kanbun", WeightedAutomaton.makeCharRange('\u3190', '\u319F'));
		put(automata, "BopomofoExtended", WeightedAutomaton.makeCharRange('\u31A0', '\u31BF'));
		put(automata, "EnclosedCJKLettersandMonths", WeightedAutomaton.makeCharRange('\u3200', '\u32FF'));
		put(automata, "CJKCompatibility", WeightedAutomaton.makeCharRange('\u3300', '\u33FF'));
		put(automata, "CJKUnifiedIdeographsExtensionA", WeightedAutomaton.makeCharRange('\u3400', '\u4DB5'));
		put(automata, "CJKUnifiedIdeographs", WeightedAutomaton.makeCharRange('\u4E00', '\u9FFF'));
		put(automata, "YiSyllables", WeightedAutomaton.makeCharRange('\uA000', '\uA48F'));
		put(automata, "YiRadicals", WeightedAutomaton.makeCharRange('\uA490', '\uA4CF'));
		put(automata, "HangulSyllables", WeightedAutomaton.makeCharRange('\uAC00', '\uD7A3'));
		put(automata, "CJKCompatibilityIdeographs", WeightedAutomaton.makeCharRange('\uF900', '\uFAFF'));
		put(automata, "AlphabeticPresentationForms", WeightedAutomaton.makeCharRange('\uFB00', '\uFB4F'));
		put(automata, "ArabicPresentationForms-A", WeightedAutomaton.makeCharRange('\uFB50', '\uFDFF'));
		put(automata, "CombiningHalfMarks", WeightedAutomaton.makeCharRange('\uFE20', '\uFE2F'));
		put(automata, "CJKCompatibilityForms", WeightedAutomaton.makeCharRange('\uFE30', '\uFE4F'));
		put(automata, "SmallFormVariants", WeightedAutomaton.makeCharRange('\uFE50', '\uFE6F'));
		put(automata, "ArabicPresentationForms-B", WeightedAutomaton.makeCharRange('\uFE70', '\uFEFE'));
		put(automata, "Specials", WeightedAutomaton.makeCharRange('\uFEFF', '\uFEFF'));
		put(automata, "HalfwidthandFullwidthForms", WeightedAutomaton.makeCharRange('\uFF00', '\uFFEF'));
		put(automata, "Specials", WeightedAutomaton.makeCharRange('\uFFF0',
                                                                  '\uFFFD'));

		put(automata, "OldItalic", WeightedAutomaton.makeChar('\ud800').concatenate(
                WeightedAutomaton.makeCharRange('\udf00', '\udf2f')));
		put(automata, "Gothic", WeightedAutomaton.makeChar('\ud800').concatenate(
                WeightedAutomaton.makeCharRange('\udf30', '\udf4f')));
		put(automata, "Deseret", WeightedAutomaton.makeChar('\ud801').concatenate(
                WeightedAutomaton.makeCharRange('\udc00', '\udc4f')));
		put(automata, "ByzantineMusicalSymbols", WeightedAutomaton.makeChar('\ud834').concatenate(
                WeightedAutomaton.makeCharRange('\udc00', '\udcff')));
		put(automata, "MusicalSymbols", WeightedAutomaton.makeChar('\ud834').concatenate(
                WeightedAutomaton.makeCharRange('\udd00', '\uddff')));
		put(automata, "MathematicalAlphanumericSymbols", WeightedAutomaton.makeChar('\ud835').concatenate(
                WeightedAutomaton.makeCharRange('\udc00', '\udfff')));
		
		put(automata, "CJKUnifiedIdeographsExtensionB", WeightedAutomaton.makeCharRange('\ud840', '\ud868').concatenate(
                WeightedAutomaton.makeCharRange('\udc00', '\udfff'))
                                                                         .union(WeightedAutomaton
                                                                                        .makeChar('\ud869').concatenate(
                                                                                         WeightedAutomaton
                                                                                                 .makeCharRange('\udc00', '\uded6'))));
		
		put(automata, "CJKCompatibilityIdeographsSupplement", WeightedAutomaton.makeChar('\ud87e').concatenate(
                WeightedAutomaton.makeCharRange('\udc00', '\ude1f')));
		put(automata, "Tags", WeightedAutomaton.makeChar('\udb40').concatenate(
                WeightedAutomaton.makeCharRange('\udc00', '\udc7f')));
		
		put(automata, "PrivateUse", WeightedAutomaton.makeCharRange('\uE000', '\uF8FF')
                                                     .union(WeightedAutomaton.makeCharRange('\udb80', '\udbbe').concatenate(
                                                             WeightedAutomaton.makeCharRange('\udc00', '\udfff'))
                                                                             .union(WeightedAutomaton
                                                                                            .makeChar('\udbbf').concatenate(
                                                                                             WeightedAutomaton
                                                                                                     .makeCharRange('\udc00', '\udffd'))))
                                                     .union(WeightedAutomaton.makeCharRange('\udbc0', '\udbfe').concatenate(
                                                             WeightedAutomaton.makeCharRange('\udc00', '\udfff'))
                                                                             .union(WeightedAutomaton
                                                                                            .makeChar('\udbff').concatenate(
                                                                                             WeightedAutomaton
                                                                                                     .makeCharRange('\udc00', '\udffd')))));

		System.out.println("Building Unicode category automata...");
		Map<String,Set<Integer>> categories = new HashMap<String,Set<Integer>>();
		try {
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(new FileReader("src/Unicode.txt")));
			st.resetSyntax();
			st.whitespaceChars(';', ';');
			st.whitespaceChars('\n', ' ');
			st.wordChars('0', '9');
			st.wordChars('a', 'z');
			st.wordChars('A', 'Z');
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				int cp = Integer.parseInt(st.sval, 16);
				st.nextToken();
				String cat = st.sval;
				Set<Integer> c = categories.get(cat);
				if (c == null) {
					c = new TreeSet<Integer>();
					categories.put(cat, c);
				}
				c.add(cp);
				String ccat = cat.substring(0, 1);
				c = categories.get(ccat);
				if (c == null) {
					c = new TreeSet<Integer>();
					categories.put(ccat, c);
				}
				c.add(cp);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		List<WeightedAutomaton> assigned = new ArrayList<WeightedAutomaton>();
		for (Map.Entry<String,Set<Integer>> me : categories.entrySet()) {
			List<WeightedAutomaton> la1 = new ArrayList<WeightedAutomaton>();
			List<WeightedAutomaton> la2 = new ArrayList<WeightedAutomaton>();
			for (Integer cp : me.getValue()) {
				la1.add(makeCodePoint(cp));
				if (la1.size() == 50) {
					la2.add(WeightedAutomaton.minimize(WeightedAutomaton.union(la1)));
					la1.clear();
				}
			}
			la2.add(WeightedAutomaton.union(la1));
			WeightedAutomaton
                    a = WeightedAutomaton.minimize(WeightedAutomaton.union(la2));
			put(automata, me.getKey(), a);
			assigned.add(a);
		}
		WeightedAutomaton cn = WeightedAutomaton.minimize(automata.get("Char").clone().intersection(
                WeightedAutomaton.union(assigned).complement()));
		put(automata, "Cn", cn);
		put(automata, "C", automata.get("C").clone().union(cn));
	}
	
	private static WeightedAutomaton makeCodePoint(int cp) {
		if (cp >= 0x10000) {
			cp -= 0x10000;
			char[] cu = { (char)(0xd800 + (cp >> 10)), (char)(0xdc00 + (cp & 0x3ff)) };
			return WeightedAutomaton.makeString(new String(cu));
		} else
			return WeightedAutomaton.makeChar((char)cp);
	}

	private static Map<String,WeightedAutomaton> buildMap(String[] exps) {
		Map<String,WeightedAutomaton> map = new HashMap<String,WeightedAutomaton>();
		int i = 0;
		while (i + 1 < exps.length) 
			put(map, exps[i++], new RegExp(exps[i++]).toAutomaton(map));
		return map;
	}
	
	private static void putWith(String[] exps, Map<String,WeightedAutomaton> use) {
		int i = 0;
		while (i + 1 < exps.length)  
			put(automata, exps[i++], new RegExp(exps[i++]).toAutomaton(use));	
	}
	
	private static void putFrom(String name, Map<String,WeightedAutomaton> from) {
		automata.put(name, from.get(name));
	}
	
	private static void put(Map<String,WeightedAutomaton> map, String name, WeightedAutomaton a) {
		map.put(name, a);
		System.out.println("  " + name + ": " + a.getNumberOfStates() + " states, " + a.getNumberOfTransitions() + " transitions");
	}
	
	static WeightedAutomaton getWhitespaceAutomaton() {
		return ws;
	}
}
