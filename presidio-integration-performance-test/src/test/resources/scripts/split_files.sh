#!/bin/bash

DEFAULT_PATH=/var/netwitness/presidio/workspace/presidio-performance-network-gen/presidio-integration-performance-test/target/netwitness_events_gen/*/*

for FILE in $DEFAULT_PATH; do
     FILESIZE=$(wc -c <"$FILE")

     if [[ $FILESIZE -gt 446905995 ]] ;then
        echo "$(date +%F_%T:%S) Processing file:  $FILE"
        split -b 450M $FILE $FILE
        rm -f $FILE
     else
        echo "$(date +%F_%T:%S) Skipped file:  $FILE; size=$FILESIZE"
     fi
done