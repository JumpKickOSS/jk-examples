# agent-graph — tokens to the answer

Run 2026-10-22 (jk constants + tighter banner), jk 0.20.0. The project's searchable text is ~77,053 tokens. Each cell is **find / hold**: tokens read until an answer file is named, and until its code has been read. Lower is better; ✓ means the method's own output named the answer file, ✗ that the agent had to open it anyway (counted).

| Task | grep | graphify | jk |
|---|---|---|---|
| weekend-uplift | 835 / 835 ✓ | 1,620 / 2,013 ✓ | 1,560 / 1,645 ✓ |
| double-booking | 62,532 / 62,532 ✓ | 1,607 / 2,192 ✓ | 1,543 / 1,650 ✓ |
| cancel-rules | 13,490 / 13,490 ✓ | 1,621 / 1,869 ✓ | 1,530 / 1,597 ✓ |
| confirmation-fields | 4,074 / 4,074 ✓ | 1,793 / 1,793 ✗ | 1,545 / 1,600 ✓ |
| staff-sign-in | 3,523 / 3,523 ✓ | 1,655 / 2,629 ✓ | 1,552 / 1,652 ✓ |
| occupancy | 2,531 / 2,531 ✓ | 1,621 / 2,731 ✓ | 1,567 / 1,817 ✓ |
| availability-endpoint | 7,943 / 7,943 ✓ | 1,048 / 1,048 ✗ | 1,566 / 1,694 ✓ |
| confirmation-codes | 6,029 / 6,029 ✓ | 1,631 / 1,921 ✓ | 1,535 / 1,637 ✓ |
| problem-details | 1,003 / 1,003 ✓ | 1,642 / 2,166 ✓ | 1,561 / 1,719 ✓ |
| long-stay-discount | 1,019 / 1,019 ✓ | 1,666 / 2,059 ✓ | 1,567 / 1,652 ✓ |
| **total** | **102,979 / 102,979** (10/10) | **15,904 / 20,421** (8/10) | **15,526 / 16,663** (10/10) |

## Ratchet

A later run must not raise jk's totals above **15,526** (find) or **16,663** (hold) tokens, or find fewer than **10** of 10 answers in jk's own output.
