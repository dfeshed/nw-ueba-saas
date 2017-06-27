import logging

from airflow import DAG
from datetime import datetime, timedelta

from presidio.builders.input.input_dag_builder import InputDagBuilder

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)


def test_valid_build():
    """

    Test valid input dag build
    :return:
    """

    logging.info('Test valid input dag build')
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
        'command': 'run',
    }

    dag = DAG(
        "input_test_dag", default_args=default_args, schedule_interval=timedelta(minutes=5))

    dag = InputDagBuilder(['dlpfile']).build(dag)

    assert dag.task_count == 1
