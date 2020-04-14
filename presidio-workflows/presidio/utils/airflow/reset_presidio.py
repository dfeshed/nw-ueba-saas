import argparse
import json
import requests
import time
import urllib
from datetime import datetime, timedelta

utc_now = datetime.utcnow()
default_time_delta = timedelta(days=27)


def reset_presidio(clean=False, schemas=None, date=utc_now - default_time_delta):
    """
    Clean and/or reconfigure the UEBA engine by triggering a 'Reset Presidio' DAG run.
    @param clean:
           If True, clean any existing data in Elasticsearch (Alerts) before resetting the UEBA engine.
           By default, do not clean the Elasticsearch data - Presidio should clean the rest of the data, but the Alerts
           that were already created should be kept.
           Cannot be None.
    @type clean: bool
    @param schemas:
           Reconfigure the UEBA engine to work on these schemas (e.g. ['ACTIVE_DIRECTORY', 'AUTHENTICATION', 'FILE']).
           By default, do not change the active schemas - Presidio should continue working on the schemas that are
           currently configured.
    @type schemas: list[str]
    @param date:
           Reconfigure the UEBA engine to start from midnight UTC of this date (e.g. 2010-12-31 00:00:00).
           By default, reset the start time to 27 days before the current system day, at midnight UTC - Since there
           should not be duplicate Alerts, every logical hour that was already processed should be skipped, including
           hours of the current system day. Presidio starts creating Alerts 28 logical days after the start time. So if
           the start time is reset to 27 days before the current system day, at midnight UTC, the first Alerts Presidio
           creates will be of the following day (i.e. of tomorrow). As a result, Alerts of the current system day will
           not be created (at most 24 logical hours without Alerts), but there will not be duplicates.
           Cannot be None.
    @type date: datetime
    """
    if utc_now - default_time_delta > date:
        string = "Warning: 'date' ({}) is older than {} day(s), duplicate Alerts might be created."
        print(string.format(date, default_time_delta.days))

    configuration = {
        "elasticsearch": {
            "cleanData": clean
        },
        "presidio": {
            "dataPipeline": {
                "schemas": schemas,
                "startTime": date.strftime("%Y-%m-%dT00:00:00Z")
            }
        }
    }

    execution_date = utc_now.strftime("%Y-%m-%dT%H:%M:%S")
    _trigger_dag(configuration, execution_date)
    _wait_for_dag_to_finish_successfully(execution_date)


def _trigger_dag(configuration, execution_date):
    print("Triggering a 'Reset Presidio' DAG run with this configuration:")
    print(json.dumps(configuration, indent=4, sort_keys=True).replace("\": null", "\": <unchanged>"))

    url = "http://localhost:8100/admin/rest_api/api?" + urllib.urlencode({
        "api": "trigger_dag",
        "dag_id": "reset_presidio",
        "exec_date": execution_date,
        "conf": json.dumps(configuration, separators=(",", ":"))
    })

    if requests.get(url).status_code != 200:
        raise ValueError("Get request to trigger a 'Reset Presidio' DAG run failed.")


def _wait_for_dag_to_finish_successfully(execution_date):
    url = "http://localhost:8100/admin/rest_api/api?" + urllib.urlencode({
        "api": "dag_state",
        "dag_id": "reset_presidio",
        "execution_date": execution_date
    })

    while True:
        time.sleep(60)
        state = requests.get(url).json()["output"]["stdout"].strip()

        if state == "success":
            break
        elif state == "failed":
            raise ValueError("The triggered 'Reset Presidio' DAG run failed.")
        elif state != "running":
            raise ValueError("The triggered 'Reset Presidio' DAG run is in an unknown state ({}).".format(state))


if __name__ == "__main__":
    argument_parser = argparse.ArgumentParser()

    argument_parser.add_argument(
        "-c", "--clean",
        action="store_true",
        help="clean any existing data in Elasticsearch (Alerts) before resetting the UEBA engine",
        required=False
    )

    argument_parser.add_argument(
        "-s", "--schemas",
        help="reconfigure the UEBA engine to work on these schemas (e.g. ACTIVE_DIRECTORY AUTHENTICATION FILE)",
        metavar="<schema>",
        nargs="+",
        required=False,
        type=str
    )

    argument_parser.add_argument(
        "-d", "--date",
        help="reconfigure the UEBA engine to start from midnight UTC of this date (e.g. 2010-12-31)",
        metavar="<date>",
        required=False,
        type=lambda argument: datetime.strptime(argument, "%Y-%m-%d")
    )

    arguments = vars(argument_parser.parse_args())
    arguments = {key: value for key, value in arguments.iteritems() if value is not None}
    reset_presidio(**arguments)
