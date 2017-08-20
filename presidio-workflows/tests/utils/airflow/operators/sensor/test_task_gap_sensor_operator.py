from datetime import datetime

import pytest
from mock import MagicMock


from datetime import datetime, timedelta

from airflow import DAG

from presidio.utils.airflow.operators.sensor.task_gap_sensor_operator import TaskGapSensorOperator

DEFAULT_DATE = datetime(2014, 1, 1)


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

@pytest.fixture()
def test_dag(default_args):
    return DAG("test_dag", default_args=default_args)

def test_poke_when_num_of_running_dags_is_bigger_than_zero_and_num_of_task_to_wait_is_zero(test_dag):
    task_gap_sensor_operator = TaskGapSensorOperator(dag=test_dag, task_id="test_task",external_dag_id='test_dag', external_task_id='test_task', execution_delta=timedelta(seconds=1))
    TaskGapSensorOperator.get_num_of_dag_runs_to_wait_for = MagicMock(return_value=5)
    TaskGapSensorOperator.get_num_of_task_instances_to_wait_for = MagicMock(return_value=0)
    context = {'execution_date': datetime.now()}
    ret = task_gap_sensor_operator.poke(context)
    assert ret == True

def test_poke_when_num_of_running_dags_is_bigger_than_zero_num_of_task_to_wait_is_one(test_dag):
    task_gap_sensor_operator = TaskGapSensorOperator(dag=test_dag, task_id="test_task",external_dag_id='test_dag', external_task_id='test_task', execution_delta=timedelta(seconds=1))
    TaskGapSensorOperator.get_num_of_dag_runs_to_wait_for = MagicMock(return_value=5)
    TaskGapSensorOperator.get_num_of_task_instances_to_wait_for = MagicMock(return_value=1)
    context = {'execution_date': datetime.now()}
    ret = task_gap_sensor_operator.poke(context)
    assert ret == False

def test_poke_when_num_of_running_dags_is_bigger_than_zero_num_of_task_to_wait_is_1000(test_dag):
    task_gap_sensor_operator = TaskGapSensorOperator(dag=test_dag, task_id="test_task",external_dag_id='test_dag', external_task_id='test_task', execution_delta=timedelta(seconds=1))
    TaskGapSensorOperator.get_num_of_dag_runs_to_wait_for = MagicMock(return_value=5)
    TaskGapSensorOperator.get_num_of_task_instances_to_wait_for = MagicMock(return_value=1000)
    context = {'execution_date': datetime.now()}
    ret = task_gap_sensor_operator.poke(context)
    assert ret == False

def test_poke_when_num_of_running_dags_is_zero_and_num_of_task_to_wait_is_zero(test_dag):
    task_gap_sensor_operator = TaskGapSensorOperator(dag=test_dag, task_id="test_task",external_dag_id='test_dag', external_task_id='test_task', execution_delta=timedelta(seconds=1))
    TaskGapSensorOperator.get_num_of_dag_runs_to_wait_for = MagicMock(return_value=0)
    TaskGapSensorOperator.get_num_of_task_instances_to_wait_for = MagicMock(return_value=0)
    context = {'execution_date': datetime.now()}
    ret = task_gap_sensor_operator.poke(context)
    assert ret == True

def test_poke_when_num_of_running_dags_is_zero_num_of_task_to_wait_is_one(test_dag):
    task_gap_sensor_operator = TaskGapSensorOperator(dag=test_dag, task_id="test_task",external_dag_id='test_dag', external_task_id='test_task', execution_delta=timedelta(seconds=1))
    TaskGapSensorOperator.get_num_of_dag_runs_to_wait_for = MagicMock(return_value=0)
    TaskGapSensorOperator.get_num_of_task_instances_to_wait_for = MagicMock(return_value=1)
    context = {'execution_date': datetime.now()}
    ret = task_gap_sensor_operator.poke(context)
    assert ret == True

def test_poke_when_num_of_running_dags_is_zero_num_of_task_to_wait_is_1000(test_dag):
    task_gap_sensor_operator = TaskGapSensorOperator(dag=test_dag, task_id="test_task",external_dag_id='test_dag', external_task_id='test_task', execution_delta=timedelta(seconds=1))
    TaskGapSensorOperator.get_num_of_dag_runs_to_wait_for = MagicMock(return_value=0)
    TaskGapSensorOperator.get_num_of_task_instances_to_wait_for = MagicMock(return_value=1000)
    context = {'execution_date': datetime.now()}
    ret = task_gap_sensor_operator.poke(context)
    assert ret == True

