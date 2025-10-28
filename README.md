# String Constraint Counting

A Java-based string constraint solver that analyzes symbolic string operations using automaton-based techniques. The tool uses an acyclic automaton model to solve string constraints represented as directed graphs in JSON or SMT2 format.

## Build

Build the project using Maven:

```bash
mvn install
```

This creates the executable JAR at:
`target/string-constraint-solvers-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Running the Solver

The simplest way to run the solver is using the provided `run` script, which automatically builds the project if needed:

```bash
./run <graph-file> <length>
```

**Example:**
```bash
./run graphs/inverse/inverse_case_1.json 2
```

The script runs the solver with the acyclic automaton model (version 2) configured for satisfiability reporting.

### Input Files

Provide constraint graphs in either:
- **JSON format**: See example files in the root directory and `graphs/` subdirectory
- **SMT2 format**: Standard SMT-LIB format for string constraints

Example JSON constraint files:
- Root directory: Simple examples (HelloWorld.json, CharAt.json, etc.)
- `graphs/inverse/`: Inverse solver test cases
- `graphs/benchmarks/`: Performance benchmarks
- `graphs/real/`: Real-world extracted constraints

## Test

Run the test suite:

```bash
mvn test
```

## Additional Information

See the code repository at https://github.com/BoiseState/string-constraint-counting for more details.
