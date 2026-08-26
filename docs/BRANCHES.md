# Branch Landscape

Snapshot as of 2026-08-26. Local checkouts only track `nps/dev` (current) and `master`; the
rest exist only on `origin`.

## All branches

| Branch | Last commit | vs `master` | vs `nps/dev` |
|---|---|---|---|
| `master` | 2021-05-17 (elenasherman) | — | 192 behind |
| **`nps/dev`** (current) | 2026-03-20 (Nathanael Steven, "no printouts") | 192 ahead, 0 behind | — |
| `nps/main` | 2026-01-08 (natsteven) | 191 ahead | 1 behind, 0 ahead — strict ancestor of `nps/dev` |
| `nps/paper-clean` | 2025-10-30 (natsteven) | 176 ahead | 21 behind, 5 ahead — diverged from `nps/dev` at `e363463` (2025-10-15) |
| `nps/eager-concat` | 2025-10-01 (natsteven, "broken eager concat") | 165 ahead | 28 behind, 1 ahead — diverged at `6284000` (2025-10-01) |
| `mjr/solvemain` | 2024-08-13 (esherman77) | 95 ahead | 97 behind, 0 ahead — strict ancestor of `nps/dev` |
| `new-refactor-string-methods` | 2021-11-07 (Phillip) | 63 ahead | 159 behind, 30 ahead — long-diverged side branch |
| `new-refactor` | 2021-07-27 (esherman77) | 42 ahead | 151 behind, 1 ahead — diverged at `45a449b` (2021-06-22) |
| `mjr/merge` | 2021-06-22 (Marlin Roberts) | 29 ahead | 163 behind, 0 ahead — strict ancestor of `nps/dev` |
| `refactor` | 2021-05-17 (Marlin Roberts) | 4 behind, 10 ahead | 196 behind, 10 ahead — only branch not a descendant of `master` |

## Details

### `nps/main` — effectively a snapshot of `nps/dev`
Strict ancestor of `nps/dev`, missing only the very last commit (`561392d "no printouts"`). No
unique commits of its own. Looks like a stable checkpoint of `dev` from just before that last
cleanup, not a diverging line of work.

### `nps/paper-clean` — an artifact-packaging snapshot, now stale
Forked off `nps/dev` on 2025-10-15 (at `e363463`), then diverged in a completely different
direction from `dev`'s subsequent work:

- Its 5 unique commits (`artifact init?`, `readme being udpate`, `ready`, `artifact ready`,
  `gen update`) are about stripping the repo down for a paper/artifact submission — deleting
  the `lib/` repo jars, test resources, `temp/*.png` debug images, `SolveMainTest.java`, and
  `updateSPFJar.sh`, while adding a `tools/smtlib-converter.jar`. It's a pruned distribution
  package, not development.
- Meanwhile `dev` moved on with 21 more commits of real solver work after that fork point:
  index-of fixes, disjunct/starts-ends-with fixes, contains fixes, ID determinism, dual-prop
  complement handling, refactors, etc.

So `paper-clean` is now behind on substance (missing 21 fix commits) while also being
incompatible in shape (it deleted files `dev` still uses/updated). It's not mergeable as-is
without conflicts; reconciling it would mean re-applying the "strip for release" cleanup on
top of current `dev` rather than merging in the other direction.

### `nps/eager-concat` — algorithm variant
Single experimental commit off `dev`. An algorithm variant, not evaluated for merge-worthiness
here.

### Everything else (`master`, `refactor`, `mjr/*`, `new-refactor*`)
All either strict ancestors of `nps/dev` (already fully absorbed, nothing to do) or
long-abandoned 2021-era refactor attempts that diverged before `dev`'s current line existed.

## Bottom line

`nps/dev` is the only branch with live, unmerged development value beyond itself. `nps/main`
is redundant. `nps/paper-clean` is a point-in-time release package that would need to be
regenerated from current `dev` (not merged) if an updated artifact bundle is needed.
`eager-concat` is a parked algorithm experiment.
