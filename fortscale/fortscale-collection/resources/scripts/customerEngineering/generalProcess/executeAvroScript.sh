#!/bin/bash

function log {
        echo "====== `date -u` $1" >> $logPath
}


logPath=/home/cloudera/target/AvroCopyScript.log

#Validaet that there is no avro.sh process that was executed
# if there is stop and wait till the next trigger
ps -elf | grep AvroFromRSAToFortscaleCSV.[sh] > /dev/null
if [ $? -eq 0 ]
then
        log "WARN  AvroFromRSAToFortscaleCSV.sh.sh already executed will pass for now and will try next time"
        exit
else
        sh /home/cloudera/fortscale/utils/AvroFromRSAToFortscaleCSV.sh
        #echo "execute avro.sh"
fi
