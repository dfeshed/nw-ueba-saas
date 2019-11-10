import requests
from presidio.utils.airflow.upgrade_utils import run_reset_presidio_for_upgrade
import json

# Run reset_presidio dag for upgrade
run_reset_presidio_for_upgrade()

data = {
    "script": {
        "source": "ctx._source.name = params.name",
        "lang": "painless",
        "params": {
            "name": "abnormal_file_day_time"
        }
    },
    "query": {
        "term": {
            "name": "abnormal_event_day_time"
        }
    }
}

requests.post('http://localhost:9200/presidio-output-indicator/_update_by_query',data=json.dumps(data, separators=(",", ":")))

data = {
    "script": {
        "inline": "ctx._source.indicatorsNames=ctx._source.indicatorsNames.stream().map(x->x.equals('abnormal_event_day_time')?'abnormal_file_day_time':x).collect(Collectors.toList())",
        "lang": "painless"
    },
    "query": {
        "term": {
            "indicatorsNames": "abnormal_event_day_time"
        }
    }
}

requests.post('http://localhost:9200/presidio-output-alert/_update_by_query',data=json.dumps(data, separators=(",", ":")))
