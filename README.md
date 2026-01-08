# A-Str

An acylic automata based string constraint solver for Java programs. Symbolic string inputs are represented as deterministic acylic automata and manipulated throughout the course of a programs data flow graph to find satisfying assignments. The tool accepts a proprietary .json format that represents an execution path in a Java progam and returns `unsat` or `sat` and an example assignment set.

## Build

The project is built using Maven. If necessary install Maven with, for example:
```bash
sudo apt install -y maven
```

When at the root of this project where the `pom.xml` is located, run:

```bash
mvn install
```
This creates the executable JAR at:
`target/string-constraint-solvers-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Running the Solver

```bash
./run <graph-file>
```

The `run` script:
- Builds the project using Maven if this has not been done before
- Converts an input `.smt2` file to `.json` 
- Runs the solver using the JAR, with the necessary arguments, on the input provided

It takes one argument, the input file to be solved. Note that this script does not provide any guards for time outs or memory limits so use it with caution and/or add the desired limitations.

**Examples:**

```bash
./run graphs/benchmarks/concat_isEmpty_equals_contains_l2_d2_bench.json
```

## Additional Information

The repository for the development of this tool can be found at https://github.com/BoiseState/string-constraint-counting
