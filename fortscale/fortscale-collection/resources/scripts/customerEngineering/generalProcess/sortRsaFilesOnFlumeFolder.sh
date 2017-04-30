#!/bin/bash
#copy and convert Avro files received from RSA SA to input directory

function log {
   echo "====== `date -u` $1" >> $logPath
}

logPath=/home/cloudera/target/SortRSAFilesOnFlumeDir.log
errorLogPath=/home/cloudera/target/ErrorSortRSAFilesOnFlumeDir.log
input_dir=/hadoop/input/flume_input_notSorted
output_dir=/hadoop/input/flume_input/

log "Start to sort files in $input_dir"

cd $input_dir

shopt -s nullglob
array=(RSA*.csv)

sortedFileList=($(echo ${array[@]} | tr " " "\n"| sort -V ))

for file in "${sortedFileList[@]}"
do
        log "Going to touch $file"

        touch $file 2> $errorLogPath
        mv $file $output_dir 2> $errorLogPath
done

log "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
