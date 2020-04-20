#!/usr/bin/env bash
set -e


echo "installing presidio_workflows in virtualenv"
source /etc/sysconfig/airflow
export AIRFLOW_VENV
source $AIRFLOW_VENV/bin/activate
cd /var/lib/netwitness/presidio/pypackages-ext && pwd && OWB_ALLOW_NON_FIPS=on python -m pip uninstall -y presidio-workflows-extension && OWB_ALLOW_NON_FIPS=on python -m easy_install presidio_workflows_extension*.egg
deactivate
