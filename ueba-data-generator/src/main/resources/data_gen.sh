#!/usr/bin/env bash
set -e

exec 3>&1

scenario=$(dialog --backtitle "Welcome to UEBA data generator" \
       --checklist "Select scenaios to generate:" 20 50 9 \
        1 "TLS" on \
        2 "AUTHENTICATION" on \
        3 "PROCESS" on>&1 2>&1 1>&3);

start_time=$(dialog  --backtitle "Welcome to UEBA data generator" \
   --inputbox "Event start time" 8 40 2>&1 1>&3)

calendar=$(dialog --calendar "Events start time:" 8 40 2>&1 1>&3);

exec 3>&-

echo "$scenario"
echo "$start_time"
echo "$calendar"