import json
import requests
from datetime import datetime, timedelta

# Do not clean the Elasticsearch data:
# "Reset Presidio" should clean all the data, EXCEPT for the Alerts that were already created.
clean_data = False
# Reset the start time to 27 days before the current system time, at the beginning of the day:
# Since there shouldn't be duplicate Alerts, every logical hour that was already processed should be skipped, including
# hours of the current system day. Presidio starts triggering Alerts 28 logical days after the start time. So if the
# start time is reset to 27 days before the current system time, the first Alerts Presidio triggers will be of the
# following day (i.e. of tomorrow). This means that there will be a maximum of 24 hours without Alerts (those of the
# current system day), which is acceptable by Product.
start_time = datetime.utcnow() - timedelta(days=27)
start_time = start_time.strftime("%Y-%m-%dT00:00:00Z")

data = {"conf": {
    "elasticsearch": {"cleanData": clean_data},
    "presidio": {"dataPipeline": {"startTime": start_time}}
}}

response = requests.post("http://localhost:8100/api/experimental/dags/reset_presidio/dag_runs",
                         json.dumps(data),
                         headers={"cache-control": "no-cache", "content-type": "application/json"})

if response.status_code != 201:
    raise ValueError("Post request to run the 'Reset Presidio' DAG failed.")
