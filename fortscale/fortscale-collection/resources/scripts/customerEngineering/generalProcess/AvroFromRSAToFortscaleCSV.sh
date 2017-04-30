#!/bin/bash
#copy and convert Avro files received from RSA SA to input directory

function log {
        echo "====== `date -u` $1" >> $logPath
}

function productLog {
        echo "`date "+%Y-%m-%d %H:%M:%S" -u` $1" >> $productLogPath
}

productLogPath=/home/cloudera/target/logFile.log
logPath=/home/cloudera/target/AvroCopyScript.log
errorLogPath=/home/cloudera/target/ErrorAvroCopyScript.log
input_dir=/hadoop/input/flume_input_notSorted
session_dir=/hadoop/input/rsasoc/v1/sessions/



month="$1"
year="$2"
startFromDay="$3"


if [ -z "$month" ] && [ -z "$year" ]
then
         rsa_dir=/hadoop/input/rsasoc/v1/logs/data

else
        rsa_dir=/hadoop/input/rsasoc/v1/logs/data"/$year/$month"

        #delete all the prev days directory till the startFromDay day
        if [ ! -z "$startFromDay" ]
        then
                for ((i=1; i<$startFromDay; i++)); do
                        log "Going to delete $rsa_dir/$i"
                        rm -rf $rsa_dir/$i
                done

        fi
fi

log "Starting to process new hour"

counter=0

IFS=$'\n'
for file in `find $rsa_dir -name "*.avro" -printf "%T+\t%p\n" | sort -t "/" -nk8,8 -nk9,9 -nk10,10 -nk11,11 -nk1,1`
do
       counter=$[$counter +1]
       log "#########"
       log "processing $file"

        filename=$(echo $file | awk -F"/" '{print $1 "." $8$9$10$11}' | awk -F"+" '{print $2}'|tr -d : | awk -F"." '{print "RSA_"$3$1".csv"}')
        file_location=$(echo $file | awk -F"\t" '{print $2}')
        #cp  $file_location $input_dir/$filename

        log "convert from avro to csv"
        avro cat --format csv $file_location > $input_dir/$filename 2> $errorLogPath
        log "avro convertion was done"

        chown cloudera:cloudera $input_dir/$filename 2> $errorLogPath

       log "$file was processed"
       log "Old file location: $file_location"

       log "new file path: $input_dir/$filename"
       rm -rf $file_location 2> $errorLogPath
       log "$file was deleted"

done

if [ "$counter" -eq "0" ]
then
        productLog "ERROR - No Events were recived from RSA in the last 15 min"
fi

log "Start to sort the files"
sh /home/cloudera/fortscale/utils/sortRsaFilesOnFlumeFolder.sh 2> $errorLogPath
log "Sort where done"

log "Start to clean the session folder"
rm -rf $session_dir 2> $errorLogPath
log "Session dir cleaning was daon"


log "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
