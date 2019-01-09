import json
import requests
import urllib
from datetime import datetime, timedelta

# Set the execution date of the "Reset Presidio" DAG run to the current system time.
exec_date = datetime.utcnow()
# Do not clean the Elasticsearch data:
# "Reset Presidio" should clean all the data, EXCEPT for the Alerts that were already created.
clean_data = False
# Reset the start time to 27 days before the current system time, at the beginning of the day:
# Since there shouldn't be duplicate Alerts, every logical hour that was already processed should be skipped, including
# hours of the current system day. Presidio starts triggering Alerts 28 logical days after the start time. So if the
# start time is reset to 27 days before the current system time, the first Alerts Presidio triggers will be of the
# following day (i.e. of tomorrow). This means that there will be a maximum of 24 hours without Alerts (those of the
# current system day), which is acceptable by Product.
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
