# Presidio Netwitness Package

## Installation

```sh
systemctl stop airflow-webserver
systemctl stop airflow-scheduler

yum -y install rsa-nw-presidio-ext-netwitness

source /etc/sysconfig/airflow
python -m virtualenv $AIRFLOW_VENV
source $AIRFLOW_VENV/bin/activate
cd /var/lib/netwitness/presidio/ext-pypackages
pip uninstall presidio-workflows-extension
easy_install presidio_workflows_extension*.egg
deactivate
systemctl start airflow-webserver
systemctl start scheduler

```