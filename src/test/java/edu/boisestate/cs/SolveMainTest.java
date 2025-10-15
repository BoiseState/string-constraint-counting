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

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

//	@Test
//	public void testSolve() {
//		restoreStreams();
//		String[] args = {"../smt-bench/benchmarks/bass/woorpje/01_track_10.smt2.json", "-s", "inverse", "-v", "2", "-l", "5", "-d"};
//		SolveMain.main(args);
//	}

	// friendly reminder generator has bugs and is incomplete :)
	@Test
    public void smtQueryPlaygroundTest(){
        restoreStreams();
        try {
			// generator takes directory as input and converts smt2 files
			File file = new File("src/test/java/edu/boisestate/cs");
			String javaPath = "/usr/lib/jvm/java-21-openjdk-amd64/bin/java";
            ProcessBuilder pb = new ProcessBuilder(
					javaPath, "-cp",
					"/home/nat/Repos/SMT-parser-generator/target/GenJSONs-1.0-SNAPSHOT-jar-with-dependencies.jar",
					"edu.boisestate.cs.MainJSON",
					file.getAbsolutePath()
			);
			pb.redirectErrorStream(true);
            pb.start();

			String[] args = {"output_cs/smt-input.smt2.json", "-s", "inverse", "-v", "2", "-l", "8"};
			SolveMain.main(args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Test
	public void testLength() {
		restoreStreams();
		String[] args = {"Length.json", "-s", "inverse", "-v", "2", "-l", "5"};
		SolveMain.main(args);
	}

	@Test
	public void testIsEmpty() {
		restoreStreams();
		String[] args = {"isEmpty.json", "-s", "inverse", "-v", "2", "-l", "5"};
		SolveMain.main(args);
	}

    @Test
    public void testReverse() {
        restoreStreams();
        String[] args = {"src/test/resources/in/Reverse.json", "-s", "inverse", "-v", "2", "-l", "9"};
        SolveMain.main(args);
    }
    @Test
    public void testInsert() {
        restoreStreams();
        String[] args = {"Insert.json", "-s", "inverse", "-v", "2", "-l", "11"};
        SolveMain.main(args);
    }

    @Test
    public void testTrim() {
        restoreStreams();
        String[] args = {"Trim.json", "-s", "inverse", "-v", "2", "-l", "5"};
        SolveMain.main(args);
    }

    @Test
    public void testIndexOf() {
        restoreStreams();
        String[] args = {"IndexOf.json", "-s", "inverse", "-v", "2", "-l", "5"};
        SolveMain.main(args);
    }

    @Test
    public void testCharAt(){
        restoreStreams();
        String[] args = {"CharAt.json", "-s", "inverse", "-v", "2", "-l", "5"};
        SolveMain.main(args);
    }

    @Test
    public void testDeleteAgain(){
        String[] args = {"Delete.json", "-s", "inverse", "-v", "2", "-l", "5"};
        SolveMain.main(args);
		assertEquals("sat,\n4: \"0\"\n", outContent.toString());
    }

    @Test
    public void testContains(){
        String expectedOutput = "sat,\n1: \"0\"\n";
        String[] args = {"notContains.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
        assertEquals(expectedOutput, outContent.toString());
    }



    @Test
    public void testConcat() {
//		restoreStreams();
        String expectedOutput = "";
        try {
            expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + "concat.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] args = {inPath + "concat_isEmpty_equals_contains_l2_d2_bench.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testDelete() {
        String expectedOutput = "";
        try {
            expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + "delete.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] args = {inPath + "delete_isEmpty_equals_contains_l2_d2_bench.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testReplace() {
        String expectedOutput = "";
        try {
            expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + "replace.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] args = {inPath + "replace_isEmpty_equals_contains_l2_d2_bench.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testSubstring() {
        String expectedOutput = "";
        try {
            expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + "substring.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] args = {inPath + "substring12_isEmpty_equals_contains_l2_d2_bench.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testToLower() {
        String expectedOutput = "";
        try {
            expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + "toLower.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] args = {inPath + "toLowerCase_isEmpty_equals_contains_l2_d2_bench.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
        assertEquals(expectedOutput, outContent.toString());
    }
}
