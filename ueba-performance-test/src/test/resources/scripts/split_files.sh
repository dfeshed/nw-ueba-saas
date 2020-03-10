#!/bin/bash

DEFAULT_PATH="/var/netwitness/presidio/perf_data"
DEFAULT_SIZE=447000000

SPLIT_SIZE=${1:-${DEFAULT_SIZE}}
GENERATED_PATH="${2:-${DEFAULT_PATH}}/generated"

echo "*****************************   SPLIT FILES Started  *****************************"
[ -d $GENERATED_PATH ] || mkdir -p $GENERATED_PATH
FILES_DIR="${GENERATED_PATH}/*/*"

for FILE in ${FILES_DIR}; do
     FILESIZE=$(wc -c <"$FILE")

     if [[ $FILESIZE -gt $SPLIT_SIZE ]] ;then
        echo "$(date +%F_%T:%S) Processing file:  $FILE"
        split -b ${SPLIT_SIZE} $FILE $FILE
        rm -f $FILE
     else
        echo "$(date +%F_%T:%S) Insufficient size, skipped:  $FILE; size=$FILESIZE"
     fi
done

echo "*****************************   SPLIT FILES Finished  *****************************"
