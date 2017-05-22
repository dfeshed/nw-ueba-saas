import logging
from datetime import datetime, timedelta
import pytest
from airflow import DAG
from airflow.models import TaskInstance
from airflow.settings import Session
from airflow.utils.state import State
from presidio.operators.presidio_jar_operator import PresidioJarOperator
from presidio.operators.presidio_jar_operator import InValidFixedDurationStrategyError
from presidio.operators.presidio_jar_operator import InValidStartDateError
from presidio.utils.airflow.services.time_service import TimeService

DEFAULT_DATE = datetime(2014, 5, 13, 10, 0, 2)
FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)

JAR_PATH = '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar'
MAIN_CLASS = 'HelloWorld.Main'


@pytest.fixture
def default_args():
    return {
        'owner': 'airflow',
        'depends_on_past': False,
        'start_date': DEFAULT_DATE,
        'email': ['airflow@airflow.com'],
        'email_on_failure': False,
        'email_on_retry': False,
        'retries': 1,
        'retry_delay': timedelta(minutes=5),
    }


def get_task_instances(dag):
    """
    
    Get_task_instances according to dag_id
    :param dag:
    :return: tis
    """
    session = Session()
    tis = session.query(TaskInstance).filter(
        TaskInstance.dag_id == dag.dag_id,
        TaskInstance.execution_date == DEFAULT_DATE
    )
    session.close()
    return tis


def assert_task_state(tis):
    """

    Assert task state
    :param tis: task instances
    :return:
    """
    for ti in tis:
        if ti.task_id == 'presidio_jar_operator':
            assert ti.state == State.SUCCESS
        else:
            raise


def test_presidio_jar_operator(default_args):
    """

    Test presidio jar operator
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('Test presidio operator')
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_presidio_operator_dags", default_args=default_args, schedule_interval=timedelta(weeks=1))

    java_args = {
        'a': 'one',
        'b': 'two'
    }

    task = PresidioJarOperator(
        task_id='presidio_jar_operator',
        jvm_args=jvm_args,
        java_args=java_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)

    task.clear()
    task.run(start_date=DEFAULT_DATE, end_date=DEFAULT_DATE)
    tis = get_task_instances(dag)
    assert_task_state(tis)


def test_valid_fixed_duration(default_args):
    """
    
    Test fixed_duration validation
    :param default_args: 
    :return: 
    """
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_valid_fixed_duration_dag", default_args=default_args, schedule_interval=timedelta(weeks=1))

    task = PresidioJarOperator(
        task_id='valid_fixed_duration',
        jvm_args=jvm_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)

    valid = task.validate_fixed_duration(FIX_DURATION_STRATEGY_HOURLY.total_seconds())
    assert valid is True


def test_invalid_fixed_duration(default_args):
    """
    
    Test Expected error on invalid fixed_duration
    :param default_args: 
    :return: 
    """
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_invalid_fixed_duration_dag", default_args=default_args, schedule_interval=timedelta(hours=1))
    task = PresidioJarOperator(
        task_id='invalid_fixed_duration',
        jvm_args=jvm_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)

    with pytest.raises(InValidFixedDurationStrategyError):
        task.validate_fixed_duration(timedelta(seconds=70).total_seconds())


def test_valid_start_date(default_args):
    """
    
    Test start_date validation
    :param default_args: 
    :return: 
    """
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_valid_start_date_dag", default_args=default_args, schedule_interval=timedelta(weeks=1))
    task = PresidioJarOperator(
        task_id='valid_start_date',
        jvm_args=jvm_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)
    start_date = datetime(2014, 5, 13, 10, 0, 0)
    valid = task.validate_start_date(TimeService.datetime_to_epoch(start_date),
                                     FIX_DURATION_STRATEGY_HOURLY.total_seconds())
    assert valid is True


def test_invalid_start_date(default_args):
    """
    
    Test Expected error on invalid start_date
    :param default_args: 
    :return: 
    """
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_invalid_start_date_dag", default_args=default_args, schedule_interval=timedelta(hours=1))
    task = PresidioJarOperator(
        task_id='invalid_start_date',
        jvm_args=jvm_args,
        fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
        dag=dag)

    start_date = datetime(2014, 5, 13, 10, 15, 0)
    with pytest.raises(InValidStartDateError):
        task.validate_start_date(TimeService.datetime_to_epoch(start_date),
                                 FIX_DURATION_STRATEGY_HOURLY.total_seconds())
