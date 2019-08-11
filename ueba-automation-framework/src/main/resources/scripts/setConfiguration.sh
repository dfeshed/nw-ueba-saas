#!/bin/bash
set -e

#Set Analytics Testing Configuration
if [ ! -f /etc/netwitness/presidio/configserver/configurations/airflow/workflows-default.json ]; then
    echo "ERROR: workflows-default.json file not found. Exiting."
    exit 1;
else
   sed -i 's!"min_time_to_start_retention_in_days.*!"min_time_to_start_retention_in_days": 150,!g' /etc/netwitness/presidio/configserver/configurations/airflow/workflows-default.json
fi

sed -i 's!number-of-partitions-to-influence-enough.*!number-of-partitions-to-influence-enough": 1,!g' /var/lib/netwitness/presidio/asl/scorers/enriched-records/*
sed -i 's!number-of-partitions-to-influence-enough.*!number-of-partitions-to-influence-enough": 1,!g' /var/lib/netwitness/presidio/asl/scorers/feature-aggregation-records/*
sed -i 's!number-of-partitions-to-influence-enough.*!number-of-partitions-to-influence-enough": 1,!g' /var/lib/netwitness/presidio/asl/scorers/smart-records/*

sed -i 's!"type": "smart_values_prior_model_builder"*!"type": "smart_values_prior_model_builder","quantile":0.6!g' /var/lib/netwitness/presidio/asl/models/smart-records/userId_hourly.json

# output smart.threshold.score
if [ -f "/etc/netwitness/presidio/configserver/configurations/output-processor.properties" ];
then
	if grep -q 'smart.threshold.score' /etc/netwitness/presidio/configserver/configurations/output-processor.properties
	then
		sed -i 's!smart.threshold.score.*!smart.threshold.score=30!g' /etc/netwitness/presidio/configserver/configurations/output-processor.properties
	else
		echo "smart.threshold.score=30" >> /etc/netwitness/presidio/configserver/configurations/output-processor.properties
	fi
else
    echo "smart.threshold.score=30" >> /etc/netwitness/presidio/configserver/configurations/output-processor.properties
fi

# disable ADE retention
if [ $( grep -q 'presidio.execute.ttl.cleanup' /etc/netwitness/presidio/configserver/configurations/application.properties ) ];
then
    sed -i 's!presidio.execute.ttl.cleanup=*!presidio.execute.ttl.cleanup=false!g' /etc/netwitness/presidio/configserver/configurations/application.properties
else
    echo "" >>  /etc/netwitness/presidio/configserver/configurations/application.properties
    echo "presidio.execute.ttl.cleanup=false" >>  /etc/netwitness/presidio/configserver/configurations/application.properties
fi
sed -i 's!"sourceKey": "event_time"!"sourceKey": "time"!g' /var/netwitness/presidio/flume/conf/adapter/transformers/tls.json
sudo systemctl start airflow-scheduler
sudo systemctl start airflow-webserver
