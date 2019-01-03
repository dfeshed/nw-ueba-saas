from airflow.operators.python_operator import PythonOperator
from elasticsearch import Elasticsearch
from presidio.builders.context_conf_extractor import extract_context_conf

ELASTICSEARCH_CONF_KEY_NAME = "elasticsearch"
CLEAN_DATA_KEY_NAME = "cleanData"


def build_clean_elasticsearch_data_operator(dag):
    return PythonOperator(task_id='clean_elasticsearch_data',
                          python_callable=clean_elasticsearch_data,
                          provide_context=True,
                          dag=dag)


def clean_elasticsearch_data(**context):
    elasticsearch_conf = extract_context_conf(ELASTICSEARCH_CONF_KEY_NAME, **context)

    # Skip if the "clean data" flag is turned off (the flag is turned on by default).
    if not elasticsearch_conf.get(CLEAN_DATA_KEY_NAME, True):
        return

    elasticsearch = Elasticsearch(hosts=["localhost"])
    indices = elasticsearch.cat.indices(h="index").encode("utf-8").split("\n")

    for index in indices:
        # Escape system metrics.
        if not index.startswith(".") and not index == "":
            if index.startswith(('presidio-monitoring', 'metricbeat', 'packetbeat')):
                elasticsearch.indices.delete(index=index,
                                             ignore=[404],
                                             request_timeout=360)
            else:
                elasticsearch.delete_by_query(index=index,
                                              body="{\"query\": {\"match_all\": {}}}",
                                              request_timeout=360)
