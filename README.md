# Recommender System

## How to run on Unix
Note: I have no idea how Windows works so you will have to try on your own.
### System Prerequistes
- Java 1.8 (lambda, concurrency)
### Run Task to generate outputs
`./gradlew clean run`

## Run Outputs
- `output.log` is the stdout output containing some of the aggregated metrics
- `output_csv.tgz` is the tar gz archive for all evaluation metrics as csv.

## Document Listing

Extract `output_csv.tgz`, it should contain following evaluation metrics in csv
format:

- baseline.csv contains per prediction metrics for baseline
- msd[10,300].csv contains msd runs on threshold from 10 to 300
- resnick[10,300].csv contains msd+resnick runs on threshold from 10 to 300
- stats_movie.csv contains per movie statistics for original dataset
- stats_user.csv contains statistics per user for original dataset

Under `src/main/R/` there is melted (reshape) data.frame from the above
mentioned csv files, in R dataset format.

Under `src/main/R/` there is source document for report and a copy of the
report in PDF form.

For convenience a copy of the PDF was being placed under the project root
folder.
