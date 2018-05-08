# Presidio airflow 

An rpm wrap to apache-airflow python package



### Installation

```sh
$ #!/usr/bin/env bash
$  source /etc/sysconfig/airflow
$  pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ")/../virtualenv virtualenv==15.2.0
$  python -m virtualenv $AIRFLOW_VENV
$  source $AIRFLOW_VENV/bin/activate
$  pip install --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") -r $AIRFLOW_PKG_REQ
$  deactivate
```

