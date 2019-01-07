import json
import requests

from airflow.operators.python_operator import PythonOperator
from presidio.utils.airflow.context_conf_extractor import extract_context_conf

PRESIDIO_CONF_KEY_NAME = "presidio"
DATA_PIPELINE_KEY_NAME = "dataPipeline"
SCHEMAS_KEY_NAME = "schemas"
START_TIME_KEY_NAME = "startTime"


def build_reset_presidio_configuration_operator(dag):
    return PythonOperator(task_id='reset_presidio_configuration',
                          python_callable=reset_presidio_configuration,
                          provide_context=True,
                          dag=dag)


def reset_presidio_configuration(**context):
    presidio_conf = extract_context_conf(PRESIDIO_CONF_KEY_NAME, **context)

    # Extract all the data pipeline configurations.
    data_pipeline = presidio_conf.get(DATA_PIPELINE_KEY_NAME, {})
    schemas = data_pipeline.get(SCHEMAS_KEY_NAME)
    start_time = data_pipeline.get(START_TIME_KEY_NAME)

    # Append a replace operation for each data pipeline configuration that has been changed.
    operations = []
    append_replace_operation_if_value_is_not_none(operations, "/dataPipeline/schemas", schemas)
    append_replace_operation_if_value_is_not_none(operations, "/dataPipeline/startTime", start_time)

    # Skip if none of the data pipeline configurations has been changed.
    if len(operations) == 0:
        return

    response = requests.patch("http://localhost:8881/configuration",
                              json.dumps({"operations": operations}),
                              headers={'content-type': 'application/json', 'accept': 'application/json'})

    if response.status_code != 201:
        raise ValueError("Patch request to reset Presidio's configuration failed.")


def append_replace_operation_if_value_is_not_none(operations, path, value):
    if value is not None:
        operation = {"op": "replace", "path": path, "value": value}
        operations.append(operation)
