package edu.boisestate.cs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class SolveMainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final String outPath = "src/test/resources/out/";
    private final String inPath = "src/test/resources/in/";
	private String in;
	private String expect;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

	@Test
	public void testSolve() {
		restoreStreams();
		String[] args = {inPath + "smt-input.smt2", "-s", "inverse", "-v", "2", "-l", "5"};
		SolveMain.main(args);
	}

	// friendly reminder generator has bugs and is incomplete :)
//	@Test
//    public void smtQueryPlaygroundTest(){
//        restoreStreams();
//		String jsonOutputPath = inPath + "smt-input.smt2.json";
//        try {
//			// generator takes directory as input and converts smt2 files
//			File file = new File(inPath + "smt-input.smt2");
//			String javaPath = "/usr/lib/jvm/java-21-openjdk-amd64/bin/java";
//            ProcessBuilder pb = new ProcessBuilder(
//					javaPath, "-cp",
//					"/home/nat/Repos/SMT-parser-generator/target/GenJSONs-1.0-SNAPSHOT-jar-with-dependencies.jar",
//					"edu.boisestate.cs.MainJSON",
//					file.getAbsolutePath(),
//					jsonOutputPath
//			);
////			pb.redirectErrorStream(true);
//            pb.start();
//
//			String[] args = {jsonOutputPath, "-s", "inverse", "-v", "2", "-l", "8"};
//			SolveMain.main(args);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

	@Test
	public void testEmptyContains() {
		in = "emptyContains.smt2";
		expect = "emptyContains.txt";
		runExpected(in, expect);
	}

	@Test
	public void testLength() {
		in = "Length.json";
		expect = "length.txt";
		runExpected(in, expect);
	}

	@Test
	public void testIsEmpty() {
		in = "isEmpty.json";
		expect = "isEmpty.txt";
		runExpected(in, expect);
	}

	@Test
	public void testReverse() {
		in = "Reverse.json";
		expect = "testReverse.txt";
		runExpected(in, expect);
	}
	@Test
	public void testInsert() {
		in = "Insert.json";
		expect = "testInsert.txt";
		runExpected(in, expect);
	}

	@Test
	public void testTrim() {
		in = "Trim.json";
		expect = "testTrim.txt";
		runExpected(in, expect);
	}

	@Test
	public void testIndexOf() {
		in = "IndexOf.json";
		expect = "testIndexOf.txt";
		runExpected(in, expect);
	}

	@Test
	public void testCharAt(){
		in = "CharAt.json";
		expect = "testCharAt.txt";
		runExpected(in, expect);
	}

	@Test
	public void testDeleteAgain(){
		in = "Delete.json";
		expect = "testDeleteAgain.txt";
		runExpected(in, expect);
	}

	@Test
	public void testContains(){
		in = "notContains.json";
		expect = "testContains.txt";
		runExpected(in, expect);
	}

	@Test
	public void notEmptyContainsTest() {
		in = "notEmptyContainsTest.smt2";
		expect = "notEmptyContainsTest.txt";
		runExpected(in, expect);
	}


    @Test
    public void testConcat() {
		in = "concat_isEmpty_equals_contains_l2_d2_bench.json";
		expect = "concat.txt";
		runExpected(in, expect);
    }

    @Test
    public void testDelete() {
		in = "delete_isEmpty_equals_contains_l2_d2_bench.json";
		expect = "delete.txt";
		runExpected(in, expect);
    }

    @Test
    public void testReplace() {
		in = "replace_isEmpty_equals_contains_l2_d2_bench.json";
		expect = "replace.txt";
		runExpected(in, expect);
	}

    @Test
    public void testSubstring() {
		in = "substring12_isEmpty_equals_contains_l2_d2_bench.json";
		expect = "substring.txt";
			   	runExpected(in, expect);
    }

    @Test
    public void testToLower() {
		in = "toLowerCase_isEmpty_equals_contains_l2_d2_bench.json";
		expect = "toLower.txt";
       	runExpected(in, expect);
    }

	private void runExpected(String inFile, String outFile) {
		String expectedOutput = "";
		try {
			expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + outFile)));
		} catch (Exception e) {
			assert(false);
			e.printStackTrace();
		}
		String[] args = {inPath + inFile, "-s", "inverse", "-v", "2", "-l", "2"};
		SolveMain.main(args);
		assertEquals(expectedOutput, outContent.toString());
	}

	private void run(String inFile){
		restoreStreams();
		String[] args = {inPath + inFile, "-s", "inverse", "-v", "2", "-l", "2"};
		SolveMain.main(args);
	}
}
