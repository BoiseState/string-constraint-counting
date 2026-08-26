# Test Coverage Plan

Working branch: `nps/test-coverage`. Goal: build real test coverage over the inverse
solver, progressing outside-in — overarching algorithm first, then forward solving,
then the heavy automaton algorithms in `Model_Acyclic_Inverse`. Bugs found along the
way get fixed in-chunk, each with a failing-then-passing test in its own commit.

## Oracle strategy (no external oracles needed yet)

Two internal oracles carry chunks 1–5:

1. **Witness replay** — every `sat` answer produces concrete input strings. Replaying
   the graph's actual `java.lang.String` operations on those witnesses and checking
   each predicate takes its recorded branch verifies soundness for free.
   `Solver.isSound(int id, String actualValue)` already exists as a starting point.
2. **Exhaustive small-bound enumeration** — over a tiny alphabet (e.g. `a,b`) and
   bound ≤ 4, enumerate every string an automaton accepts (dk.brics
   `getFiniteStrings` / the model's accessors) and compare against brute force:
   apply the real `String` operation to every enumerated input and compare result
   sets. This is a complete oracle for the operation classes and `inv_*` methods at
   small scale.

What these *cannot* verify: `unsat` claims (completeness). Those become known-answer
tests for now, upgraded later when external oracles arrive (candidates: Z3/CVC5 on
the `.smt2` sources of the fixtures, or the pre-refactor concrete-solver oracle from
git history).

## Chunk 0 — infrastructure

- Test utilities in `src/test/java`:
  - `GraphBuilder`: construct `InvDefaultDirectedGraph` fixtures programmatically
    (no JSON round-trip) — vertex + `SymbolicEdge` wiring, topological IDs,
    `computePredicateDependencies()`.
  - `AutomatonAssert`: assert a model accepts/rejects given strings, and
    language-equality up to a bound (enumeration compare).
  - `WitnessReplay`: interpret a graph path over real `java.lang.String` ops and
    check predicate outcomes for a given assignment.
  - In-process pipeline runner that returns the `SolutionSet` from
    `Reporter_Inverse_BFS` instead of scraping stdout (a small accessor seam is
    acceptable; no behavior change — golden tests must stay green).
- Add JaCoCo (Java-8-compatible version) to track coverage per chunk.
- The 17 golden-file tests stay untouched as the output-regression harness.

## Chunk 1 — overarching algorithm (end-to-end)

- Parameterized end-to-end test: for each fixture graph, run the pipeline in-process,
  assert sat/unsat as recorded, and **witness-replay every sat assignment**.
- Extend the fixture set to cover each `Operation` at least once, plus the
  historically buggy shapes (see git log): `indexOf`, `contains`,
  `startsWith`/`endsWith` disjuncts, multi-predicate dependency graphs,
  negated predicates (dual-prop complement).
- Run the same harness over `graphs/inverse` / `graphs/benchmarks` as a slower,
  tagged suite (not part of default `mvn test`).
- Exit criteria: every sat answer across fixtures + benchmark suites replay-verified.

## Chunk 2 — graph loading and ICG construction

- `SolveMain.loadGraph`: JSON → graph invariants (IDs topologically ordered,
  predicate/symbolic-input maps correct) on small hand-written JSON fixtures.
- `InvDefaultDirectedGraph.computePredicateDependencies()` /
  `orderIDsTopologically()` on `GraphBuilder`-made graphs with known answers.
- `ICGBuilder`: for each `Operation` code, assert the produced `Inv*Constraint`
  type and its arg/next/prev wiring; unknown-op handling.
- `Parser_2` dispatch: on tiny graphs, assert the forward sweep produces the
  expected per-variable models (via enumeration), not just expected stdout.

## Chunk 3 — forward solving (`Solver` + forward ops of the model)

- Per-operation forward semantics via the enumeration oracle: build inputs with
  `Model_Acyclic_Inverse_Manager`, apply the forward op (`append`, `substring`,
  `trim`, `replace`, `delete`, `insert`, `setCharAt`, `setLength`, `reverse`,
  case ops, …), compare accepted-language against brute force.
- Predicate assertions (`assertEquals`, `assertContains`, `assertStartsWith`, …):
  asserted model = brute-force filter of the input language.
- `Solver` bookkeeping: temp ids, `revertLastPredicate`, `intersectPrevious`,
  symbolic/concrete map state across a scripted op sequence.

## Chunk 4 — backward propagation (`Reporter_Inverse_BFS` + `Inv*Constraint`s)

- Per `Inv*Constraint`: constrain the node's output model, run its backprop step,
  check the computed input model is a correct pre-image (enumeration: every
  accepted input maps into the constrained output language; flag over- and
  under-approximation separately — some ops are deliberately imprecise, document
  which as tests are written).
- BFS driver behavior on multi-predicate graphs: `getNecessaryPredicates` /
  `getDependedPredicates` ordering, bound-increase retry loop, solution
  intersection across predicates.

## Chunk 5 — heavy automaton algorithms (`model/`)

- The 13 `model/operations/` classes, each vs. exhaustive brute force at small
  bounds: `PreciseSubstring`, `PrecisePrefix`, `PreciseSuffix`, `PreciseDelete`,
  `PreciseInsert`, `PreciseSetCharAt`, `PreciseSetLength`, `PreciseTrim`,
  `IgnoreCase`, `InverseReplaceCC`, `InverseLowerCase`, `InverseUpperCase`;
  `StringModelCounter.ModelCount` vs. enumerated-set size.
- The ~30 `inv_*` methods on `Model_Acyclic_Inverse`, prioritized by recent bug
  history: `inv_indexOf`, `inv_contains`, `inv_concat` (both arg positions),
  the replace family, substring family, then the rest.
- Edge cases everywhere: empty string, empty language, full-alphabet any-string,
  bound-boundary lengths, single-char alphabet.

## Chunk 6 — validation at scale (needs user input)

- Differential runs over all of `graphs/` vs. the pre-refactor jar (available from
  git history) for output equality where output should not have changed.
- External oracles once provided (SMT solver on `.smt2` twins, or other): upgrade
  unsat known-answer tests to oracle-checked, and cross-check model counts.
- Fuzzing option: random small graphs from `GraphBuilder` + witness replay, as a
  soak test.

## Conventions

- One chunk per PR-sized unit; within a chunk, bugfixes are separate commits, each
  with the failing test first.
- A bugfix that changes solver output updates golden files deliberately in the same
  commit, with the reason in the commit message.
- Every new test must be deterministic (fixed seeds if randomized) and fast enough
  for default `mvn test`; slow enumeration/benchmark suites go behind a tag/profile.
