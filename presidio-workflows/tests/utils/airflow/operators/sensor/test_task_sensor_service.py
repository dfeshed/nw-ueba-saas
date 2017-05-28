import pytest
from mock import MagicMock


from datetime import datetime, timedelta

from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
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

@pytest.fixture()
def test_task_sensor_service():
    return TaskSensorService()

@pytest.fixture()
def test_dummy_task(test_dag):
    dummy_task_id = "test_task"
    return DummyOperator(dag=test_dag, task_id=dummy_task_id)


def test_dag_after_add_task_sequential_sensor(test_task_sensor_service, test_dummy_task):
    test_task_sensor_service.add_task_sequential_sensor(test_dummy_task)
    task_downstream_list = test_dummy_task.get_direct_relatives(upstream=False)
    assert len(task_downstream_list) == 0
    task_upstream_list = test_dummy_task.get_direct_relatives(upstream=True)
    assert len(task_upstream_list) == 1
    sensor = task_upstream_list[0]
    assert isinstance(sensor, TaskGapSensorOperator)
    assert sensor.get_external_dag_id() == test_dummy_task.dag.dag_id
    assert sensor.get_external_task_id() == test_dummy_task.task_id
    assert sensor.get_execution_delta() == timedelta(seconds=1)

def test_dag_after_add_task_gap_sensor(default_args, test_task_sensor_service, test_dummy_task):
    gapped_task_dag = DAG("test_gapped_dag", default_args=default_args)
    gapped_task = DummyOperator(dag=gapped_task_dag, task_id="gapped_task")
    execution_delta = timedelta(seconds=60)
    test_task_sensor_service.add_task_gap_sensor(test_dummy_task,gapped_task, execution_delta)
    task_downstream_list = test_dummy_task.get_direct_relatives(upstream=False)
    assert len(task_downstream_list) == 0
    task_upstream_list = test_dummy_task.get_direct_relatives(upstream=True)
    assert len(task_upstream_list) == 1
    sensor = task_upstream_list[0]
    assert isinstance(sensor, TaskGapSensorOperator)
    assert sensor.get_external_dag_id() == gapped_task_dag.dag_id
    assert sensor.get_external_task_id() == gapped_task.task_id
    assert sensor.get_execution_delta() == execution_delta

    gapped_task_downstream_list = gapped_task.get_direct_relatives(upstream=False)
    assert len(gapped_task_downstream_list) == 0
    gapped_task_upstream_list = gapped_task.get_direct_relatives(upstream=True)
    assert len(gapped_task_upstream_list) == 0

def test_dag_after_add_task_gap_sensor_with_2_tasks_to_keep_gap_from(default_args, test_task_sensor_service, test_dummy_task):
    gapped_task_dag1 = DAG("test_gapped_dag1", default_args=default_args)
    gapped_task1 = DummyOperator(dag=gapped_task_dag1, task_id="gapped_task1")
    execution_delta1 = timedelta(seconds=60)
    test_task_sensor_service.add_task_gap_sensor(test_dummy_task,gapped_task1, execution_delta1)

    gapped_task_dag2 = DAG("test_gapped_dag2", default_args=default_args)
    gapped_task2 = DummyOperator(dag=gapped_task_dag2, task_id="gapped_task2")
    execution_delta2 = timedelta(seconds=90)
    test_task_sensor_service.add_task_gap_sensor(test_dummy_task, gapped_task2, execution_delta2)

    task_downstream_list = test_dummy_task.get_direct_relatives(upstream=False)
    assert len(task_downstream_list) == 0
    task_upstream_list = test_dummy_task.get_direct_relatives(upstream=True)
    assert len(task_upstream_list) == 2

    sensor = task_upstream_list[0]
    assert isinstance(sensor, TaskGapSensorOperator)
    assert sensor.get_external_dag_id() == gapped_task_dag1.dag_id
    assert sensor.get_external_task_id() == gapped_task1.task_id
    assert sensor.get_execution_delta() == execution_delta1

    gapped_task_downstream_list = gapped_task1.get_direct_relatives(upstream=False)
    assert len(gapped_task_downstream_list) == 0
    gapped_task_upstream_list = gapped_task1.get_direct_relatives(upstream=True)
    assert len(gapped_task_upstream_list) == 0


    sensor = task_upstream_list[1]
    assert isinstance(sensor, TaskGapSensorOperator)
    assert sensor.get_external_dag_id() == gapped_task_dag2.dag_id
    assert sensor.get_external_task_id() == gapped_task2.task_id
    assert sensor.get_execution_delta() == execution_delta2

    gapped_task_downstream_list = gapped_task2.get_direct_relatives(upstream=False)
    assert len(gapped_task_downstream_list) == 0
    gapped_task_upstream_list = gapped_task2.get_direct_relatives(upstream=True)
    assert len(gapped_task_upstream_list) == 0
    


