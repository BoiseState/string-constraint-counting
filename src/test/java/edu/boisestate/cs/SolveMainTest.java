package edu.boisestate.cs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
        restoreStreams();
        String[] args = {"Delete.json", "-s", "inverse", "-v", "2", "-l", "5"};
        SolveMain.main(args);
    }

    @Test
    public void testContains(){
        restoreStreams();
        String expectedOutput = "";
        try {
            expectedOutput = new String (Files.readAllBytes(Paths.get(outPath + "concat.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] args = {"notContains.json", "-s", "inverse", "-v", "2", "-l", "2"};
        SolveMain.main(args);
//        assertEquals(expectedOutput, outContent.toString());
    }

//    @Test
//    public void smtQueryPlaygroundTest(){
//        restoreStreams();
//        String query = "(declare-fun s () String)\n" +
//                "(assert (not (str.contains \"HelloWorld\" s)))\n" +
//                "(check-sat)\n" +
//                "(get-model)";
//        String tempFile = inPath + "query/smtQueryPlayground.smt2";
//        try {
//            Files.createDirectories(Paths.get(inPath + "query"));
//            Files.write(Paths.get(tempFile), query.getBytes());
//            ProcessBuilder pb = new ProcessBuilder("java", "-cp","~/Repos/SMT-parser-generator/target/GenJSONs-1.0-SNAPSHOT-jar-with-dependencies", "edu.boisestate.cs.MainJSON", inPath+"query");
//
//            System.out.println(pb.command());
//            pb.start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void testConcat() {
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
