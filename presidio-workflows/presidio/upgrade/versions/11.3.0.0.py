import json
import requests
from presidio.utils.airflow.upgrade_utils import run_reset_presidio_for_upgrade
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

config_reader = ConfigServerConfigurationReaderSingleton().config_reader
elasticsearch_host = config_reader.read("elasticsearch.host", "localhost")
elasticsearch_rest_port = config_reader.read("elasticsearch.restPort", "9200")

# Run the "Reset Presidio for Upgrades" DAG first.
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

requests.post(
    'http://{}:{}/presidio-output-indicator/_update_by_query'.format(elasticsearch_host, elasticsearch_rest_port),
    data=json.dumps(data, separators=(",", ":"))
)

inline_script = "ctx._source.indicatorsNames=ctx._source.indicatorsNames.stream()" \
                "    .map(name -> name.equals('abnormal_event_day_time') ? 'abnormal_file_day_time' : name)" \
                "    .collect(Collectors.toList())"
data = {
    "script": {
        "inline": inline_script,
        "lang": "painless"
    },
    "query": {
        "term": {
            "indicatorsNames": "abnormal_event_day_time"
        }
    }
}

requests.post(
    'http://{}:{}/presidio-output-alert/_update_by_query'.format(elasticsearch_host, elasticsearch_rest_port),
    data=json.dumps(data, separators=(",", ":"))
)
