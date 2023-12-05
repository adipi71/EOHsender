# EOHsender

EFSA One Health WGS System helping tools for Programmatic Submission

## Sender

Prepares CLI and API Programmatic Submission JSON files for the EFSA One Health WGS System



## Compatibility report

Calculates a compatibility report between two allelic profile result files calculated on the same sample sequences: one (*base*) coming from EFSA Analytical Pipeline and one (*test*) from a testing pipeline.

**Definitions:**

* `NumMissingLociTest`: Number of missing loci in *test* with corresponding detected loci in *base*. The number of missing Loci on *base* is not accounted. 
* `AllelesDiff`: different called alleles under the condition that the related loci have been detected on both *base* and *test*.
* `MAX_DIFF, MIN_ALLELES_DIFF, MAX_ALLELES_DIFF`: Thresholds species dependent

**Rules:**

| threshold | Description |
| --- | --- |
| Perfect | `NumMissingLociTest + AllelesDiff == 0` |
| Acceptable | `NumMissingLociTest + AllelesDiff  <= MAX_DIFF` |
| Warning | `MIN_ALLELES_DIFF < AllelesDiff  < MAX_ALLELES_DIFF` |
| Fail | `AllelesDiff   => MAX_ALLELES_DIFF` |
| Fail | `NumMissingLociTest + AllelesDiff  > MAX_DIFF` |

**Thresholds:**

| Specie | `MIN_ALLELES_DIFF` | `MAX_ALLELES_DIFF` | `MAX_DIFF` |
| --- | --- | --- | --- |
| *L. monocytogenes*  | 4 |  7 | 10 | 
| *Salmonella*  | 5 |  10 | 15 | 
| *STEC*  | 5 |  10 | 15 | 


