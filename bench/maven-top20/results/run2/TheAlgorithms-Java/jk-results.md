# jk results — FAIL

**FAIL** · test · `com.thealgorithms:Java` · #9 · 13.4s · **exit 1** · jid 11
trigger: cli · commit: b6fb8ad · jk 0.13.7

- `com.thealgorithms:Java` `run-tests`

Tests: **1 failed** · 9744 passed (9745 total) · _took 54.1s_

## Files

- High-level report (this file): `/home/bsant/src/scratch/maven-corpus/TheAlgorithms-Java/target/jk-results.md`
- Step-by-step transcript: `/home/bsant/.jk/state/builds/projects/0187dac78cc21adebb63aedb469fb744/runs/9/details.jsonl` — JSONL, same shape as `--output json`
- JUnit XML: `target/reports/test-results/`

## Tests

**99%** pass rate · **1 failure** out of **9745** tests · _took 54.1s_

| Package | Fail | Skip | Pass | Total |
|---|---|---|---|---|
| com.thealgorithms.others | 0 | 0 | 284 | 284 |
| com.thealgorithms.datastructures.trees | 0 | 0 | 298 | 298 |
| com.thealgorithms.scheduling | 0 | 0 | 71 | 71 |
| com.thealgorithms.maths | 0 | 0 | 1088 | 1088 |
| com.thealgorithms.backtracking | 0 | 0 | 95 | 95 |
| com.thealgorithms.ciphers | 0 | 0 | 146 | 146 |
| com.thealgorithms.datastructures.heaps | 0 | 0 | 88 | 88 |
| com.thealgorithms.prefixsum | 0 | 0 | 39 | 39 |
| com.thealgorithms.dynamicprogramming | 0 | 0 | 490 | 490 |
| com.thealgorithms.sorts | 1 | 0 | 3461 | 3462 |
| com.thealgorithms.bitmanipulation | 0 | 0 | 259 | 259 |
| com.thealgorithms.strings | 0 | 0 | 518 | 518 |
| com.thealgorithms.datastructures.queues | 0 | 0 | 136 | 136 |
| com.thealgorithms.streaming | 0 | 0 | 177 | 177 |
| com.thealgorithms.datastructures.lists | 0 | 0 | 195 | 195 |
| com.thealgorithms.graph | 0 | 0 | 126 | 126 |
| com.thealgorithms.geometry | 0 | 0 | 72 | 72 |
| com.thealgorithms.divideandconquer | 0 | 0 | 30 | 30 |
| com.thealgorithms.searches | 0 | 0 | 268 | 268 |
| com.thealgorithms.datastructures.graphs | 0 | 0 | 171 | 171 |
| com.thealgorithms.slidingwindow | 0 | 0 | 36 | 36 |
| com.thealgorithms.physics | 0 | 0 | 76 | 76 |
| com.thealgorithms.conversions | 0 | 0 | 375 | 375 |
| com.thealgorithms.compression | 0 | 0 | 104 | 104 |
| com.thealgorithms.datastructures.caches | 0 | 0 | 117 | 117 |
| com.thealgorithms.datastructures.buffers | 0 | 0 | 74 | 74 |
| com.thealgorithms.greedyalgorithms | 0 | 0 | 78 | 78 |
| com.thealgorithms.datastructures.bloomfilter | 0 | 0 | 18 | 18 |
| com.thealgorithms.stacks | 0 | 0 | 214 | 214 |
| com.thealgorithms.audiofilters | 0 | 0 | 10 | 10 |
| com.thealgorithms.datastructures.disjointsetunion | 0 | 0 | 20 | 20 |
| com.thealgorithms.randomized | 0 | 0 | 30 | 30 |
| com.thealgorithms.datastructures.hashmap.hashing | 0 | 0 | 94 | 94 |
| com.thealgorithms.misc | 0 | 0 | 76 | 76 |
| com.thealgorithms.ciphers.a5 | 0 | 0 | 9 | 9 |
| com.thealgorithms.machinelearning | 0 | 0 | 37 | 37 |
| com.thealgorithms.datastructures.stacks | 0 | 0 | 64 | 64 |
| com.thealgorithms.matrix | 0 | 0 | 67 | 67 |
| com.thealgorithms.strings.zigZagPattern | 0 | 0 | 1 | 1 |
| com.thealgorithms.lineclipping | 0 | 0 | 11 | 11 |
| com.thealgorithms.datastructures.crdt | 0 | 0 | 33 | 33 |
| com.thealgorithms.recursion | 0 | 0 | 40 | 40 |
| com.thealgorithms.scheduling.diskscheduling | 0 | 0 | 21 | 21 |
| com.thealgorithms.puzzlesandgames | 0 | 0 | 17 | 17 |
| com.thealgorithms.maths.prime | 0 | 0 | 42 | 42 |
| com.thealgorithms.devutils.entities | 0 | 0 | 20 | 20 |
| com.thealgorithms.datastructures.bag | 0 | 0 | 18 | 18 |
| com.thealgorithms.io | 0 | 0 | 3 | 3 |
| com.thealgorithms.datastructures.dynamicarray | 0 | 0 | 27 | 27 |

### Failed tests
#### com.thealgorithms.sorts.SelectionSortRecursiveTest
##### `shouldAcceptWhenRandomListIsPassed()` — _took 23ms_
```
java.lang.StackOverflowError
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:59)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
	at com.thealgorithms.sorts.SelectionSortRecursive.findMinIndex(SelectionSortRecursive.java:56)
…
```

## Failed steps

| Module | Task | Status | Time |
|---|---|---|---|
|  | `run-tests` | FAIL | 12.9s |

_6 tasks skipped (cache)._

