#!/bin/bash

# this script should be run from the collection target directory using: ./resources/scripts/
# should be given: job name, job group name, day to start and day to end (inclusive, relative to today)

if [ $# -lt 1 ]; then
	echo "usage: $0 jobName groupName startDay endDay"
	echo "NOTE: this script should be running from the collection target directory (where fortscale collection jar is located)"
	exit 1
fi

jobName=$1
groupName=$2
startDay=$3
endDay=$4

for ((day=${startDay}; day<=${endDay}; day++))
do
   for ((hour=0; hour<24; hour++))
   do
      endHour=$(($hour + 1))
      java -Dlogback.configurationFile=resource/logback-console.xml -jar fortscale-collection*.jar $1 $2 earliest=-${day}d@d+${hour}h latest=-${day}d@d+${endHour}h
   done
done
