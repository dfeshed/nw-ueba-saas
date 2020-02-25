#!/usr/bin/env bash
set -e
exec 3>&1

schemasSelector=$(dialog --backtitle "Welcome to UEBA data generator" \
       --checklist "Select events data types to generate:" 20 50 9 \
        "TLS" "" on \
        "AUTHENTICATION" "" on \
        "ACTIVE_DIRECTORY" "" on \
        "FILE" "" on \
        "PROCESS" "" on \
        "REGISTRY" "" on>&1 2>&1 1>&3);

commaDelimited=${schemasSelector//" "/","}
schemaPARAM="schemas=${commaDelimited}"


start_time=$(dialog  --backtitle "Welcome to UEBA data generator" \
   --inputbox "Events start time (days from today): " 8 60 2>&1 1>&3)

startTimePARAM="historical_days_back=${start_time}"


destinationSelector=$(dialog --backtitle "Welcome to UEBA data generator" \
       --radiolist "Select events destination:" 20 90 4 \
        "CEF_DAILY_FILE" "CEF files creation without sending" on \
        "CEF_DAILY_BROKER" "Creation and sending CEF files to Broker" on \
        "MONGO_ADAPTER" "Events injection through internal UEBA Mongo collections" on>&1 2>&1 1>&3);

destinationPARAM="generatorFormat=${destinationSelector}"

CMD="java -jar ./ueba-data-generator.jar ${schemaPARAM} ${startTimePARAM} ${destinationPARAM}"
exec 3>&-
LOG=/tmp/data_gen_out.log
echo "Execution command = ${CMD}" > $LOG
$CMD &>> $LOG &

exit_status=1

while [ $exit_status -ne 0 ]
  do
    dialog \
      --ok-label Abort \
      --tailbox "$LOG" 60 150

    exit_status=$?

    if [ $exit_status -eq 255 ]; then
      dialog --textbox "$LOG" 21 80
    fi
  done

kill -9 %%