# RIF to RDA Bridge
Utility project for convert rif files into RDA ndjson files to be used with the bfd-pipeline-rda-grpc-server to provide
synthetic RDA data for FISS and MCS claims.


## Prerequisite
Before the rif-to-rda bridge can be executed, you need RIF files to read from.  These RIF file can have any origin, but
the expected approach is to generate them from the Synthea project (using the bfd specific branch) to first create the
required RIF files and then use the root directory of those files as the basis for the bridge execution.

The bridge project will generate FISS claims from the Inpatient, Outpatient, HHA, Hospice, and SNF RIF files generated
by Synthea, as well as additional MCS claims from the Carrier RIF file.

Expected RIF file names...

FISS:
- inpatient.csv
- outpatient.csv
- home.csv
- hospice.csv
- snf.csv

MCS:
- carrier.csv

## Run
Execute the command
```shell
mvn clean install exec:java <Path to Rif Dir>
```
or execute the shell script
```shell
./run_bridge <Path to Rif Dir>
```

## Sample Output

```
Written 43214 inpatient claims
Written 391498 outpatient claims
Written 54940 home claims
Written 140635 hospice claims
Written 3784 snf claims
Written 326592 carrier claims
```