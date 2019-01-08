import json
import presidio_upgrade_utils
import requests

from_version = presidio_upgrade_utils.read_installed_presidio_version()
to_version = presidio_upgrade_utils.get_installed_presidio_version()

if from_version != presidio_upgrade_utils.PRESIDIO_NOT_INSTALLED_FLAG:
    data = {"conf": {
        "upgrade": {
            "fromVersion": from_version,
            "toVersion": to_version
        }
    }}

    response = requests.post("http://localhost:8100/api/experimental/dags/presidio_upgrade_dag/dag_runs",
                             json.dumps(data),
                             headers={"cache-control": "no-cache", "content-type": "application/json"})

    if response.status_code != 201:
        raise ValueError("Post request to run the 'Presidio Upgrade' DAG failed.")
