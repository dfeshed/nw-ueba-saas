import logging
from datetime import datetime, timedelta

from airflow import DAG

from presidio.builders.indicator.indicator_dag_builder import IndicatorDagBuilder
from presidio.utils.configuration.config_server_reader_test_builder import ConfigServerConfigurationReaderTestBuilder

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)


def test_valid_build():
    """

    Test valid indicator dag build
    :return:
    """

    logging.info('Test valid indicator dag build')
    ConfigServerConfigurationReaderTestBuilder().build()

    default = datetime(2014, 5, 13, 13, 00, 2)

    default_args = {
        'owner': 'airflow',
        'depends_on_past': False,
        'start_date': default,
        'email': ['airflow@airflow.com'],
        'email_on_failure': False,
        'email_on_retry': False,
        'retries': 1,
        'retry_delay': timedelta(minutes=5),
        'schema': 'FILE'
    }

    dag = DAG(
        "indicator_test_dag", default_args=default_args, schedule_interval=timedelta(minutes=5))

    dag = IndicatorDagBuilder().build(dag)

    assert dag.task_count == 11
