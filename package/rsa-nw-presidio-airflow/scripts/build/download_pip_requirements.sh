#!/usr/bin/env bash
mkdir -p $1/airflow
mkdir -p $1/virtualenv
pip download virtualenv==15.2.0 -d $1/virtualenv
pip download -r $2 -d $1/airflow
