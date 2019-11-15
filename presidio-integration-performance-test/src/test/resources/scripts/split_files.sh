#!/bin/bash

PROJECT_PATH=/var/netwitness/presidio/workspace/presidio-performance-network-gen/ueba-automation-projects/presidio-integration-performance-test
DEFAULT_PATH=${PROJECT_PATH}/target/netwitness_events_gen/*/*

echo "*****************************   SPLIT FILES Started  *****************************"

for FILE in $DEFAULT_PATH; do
     FILESIZE=$(wc -c <"$FILE")

     if [[ $FILESIZE -gt 447000000 ]] ;then
        echo "$(date +%F_%T:%S) Processing file:  $FILE"
        split -b 450M $FILE $FILE
        rm -f $FILE
     else
        echo "$(date +%F_%T:%S) Skipped file:  $FILE; size=$FILESIZE"
     fi
done

echo "*****************************   SPLIT FILES Finished  *****************************"
