#!/usr/bin/env bash
set -e

echo "installing presidio_workflows in virtualenv"
source /etc/sysconfig/airflow
source $AIRFLOW_VENV/bin/activate
cd /var/lib/netwitness/presidio/pypackages
OWB_ALLOW_NON_FIPS=on python -m easy_install presidio_workflows*.egg
AIRFLOW_HOME=$AIRFLOW_HOME OWB_ALLOW_NON_FIPS=on python -c "from presidio.charts import deploy_charts; deploy_charts.deploy_charts()"

deactivate

