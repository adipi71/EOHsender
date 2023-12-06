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


# Usage example

This example has been tested on Linux with OpenJdk 11. It should work on any Java enabled Linux machine.

1. Download the last release (https://github.com/adipi71/EOHsender/releases/latest),  
2. Extract EOHsender.tar.gz
3. Launch on test data
   
```bash
# launch eoh-sender on the test data
sh eoh-sender.sh etc/EfsaOhWgs/comparealleles/exampleOfCompareInputEXTENDED.tsv

# launch eoh-compatibility on the test data
sh eoh-sender.sh etc/EfsaOhWgs/exampleOfManualInput.tsv
```

# Input files format

## eoh-sender: tsv input file specifications


<table>
<tbody>
<tr class="odd">
<td style="text-align: left;"><strong>MANUAL INPUT</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><strong><br />
</strong></td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong>TYPE</strong></td>
<td style="text-align: left;"><strong>FieldName</strong></td>
<td style="text-align: left;"><strong>Mandatory</strong></td>
<td style="text-align: left;"><strong>Description</strong></td>
<td style="text-align: left;"><strong>Example</strong></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>CODES</strong></td>
<td style="text-align: left;">localRawReadId</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">See EFSA Guidance</td>
<td style="text-align: left;">rawr1234</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">sampleLocalId</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">smpl1234</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">isolationLocalId</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">isolate1234</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>EPIDATA</strong></td>
<td style="text-align: left;">isolateSpecieCode</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">See EFSA Guidance.<br />
Mandatory if cfg:epidataSubmission=‘Y’</td>
<td style="text-align: left;">RF-00003072-PAR</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">samplingCountryId</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">IT</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">samplingYear</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">2023</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">samplingMatrixCode</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">A02QE</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">samplingMatrixFreeText</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">CHEESE</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>ANALYSIS</strong></td>
<td style="text-align: left;">libraryLayoutCode</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">See EFSA Guidance</td>
<td style="text-align: left;">2</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">AnalyticalPipelineInfoTag</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">XXX pipeline</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">MLSTSequenceType.Software</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">mlst 2.23.0</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>FILES</strong></td>
<td style="text-align: left;">file:R1</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Illumina R1 or Iontorrent fastq file.</td>
<td style="text-align: left;">rawr1234/fastq/rawr1234_R1_001.fastq.gz</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:R2</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">R2 fastq file.<br />
Mandatory if libraryLayoutCode=2</td>
<td style="text-align: left;">rawr1234/fastq/rawr1234_R2_001.fastq.gz</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:fqc</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Fastq QC result file (FastQC)</td>
<td style="text-align: left;">rawr1234/fastqc/rawr1234_SRC_raw.csv</td>
</tr>
<tr class="even">
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;">file:fastp</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;">rawr1234/fastqc/rawr1234_SRC_raw.csv</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:quast</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Assembly QC result file (Quast)</td>
<td style="text-align: left;">rawr1234/spades/rawr1234_quast.csv</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:checkm</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Assembly QC result file (Checkm)</td>
<td style="text-align: left;">rawr1234/spades/bin_stats.analyze.tsv</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:bowtie</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Assembly Coverage calculated through mapping with one of the best reference</td>
<td style="text-align: left;">rawr1234/bowtie/rawr1234_bowtie_import_coverage_full.csv</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:mlst</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;"><a href="https://github.com/tseemann/mlst">MLST result file (https://github.com/tseemann/mlst)</a></td>
<td style="text-align: left;">rawr1234/mlst/rawr1234_mlst.tsv</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong>SALMONELLA<br />
FILES</strong></td>
<td style="text-align: left;">file:sistr</td>
<td style="text-align: left;">Yes for Salmonella</td>
<td style="text-align: left;">Serotype.</td>
<td style="text-align: left;">rawr1234/sistr/sistr.tsv</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:seqsero</td>
<td style="text-align: left;">Yes for Salmonella</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">rawr1234/seqsero2/SeqSero_result.tsv</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><a href="file:///seq_typing">file:seq_typing</a></td>
<td style="text-align: left;">Yes for Salmonella</td>
<td style="text-align: left;">Serotype.</td>
<td style="text-align: left;">rawr1234/seq_typing/seq_typing.report.txt</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong>STEC<br />
FILES</strong></td>
<td style="text-align: left;"><a href="file:///stx">file:stx_typing</a></td>
<td style="text-align: left;">Yes for STEC</td>
<td style="text-align: left;">Stx subtyping.</td>
<td style="text-align: left;">rawr1234/seq_typing/seq_typing.report.txt</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:ecoh</td>
<td style="text-align: left;">Yes for STEC</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:ectyper</td>
<td style="text-align: left;">Yes for STEC</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">file:innuendo</td>
<td style="text-align: left;">Yes for STEC</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">dir:patho_typing</td>
<td style="text-align: left;">Yes for STEC</td>
<td style="text-align: left;"><a href="https://github.com/B-UMMI/patho_typing/">Pathotype directory of patho_typing (https://github.com/B-UMMI/patho_typing/) It is expected to find 3 files in the directory: patho_typing.report.txt, patho_typing.extended_report.txt, rematchModule_report.txt</a></td>
<td style="text-align: left;">rawr1234/patho_typing/</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong>ALLELES<br />
FILE</strong></td>
<td style="text-align: left;">file:alleles</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">Allelic profile result file (chewBBACA gt 2.8.5)<br />
Mandatory if cfg:crc32transform=‘Y’</td>
<td style="text-align: left;">rawr1234/chewbbaca/rawr1234_chewbbaca_results_alleles.tsv</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong>CONFIG/SETTING</strong></td>
<td style="text-align: left;">cfg:cgmlstschema</td>
<td style="text-align: left;">Conditional</td>
<td style="text-align: left;">Path of chewie-ns schema.<br />
Mandatory if cfg:crc32transform=‘Y’</td>
<td style="text-align: left;">/path/to/chewie-ns/schema/</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">cfg:crc32transform</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">Put Y in case you need to transform allelic profiles in crc32 format</td>
<td style="text-align: left;">Y</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">cfg:epidataSubmission</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">Put Y in case you intend to submit the epidemiological data</td>
<td style="text-align: left;">Y</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">cfg:outputDir</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">results will be put here</td>
<td style="text-align: left;">/dir/output</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">cfg:inputDir</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">base dir for the files</td>
<td style="text-align: left;">/dir/input</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="even">
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
</tbody>
</table>

------------------------------------------------------------------------

## eoh-compatibility: tsv input file specifications

<table>
<tbody>
<tr class="odd">
<td style="text-align: left;"><strong>MANUAL INPUT</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><strong><br />
</strong></td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong>TYPE</strong></td>
<td style="text-align: left;"><strong>FieldName</strong></td>
<td style="text-align: left;"><strong>Mandatory</strong></td>
<td style="text-align: left;"><strong>Description</strong></td>
<td style="text-align: left;"><strong>Example</strong></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>FILES</strong></td>
<td style="text-align: left;">species</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">L.monocytogenes,Salmonella,STEC</td>
<td style="text-align: left;">L.monocytogenes</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">baseAllelicProfileFile</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Base file</td>
<td style="text-align: left;">smpl1234.tsv</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">testAllelicProfileFile</td>
<td style="text-align: left;">Yes</td>
<td style="text-align: left;">Test file</td>
<td style="text-align: left;">smpl1234-test.tsv</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>THRESHOLDS</strong></td>
<td style="text-align: left;">MAX_DIFF</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">Thresholds species dependent (See rules)</td>
<td style="text-align: left;">4</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">MAX_ALLELES_DIFF</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">7</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;">MIN_ALLELES_DIFF</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">“</td>
<td style="text-align: left;">10</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
<tr class="odd">
<td style="text-align: left;"><strong>CONFIG/SETTING</strong></td>
<td style="text-align: left;">cfg:outputDir</td>
<td style="text-align: left;">No</td>
<td style="text-align: left;">results will be put here</td>
<td style="text-align: left;">/dir/output</td>
</tr>
<tr class="even">
<td style="text-align: left;"><strong><br />
</strong></td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
</tbody>
</table>

------------------------------------------------------------------------

## eoh-sender: input example 

<table>
<tbody>
<tr class="odd">
<td style="text-align: left;">localRawReadId</td>
<td style="text-align: left;">sampleLocalId</td>
<td style="text-align: left;">isolationLocalId</td>
<td style="text-align: left;">isolateSpecieCode</td>
<td style="text-align: left;">samplingCountryId</td>
<td style="text-align: left;">samplingYear</td>
<td style="text-align: left;">samplingMatrixCode</td>
<td style="text-align: left;">samplingMatrixFreeText</td>
<td style="text-align: left;">libraryLayoutCode</td>
<td style="text-align: left;">AnalyticalPipelineInfoTag</td>
<td style="text-align: left;">MLSTSequenceType.Software</td>
<td style="text-align: left;">file:R1</td>
<td style="text-align: left;">file:R2</td>
<td style="text-align: left;">file:fqc</td>
<td style="text-align: left;">file:quast</td>
<td style="text-align: left;">file:bowtie</td>
<td style="text-align: left;">file:mlst</td>
<td style="text-align: left;">file:alleles</td>
<td style="text-align: left;">cfg:cgmlstschema</td>
<td style="text-align: left;">cfg:crc32transform</td>
<td style="text-align: left;">cfg:epidataSubmission</td>
<td style="text-align: left;">cfg:outputDir</td>
<td style="text-align: left;">cfg:inputDir</td>
</tr>
<tr class="even">
<td style="text-align: left;">rawr1234</td>
<td style="text-align: left;">smpl1234</td>
<td style="text-align: left;">isolate1234</td>
<td style="text-align: left;">RF-00000251-MCG</td>
<td style="text-align: left;">IT</td>
<td style="text-align: left;">2019</td>
<td style="text-align: left;">A02QE</td>
<td style="text-align: left;">FORMAGGIO</td>
<td style="text-align: left;">2</td>
<td style="text-align: left;">NGSManager</td>
<td style="text-align: left;">mlst 2.23.0</td>
<td style="text-align: left;">rawr1234/fastq/rawr1234_R1_001.fastq.gz</td>
<td style="text-align: left;">rawr1234/fastq/rawr1234_R2_001.fastq.gz</td>
<td style="text-align: left;">rawr1234/fastq/rawr1234_SRC_raw.csv</td>
<td style="text-align: left;">rawr1234/spades/rawr1234_quast.csv</td>
<td style="text-align: left;">rawr1234/bowtie/rawr1234_bowtie_import_coverage_full.csv</td>
<td style="text-align: left;">rawr1234/mlst/rawr1234_mlst.tsv</td>
<td style="text-align: left;">rawr1234/chewbbaca/rawr1234_chewbbaca_results_alleles.tsv</td>
<td style="text-align: left;">chewie_lm</td>
<td style="text-align: left;">Y</td>
<td style="text-align: left;">Y</td>
<td style="text-align: left;">/base/dir/output</td>
<td style="text-align: left;">/base/dir/input</td>
</tr>
<tr class="odd">
<td style="text-align: left;">rawr5678</td>
<td style="text-align: left;">smpl5678</td>
<td style="text-align: left;">isolate5678</td>
<td style="text-align: left;">RF-00003072-PAR</td>
<td style="text-align: left;">IT</td>
<td style="text-align: left;">2019</td>
<td style="text-align: left;">A02QE</td>
<td style="text-align: left;">FORMAGGIO</td>
<td style="text-align: left;">2</td>
<td style="text-align: left;">XXX</td>
<td style="text-align: left;">mlst 2.23.0</td>
<td style="text-align: left;">rawr5678/fastq/rawr5678_R1_001.fastq.gz</td>
<td style="text-align: left;">rawr5678/fastq/rawr5678_R2_001.fastq.gz</td>
<td style="text-align: left;">rawr5678/fastq/rawr5678_SRC_raw.csv</td>
<td style="text-align: left;">rawr5678/spades/rawr5678_quast.csv</td>
<td style="text-align: left;">rawr5678/bowtie/rawr5678_bowtie_import_coverage_full.csv</td>
<td style="text-align: left;">rawr5678/mlst/rawr5678_mlst.tsv</td>
<td style="text-align: left;">rawr5678/chewbbaca/rawr5678_chewbbaca_results_alleles.tsv</td>
<td style="text-align: left;">chewie_lm</td>
<td style="text-align: left;">Y</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;">/base/dir/output</td>
<td style="text-align: left;">/base/dir/input</td>
</tr>
<tr class="even">
<td style="text-align: left;">rawrXYZ</td>
<td style="text-align: left;">smplXYZ</td>
<td style="text-align: left;">isolateXYZ</td>
<td style="text-align: left;">RF-00003072-PAR</td>
<td style="text-align: left;">IT</td>
<td style="text-align: left;">2019</td>
<td style="text-align: left;">A02QE</td>
<td style="text-align: left;">FORMAGGIO</td>
<td style="text-align: left;">2</td>
<td style="text-align: left;">YYYY</td>
<td style="text-align: left;">mlst 2.23.0</td>
<td style="text-align: left;">rawrXYZ/fastq/rawrXYZ_R1_001.fastq.gz</td>
<td style="text-align: left;">rawrXYZ/fastq/rawrXYZ_R2_001.fastq.gz</td>
<td style="text-align: left;">rawrXYZ/fastq/rawrXYZ_SRC_raw.csv</td>
<td style="text-align: left;">rawrXYZ/spades/rawrXYZ_quast.csv</td>
<td style="text-align: left;">rawrXYZ/bowtie/rawrXYZ_bowtie_import_coverage_full.csv</td>
<td style="text-align: left;">rawrXYZ/mlst/rawrXYZ_mlst.tsv</td>
<td style="text-align: left;">rawrXYZ/chewbbaca/rawrXYZ_chewbbaca_results_alleles.tsv</td>
<td style="text-align: left;">chewie_lm</td>
<td style="text-align: left;">Y</td>
<td style="text-align: left;">Y</td>
<td style="text-align: left;">/base/dir/output</td>
<td style="text-align: left;">/base/dir/input</td>
</tr>
</tbody>
</table>

------------------------------------------------------------------------

## eoh-compatibility: input example 

<table>
<tbody>
<tr class="odd">
<td style="text-align: left;">species</td>
<td style="text-align: left;">baseAllelicProfileFile</td>
<td style="text-align: left;">testAllelicProfileFile</td>
<td style="text-align: left;">cfg:outputDir</td>
<td style="text-align: left;">MAX_DIFF</td>
<td style="text-align: left;">MAX_ALLELES_DIFF</td>
<td style="text-align: left;">MIN_ALLELES_DIFF</td>
</tr>
<tr class="even">
<td style="text-align: left;">STEC</td>
<td style="text-align: left;">/stec/base.tsv</td>
<td style="text-align: left;">/stec/test.tsv</td>
<td style="text-align: left;">/tmp/</td>
<td style="text-align: left;">15</td>
<td style="text-align: left;">10</td>
<td style="text-align: left;">5</td>
</tr>
<tr class="odd">
<td style="text-align: left;">L.monocytogenes</td>
<td style="text-align: left;">base.tsv</td>
<td style="text-align: left;">test.tsv</td>
<td style="text-align: left;">/tmp/</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
<td style="text-align: left;"><br />
</td>
</tr>
</tbody>
</table>
