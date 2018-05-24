# Presidio Netwitness Package

## Installation
## Installation

```sh
systemctl stop airflow-webserver
systemctl stop airflow-scheduler

yum -y install rsa-nw-presidio-ext-netwitness

source /etc/sysconfig/airflow
python -m virtualenv $AIRFLOW_VENV
source $AIRFLOW_VENV/bin/activate
pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") -r $AIRFLOW_PKG_REQ
deactivate

```