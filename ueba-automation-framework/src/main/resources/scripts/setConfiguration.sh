#!/bin/bash
set -e
sleep 15
#Sleep in order to wait until the workflows-default.json will be created

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

# Set smart.threshold.score
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

# Disable ADE retention
if [ $( grep -q 'presidio.execute.ttl.cleanup' /etc/netwitness/presidio/configserver/configurations/application.properties ) ];
then
    sed -i 's!presidio.execute.ttl.cleanup=*!presidio.execute.ttl.cleanup=false!g' /etc/netwitness/presidio/configserver/configurations/application.properties
else
    echo "" >>  /etc/netwitness/presidio/configserver/configurations/application.properties
    echo "presidio.execute.ttl.cleanup=false" >>  /etc/netwitness/presidio/configserver/configurations/application.properties
fi

# Disable exporting metrics
if grep -q 'enable.metrics.export' /etc/netwitness/presidio/configserver/configurations/application.properties
	then
		sed -i 's!enable.metrics.export.*!enable.metrics.export=false!g' /etc/netwitness/presidio/configserver/configurations/application.properties
	else
		echo "senable.metrics.export=false" >> /etc/netwitness/presidio/configserver/configurations/application.properties
fi

sudo systemctl start airflow-webserver
