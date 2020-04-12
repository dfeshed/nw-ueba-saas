import argparse
import json
import requests
import urllib
from datetime import datetime


def parse_date_argument(date):
    # First, call strptime to:
    #    1. Check that the date string is in the provided format.
    #    2. Convert the date string to a datetime instance.
    # Then, call strftime to return a string representation of the datetime instance and:
    #    1. Set the time to midnight (i.e. 00:00:00).
    #    2. Set the time zone to UTC (i.e. Z).
    return datetime.strptime(date, "%Y-%m-%d").strftime("%Y-%m-%dT00:00:00Z")


argument_parser = argparse.ArgumentParser()
help = "reconfigure the UEBA engine to work on these schemas (e.g. ACTIVE_DIRECTORY AUTHENTICATION FILE)"
argument_parser.add_argument("-s", "--schemas", help=help, metavar="<schema>", nargs="+", required=False, type=str)
help = "reconfigure the UEBA engine to start from this date (e.g. 2010-12-31)"
argument_parser.add_argument("-d", "--date", help=help, metavar="<date>", required=False, type=parse_date_argument)
arguments = argument_parser.parse_args()

conf = {
    "elasticsearch": {
        "cleanData": False
    },
    "presidio": {
        "dataPipeline": {
            "schemas": arguments.schemas,
            "startTime": arguments.date
        }
    }
}

print("Triggering the Reset Presidio DAG with this configuration:")
print(json.dumps(conf, indent=4).replace("\": null", "\": <unchanged>"))

query = {
    "api": "trigger_dag",
    "dag_id": "reset_presidio",
    "exec_date": datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S"),
    "conf": json.dumps(conf, separators=(",", ":"))
}

requests.get("http://localhost:8100/admin/rest_api/api?" + urllib.urlencode(query))
