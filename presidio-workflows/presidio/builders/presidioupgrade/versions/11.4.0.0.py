import datetime
import logging
import os
import subprocess


# source /etc/sysconfig/airflow
# source $AIRFLOW_VENV/bin/activate
# OWB_ALLOW_NON_FIPS=on SLUGIFY_USES_TEXT_UNIDECODE=yes python -m pip install -U --no-index --find-links=$(dirname "$AIRFLOW_PKG_REQ") -r $AIRFLOW_PKG_REQ
# OWB_ALLOW_NON_FIPS=on airflow upgradedb
# deactivate
import requests

ENV_VARIABLES_PATH = "/etc/sysconfig/airflow"

subprocess.call(["bash", "/var/netwitness/presidio/airflow/venv/lib/python2.7/site-packages/presidio_workflows-1.0-py2.7.egg/presidio/builders/presidioupgrade/versions/upgrade.sh"])

#
# logging.error("running upgradedb:")
# subprocess.call(["/var/lib/netwitness/presidio/airflow/venv/bin/airflow", "upgradedb"])
#
#
#
# # from os import environ
# #
# # data = subprocess.call([".", "/etc/sysconfig/airflow", ";", "env"])
# # env = dict((line.split("=", 1) for line in data.splitlines()))
# # environ.update(env)
#
#
#
# subprocess.call(["echo", "$AIRFLOW_PKG_REQ"])
# logging.error("running install -u:")
# subprocess.call(["python", "-m", "pip", "install", "-U", "--no-index", "--find-links=$(\"/var/lib/netwitness/presidio/pypackages/airflow/\")", "-r", "/var/lib/netwitness/presidio/pypackages/airflow/requirements.txt"])
#
#
#















#
# # exec_date = datetime.utcnow()
#
# # Wait for the "Reset Presidio" DAG run to finish.
# url = "http://localhost:8100/admin/rest_api/api?%s" % urllib.urlencode({
#     "api": "pause",
#     "dag_id": "full_flow_2019-03-01_00_00_00"
# })
#
# while True:
#     # time.sleep(60)
#     response = requests.get(url)
#     # logging.error(url)
#     state = response.json()["output"]["stdout"].strip()
#     # stderr = response.json()["output"]["stderr"].strip()
#
#     # raise ValueError(stderr)
#     if state == "paused":
#         logging.error("The state is (%s)." % state)
#         break
#     elif state == "failed":
#         raise ValueError("failed.")
#     elif state != "running":
#         raise ValueError("DAG run is in an unknown state (%s)." % state)
