# EOHsender

EFSA One Health WGS System helping tools for Programmatic Submission

!!! README is WORK in progress  !!!

## Sender

Prepares CLI and API Programmatic Submission JSON files for the EFSA One Health WGS System

## Compatibility report
Calculates a compatibility report between two allelic profile calculated on the same sample sequences: one (base) coming from EFSA Analytical Pipeline and one (test) from a testing pipeline
  * threshold perfect: perfect match
  * threshold acceptable: Num. missing Loci + different called alleles <= 10
  * threshold warning: different called alleles <= 4
  * threshold fail: Num. missing Loci + different called alleles > 10
