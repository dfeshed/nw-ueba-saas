#!/usr/bin/env bash
echo "stoping airflow services"
systemctl stop airflow-webserver
systemctl stop airflow-scheduler

echo "installing presidio_workflows in virtualenv"
source /etc/sysconfig/airflow
source $AIRFLOW_VENV/bin/activate
cd /var/lib/netwitness/presidio/pypackages
easy_install presidio_workflows*.egg
deactivate

echo "starting airflow services"
systemctl start airflow-webserver
systemctl start airflow-scheduler