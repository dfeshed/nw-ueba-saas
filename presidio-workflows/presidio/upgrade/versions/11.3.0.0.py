import json
import requests
from presidio.utils.airflow.reset_presidio import reset_presidio
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

config_reader = ConfigServerConfigurationReaderSingleton().config_reader
elasticsearch_host = config_reader.read("elasticsearch.host", "localhost")
elasticsearch_rest_port = config_reader.read("elasticsearch.restPort", "9200")

# For an upgrade, trigger Reset Presidio with the default arguments.
reset_presidio()

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
