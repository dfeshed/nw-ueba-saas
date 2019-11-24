#!/bin/bash

PROJECT_PATH=/var/netwitness/presidio/workspace/presidio-performance-network-gen/ueba-automation-projects/ueba-performance-test
DEFAULT_PATH="/var/netwitness/presidio/perf_data/generated/*/*"
DONE_PATH="/var/netwitness/presidio/perf_data/inserted"

echo "*****************************   UPLOAD TO BROKER Started  *****************************"

ls $DEFAULT_PATH > /dev/null
RESULT=$?

if [[ $RESULT -ne 0 ]]
then
  echo "No data files found in $DEFAULT_PATH"
  exit 1
fi

mkdir -p $DONE_PATH

for FILE in $DEFAULT_PATH; do
     echo "$(date +%F_%T:%S) Processing:  $FILE"
     RESULT=$(NwLogPlayer -f $FILE &)

     if [[ ${RESULT} == *"LogPlayer finished sending"* ]]
     then
        mv $FILE $DONE_PATH
        echo "success"
        sleep 150
     else
       echo
       echo "$(date +%F_%T:%S) ERROR - failed processing $FILE"
       echo
       sleep 30
     fi
done

echo "*****************************   UPLOAD TO BROKER Finished  *****************************"
