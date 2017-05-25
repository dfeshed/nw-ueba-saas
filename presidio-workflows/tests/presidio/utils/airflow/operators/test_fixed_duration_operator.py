import logging
from datetime import datetime, timedelta
import pytest
from airflow import DAG
from airflow.models import TaskInstance
from airflow.settings import Session
from airflow.utils.state import State
from presidio.operators.fixed_duration_operator import FixedDurationOperator

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)

JAR_PATH = '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar'
MAIN_CLASS = 'HelloWorld.Main'


def get_task_instances(dag):
    """
    
    Get_task_instances according to dag_id
    :param dag:
    :return: tis
    """
    session = Session()
    tis = session.query(TaskInstance).filter(
        TaskInstance.dag_id == dag.dag_id,
    )
    session.close()
    return tis


def assert_task_success_state(tis):
    """

    Assert task state
    :param tis: task instances
    :return:
    """
    for ti in tis:
        if ti.task_id == 'fixed_duration_operator':
            assert ti.state == State.SUCCESS
        else:
            raise


def assert_task_skipped_state(tis):
    """

    Assert task state
    :param tis: task instances
    :return:
    """
    for ti in tis:
        if ti.task_id == 'fixed_duration_operator':
            assert ti.state == State.SKIPPED
        else:
            raise


def test_skipped_state():
    """

    Test skipped task of fixed duration operator 
    :return:
    """

    logging.info('Test skipped task state of fixed duration operator')
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
    }

    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_skipped_state", default_args=default_args, schedule_interval=timedelta(minutes=5))

    java_args = {
        'a': 'one',
        'b': 'two'
    }

    task = FixedDurationOperator(
        task_id='fixed_duration_operator',
        jvm_args=jvm_args,
        java_args=java_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)

    task.clear()
    task.run(start_date=default, end_date=default)
    tis = get_task_instances(dag)
    assert_task_skipped_state(tis)


def test_success_state():
    """

    Test success state of fixed duration operator
    :return:
    """
    logging.info('Test fixed duration operator')

    default = datetime(2014, 5, 13, 13, 56, 2)

    default_args = {
        'owner': 'airflow',
        'depends_on_past': False,
        'start_date': default,
        'email': ['airflow@airflow.com'],
        'email_on_failure': False,
        'email_on_retry': False,
        'retries': 1,
        'retry_delay': timedelta(minutes=5),
    }

    logging.info('Test fixed duration operator')
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_success_state", default_args=default_args, schedule_interval=timedelta(minutes=5))

    java_args = {
        'a': 'one',
        'b': 'two'
    }

    task = FixedDurationOperator(
        task_id='fixed_duration_operator',
        jvm_args=jvm_args,
        java_args=java_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)

    task.clear()
    task.run(start_date=default, end_date=default)
    tis = get_task_instances(dag)
    assert_task_success_state(tis)
