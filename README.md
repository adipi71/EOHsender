# EOHsender

EFSA One Health WGS System helping tools for Programmatic Submission

!!! Work  in progress  !!!

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
| perfect | `NumMissingLociTest + AllelesDiff == 0` |
| acceptable | `NumMissingLociTest + AllelesDiff  <= MAX_DIFF` |
| warning | `MIN_ALLELES_DIFF < AllelesDiff  < MAX_ALLELES_DIFF` |
| fail | `AllelesDiff   => MAX_ALLELES_DIFF` |
| fail | `NumMissingLociTest + AllelesDiff  > MAX_DIFF` |

**Thresholds:**

| Specie | `MIN_ALLELES_DIFF` | `MAX_ALLELES_DIFF` | `MAX_DIFF` |
| --- | --- | --- | --- |
| *L. monocytogenes*  | 4 |  7 | 10 | 
| *Salmonella*  | 5 |  10 | 15 | 
| *STEC*  | 5 |  10 | 15 | 

