# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A-Str: an acyclic-automata-based string constraint solver for Java programs. Symbolic
string inputs are represented as deterministic acyclic automata (dk.brics) and manipulated
across a program's data-flow graph. The tool consumes a proprietary `.json` graph format
describing an execution path of a Java program, reports `sat`/`unsat` per string constraint,
and generates concrete satisfying input assignments by inverse (backward) propagation.

## Build

Maven, Java 8 source/target.

```bash
mvn install
```

This produces the runnable jar at:
`target/string-constraint-solvers-1.0-SNAPSHOT-jar-with-dependencies.jar`

The build depends on a prebuilt system-scoped jar checked into `lib/GenJSONs-1.0-SNAPSHOT-jar-with-dependencies.jar`
(used to convert `.smt2` input to the tool's `.json` graph format). `updateSPFJar.sh` rebuilds
the jar and copies it into a sibling `../SPF/jpf-symbc/lib/MAS.jar` for use by a related SPF project.

## Running

```bash
./run <graph-file> [length] [-d]
```

`run` invokes the jar directly (default length bound 2). Main entry point is
`edu.boisestate.cs.SolveMain`.

CLI (see `SolveMain -h` / `CommandLine.java`):

```
java edu.boisestate.cs.SolveMain <graph-file> [-d] [-l <length>]
```

- `-l` sets the initial length bound for symbolic inputs (default 15); `-d` enables debug
  output. The inverse solver is the only mode — there is no solver/reporter selection.
- Input can be `.json`, `.smt2` (auto-converted to `.json` by shelling out to the bundled
  `GenJSONs` jar), or `.ser` (a serialized `AStrBenchmarkBundle` — pre-built graph, alphabet
  string, and bound — which bypasses `loadGraph` and `-l`, and is used for benchmark
  reproduction).

`runLengths.sh` retries the inverse solver at increasing `-l` bound until it gets a `sat`
result (gives up after length 15). Benchmark suites live in `graphs/`.

## Testing

JUnit 4, run via Maven Surefire.

```bash
mvn test
mvn -Dtest=SolveMainTest#<methodName> test   # single test method
```

`runTestAndMakeOutputFile.sh <methodName>` runs one `SolveMainTest` method and captures its
(cleaned) stdout into `src/test/resources/out/<methodName>.txt` — these captured-output files
are the primary regression check for solver behavior: every test invokes `SolveMain.main`
with `-s inverse` and asserts the printed output matches the stored golden file
byte-for-byte. Update a golden file only deliberately, when solver output is intentionally
changing. Input graph fixtures live in `src/test/resources/in/` as `.json` or `.smt2`.

## Architecture

Single-purpose pipeline: **load graph → forward propagation (build per-variable automata) →
inverse constraint graph → backward propagation (generate satisfying inputs) → report**.
`SolveMain.run_Acyclic_Inverse_r3` wires the four collaborators together:

```
Model_Acyclic_Inverse_Manager → Solver_Inverse → Parser_2 → Reporter_Inverse_BFS
```

**Graph (`graph/`)**: The input `.json` describes a Java execution path as a DAG of
`PrintConstraint` vertices connected by `SymbolicEdge`s. `SolveMain.loadGraph` builds an
`InvDefaultDirectedGraph` (extends jgrapht's `DefaultDirectedGraph<PrintConstraint,
SymbolicEdge>`), which additionally assigns topological IDs and computes inter-predicate
dependencies via `computePredicateDependencies()`. The `Inv*Constraint` classes
(`I_Inv_Constraint`/`A_Inv_Constraint` + ~23 concrete types like `InvConstraintConcatSym`,
`InvConstraintSubString*`, `InvConstraintReplace*`) each model one Java `String` operation
as a node in the *inverse constraint graph* built after forward propagation.
`Operation` is the enum of recognized string operations (serialized by name in `.ser`
bundles — don't rename constants). `SolutionSet`/`SolutionSetInternal` hold candidate
assignments during backward propagation. `PrintConstraintComparator` orders constraints for
deterministic output.

**Model (`model/`)**: `Model_Acyclic_Inverse` (~3k lines) is the automaton engine — one flat
class holding a dk.brics `Automaton` per symbolic value, with both forward operations
(concat, substring, replace, assertions, …) and their `inv_*` inverses used during backward
propagation. It works directly on `dk.brics.automaton` and `dk.brics.string.stringoperations`
types; `AutomatonHelper` supplies transition inspection. `Model_Acyclic_Inverse_Manager` is
the small factory (`createString`/`createAnyString`) parameterized by alphabet and bound.
`model/operations/` holds the custom automaton transformations (`Precise*` for
index-sensitive operations, `Inverse*` for backward steps, `StringModelCounter` for counting
accepted strings up to a bound).

**Solver (`solvers/`)**: `Solver` holds the per-variable model maps and the ~40 forward
operations that `Parser_2` invokes while sweeping the graph; `Solver_Inverse` (interface
`I_Solver_Inverse`) adds the inverse-solving entry points used during backward propagation.

**Parsing**: `Parser_2` translates each graph vertex (by `Operation`) into the corresponding
`Solver` calls during the forward sweep, tracking temp variables and predicate reversion.

**Reporting (`reporting/`)**: `A_Reporter.run()` is the top-level driver — it walks the graph
in `PrintConstraintComparator` order, feeding each vertex to `Parser_2`, then hands off to
`Reporter_Inverse_BFS`, which builds the inverse constraint graph via `ICGBuilder` (the
`Operation` → `Inv*Constraint` factory) and performs BFS backward propagation to produce and
print concrete satisfying inputs.

**Serialization**: `AStrBenchmarkBundle` bundles `{InvDefaultDirectedGraph graph, String
alphabetString, int bound}` for `.ser`-based benchmark reproduction. Its object graph pins
`InvDefaultDirectedGraph`, `PrintConstraint`, `SymbolicEdge`, `Operation`, and jgrapht 0.9.1
internals — moving/renaming those classes or their fields invalidates existing `.ser` files
(they can be regenerated from `.json`).

## Working in this codebase

- Adding support for a new Java `String` method touches four places: an `Operation` case in
  `Parser_2` (forward), the forward + `inv_*` methods in `Model_Acyclic_Inverse`, a new
  `Inv*Constraint` class in `graph/`, and its wiring in `ICGBuilder`.
- Solver output is regression-checked byte-for-byte by the golden files — even changing
  print order or an incidental `println` fails the suite. Keep collection iteration order
  stable in anything that feeds output.
- The codebase was pruned and flattened (2026) from a multi-pipeline research tool down to
  this single inverse path; `git log` before that point shows the removed JSA/bounded/
  weighted/model-count pipelines if historical reference is needed.
