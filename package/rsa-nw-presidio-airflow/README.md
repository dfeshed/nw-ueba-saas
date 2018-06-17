# Presidio airflow 

An rpm wrap to [apache-airflow](https://airflow.apache.org/) python package

### Environment variable
see [service env file](/package/rsa-nw-presidio-airflow/scripts/systemd/airflow)


### Installation

```sh
# install airflow rpm
yum -y install rsa-nw-presidio-airflow

# Install airflow in a virtualenv
source /etc/sysconfig/airflow
OWB_ALLOW_NON_FIPS=on python -m pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ")/../virtualenv virtualenv==15.2.0
OWB_ALLOW_NON_FIPS=on python -m virtualenv $AIRFLOW_VENV
source $AIRFLOW_VENV/bin/activate
OWB_ALLOW_NON_FIPS=on python -m pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") numpy
OWB_ALLOW_NON_FIPS=on python -m pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") -r $AIRFLOW_PKG_REQ
deactivate

# Start airflow systemd service
systemctl daemon-reload
systemctl enable airflow-webserver
systemctl start airflow-webserver
systemctl enable airflow-scheduler
systemctl start airflow-scheduler
```

