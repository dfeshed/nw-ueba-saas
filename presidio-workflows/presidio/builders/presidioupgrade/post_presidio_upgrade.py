import json
import presidio_upgrade_utils
import requests
import urllib

from_version = presidio_upgrade_utils.read_installed_presidio_version()
to_version = presidio_upgrade_utils.get_installed_presidio_version()

if from_version != presidio_upgrade_utils.PRESIDIO_NOT_INSTALLED_FLAG:
    conf = {
        "upgrade": {
            "fromVersion": from_version,
            "toVersion": to_version
        }
    }

    query = {
        "api": "trigger_dag",
        "dag_id": "presidio_upgrade_dag",
        "conf": json.dumps(conf, separators=(",", ":"))
    }

    response = requests.get("http://localhost:8100/admin/rest_api/api?%s" % urllib.urlencode(query))

    if response.status_code != 200:
        raise ValueError("Get request to trigger a 'Presidio Upgrade' DAG run failed.")
