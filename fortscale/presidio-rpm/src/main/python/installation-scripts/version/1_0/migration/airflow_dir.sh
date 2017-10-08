#!/bin/bash
mkdir /var/run/airflow
chown presidio:presidio /var/run/airflow
systemctl restart airflow-webserver