#!/bin/bash

PROJECT_PATH=/var/netwitness/presidio/workspace/presidio-performance-network-gen/ueba-automation-projects/presidio-integration-performance-test
DEFAULT_PATH=${PROJECT_PATH}/target/netwitness_events_gen/*/*
DONE_PATH=${PROJECT_PATH}/target/netwitness_events_uploaded/

echo "*****************************   UPLOAD TO BROKER Started  *****************************"
mkdir -p $DONE_PATH

for FILE in $DEFAULT_PATH; do
     echo "$(date +%F_%T:%S) Processing:  $FILE"
     RESULT=$(NwLogPlayer -f $FILE &)

     if [[ ${RESULT} == *"LogPlayer finished sending"* ]]
     then
        mv $FILE $DONE_PATH
        echo "success"
     else
       echo
       echo "$(date +%F_%T:%S) ERROR - failed processing $FILE"
       echo
     fi

     sleep 150
done

echo "*****************************   UPLOAD TO BROKER Finished  *****************************"
