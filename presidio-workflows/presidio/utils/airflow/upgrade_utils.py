import json
import requests
import time
import urllib
import logging
import sys
from datetime import datetime, timedelta

from airflow.models import DagModel
from airflow.utils.db import provide_session


def run_reset_presidio_for_upgrade():
    # Set the execution date of the "Reset Presidio" DAG run to the current system time.
    exec_date = datetime.utcnow()
    # Do not clean the Elasticsearch data:
    # "Reset Presidio" should clean all the data, EXCEPT for the Alerts that were already created.
    clean_data = False
    # Reset the start time to 27 days before the current system time, at the beginning of the day:
    # Since there shouldn't be duplicate Alerts, every logical hour that was already processed should be skipped,
    # including hours of the current system day. Presidio starts triggering Alerts 28 logical days after the start time.
    # So if the start time is reset to 27 days before the current system time, the first Alerts Presidio triggers will
    # be of the following day (i.e. of tomorrow). This means that there will be a maximum of 24 hours without Alerts
    # (those of the current system day), which is acceptable by Product.
    start_time = exec_date - timedelta(days=27)

    exec_date = exec_date.strftime("%Y-%m-%dT%H:%M:%S")
    start_time = start_time.strftime("%Y-%m-%dT00:00:00Z")

    conf = {
        "elasticsearch": {"cleanData": clean_data},
        "presidio": {"dataPipeline": {"startTime": start_time}}
    }

    query = {
        "api": "trigger_dag",
        "dag_id": "reset_presidio",
        "exec_date": exec_date,
        "conf": json.dumps(conf, separators=(",", ":"))
    }

    response = requests.get("http://localhost:8100/admin/rest_api/api?%s" % urllib.urlencode(query))

    if response.status_code != 200:
        raise ValueError("Get request to trigger a 'Reset Presidio' DAG run failed.")

    # Wait for the "Reset Presidio" DAG run to finish.
    url = "http://localhost:8100/admin/rest_api/api?%s" % urllib.urlencode({
        "api": "dag_state",
        "dag_id": "reset_presidio",
        "execution_date": exec_date
    })

    while True:
        time.sleep(60)
        response = requests.get(url)
        state = response.json()["output"]["stdout"].strip()

        if state == "success":
            break
        elif state == "failed":
            raise ValueError("The triggered 'Reset Presidio' DAG run failed.")
        elif state != "running":
            raise ValueError("The triggered 'Reset Presidio' DAG run is in an unknown state (%s)." % state)


def get_dags_by_prefix(dag_id_prefix):
    """
    :return: list of DAG id's by prefix given
    :rtype: List[DAG]
    """
    dag_models = find_dag_models()

    dag_models_by_prefix = [x for x in dag_models if x.dag_id.startswith(dag_id_prefix)]

    return dag_models_by_prefix


@provide_session
def find_dag_models(session=None):
    DM = DagModel
    qry = session.query(DM)
    try:
        return qry.all()
    except Exception as e:
        logging.error("got error while executing {} query".format(qry))
        raise ValueError(e)
