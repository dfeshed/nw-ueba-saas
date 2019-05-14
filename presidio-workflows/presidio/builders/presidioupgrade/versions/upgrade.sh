#!/usr/bin/env bash

echo "pause full_flow_2019-03-01_00_00_00"
airflow pause full_flow_2019-03-01_00_00_00
echo "upgradedb"
airflow upgradedb
echo "install"
python -m pip install -U --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") -r $AIRFLOW_PKG_REQ