#!/bin/bash

DEFAULT_PATH="/var/netwitness/presidio/perf_data"
DEFAULT_DELAY_SEC=180

DELAY_BETWEEN_FILES_INSERT_SEC="${1:-${DEFAULT_DELAY_SEC}}/generated/*/*"
GENERATED_PATH="${2:-${DEFAULT_PATH}}/generated/*/*"
DONE_PATH="${2:-${DEFAULT_PATH}}/inserted/*/*"

echo "*****************************   UPLOAD TO BROKER Started  *****************************"
mkdir -p $GENERATED_PATH
mkdir -p $DONE_PATH

for FILE in $GENERATED_PATH; do
     echo "$(date +%F_%T:%S) Processing: $FILE"
     RESULT=$(NwLogPlayer -f $FILE &)

     if [[ ${RESULT} == *"LogPlayer finished sending"* ]]
     then
        mv $FILE $DONE_PATH
        echo ${RESULT}
        sleep $DELAY_BETWEEN_FILES_INSERT_SEC
     else
       echo
       echo "$(date +%F_%T:%S) ERROR - failed processing $FILE"
       echo ${RESULT}
       sleep 10
     fi
done

echo "*****************************   UPLOAD TO BROKER Finished  *****************************"
