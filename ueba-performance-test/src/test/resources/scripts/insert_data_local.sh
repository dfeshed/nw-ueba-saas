#!/bin/bash

DEFAULT_PATH=/var/netwitness/presidio/workspace/presidio-performance-network-gen/presidio-integration-performance-test/target/netwitness_events_gen/*/*
DONE_PATH=/var/netwitness/presidio/workspace/presidio-performance-network-gen/presidio-integration-performance-test/target/done/

echo >>  ./insert_data.log
echo >>  ./insert_data.log
echo "*****************************   Started  ************************************************" >> ./insert_data.log
echo "path=$DEFAULT_PATH" >> ./insert_data.log
echo >> ./insert_data.log
echo >> ./insert_data.log

for FILE in $DEFAULT_PATH; do
     echo "$(date +%F_%T:%S) Processing:  $FILE" >> ./insert_data.log
     RESULT=$(NwLogPlayer -f $FILE &)
     echo ${RESULT} >> ./insert_data.log

     if [[ ${RESULT} == *"LogPlayer finished sending"* ]]
     then
        mv $FILE $DONE_PATH & >> ./insert_data.log
        echo "success"  >> ./insert_data.log
     else
       echo  >> ./insert_data.log
       echo "$(date +%F_%T:%S) ERROR - failed processing $FILE" >> ./insert_data.log
       echo  >> ./insert_data.log
     fi

     sleep 180
done