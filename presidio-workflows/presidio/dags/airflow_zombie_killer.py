import logging

from airflow.models import DAG, DagRun, TaskInstance
from airflow.operators.python_operator import PythonOperator
from airflow.operators.subdag_operator import SubDagOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from datetime import datetime, timedelta

unfinished_states = State.unfinished()


@provide_session
def find_running_dag_instances(session=None):
    return session.query(DagRun).filter(DagRun.state == State.RUNNING).all()


@provide_session
def update_dag_instance_state(dag_instance, state, session=None):
    dag_instance.state = state
    session.merge(dag_instance)
    session.commit()


@provide_session
def find_task_instance(task_id, execution_date, session=None):
    query = session.query(TaskInstance)
    query = query.filter(TaskInstance.task_id == task_id, TaskInstance.execution_date == execution_date)
    return query.first()


@provide_session
def update_task_instance_state(task_instance, state, session=None):
    task_instance.state = state
    session.merge(task_instance)
    session.commit()


@provide_session
def find_running_sub_dag_operator_instances(session=None):
    query = session.query(TaskInstance)
    query = query.filter(TaskInstance.state == State.RUNNING, TaskInstance.operator == SubDagOperator.__name__)
    return query.all()


@provide_session
def find_dag_instance(dag_id, execution_date, session=None):
    query = session.query(DagRun)
    query = query.filter(DagRun.dag_id == dag_id, DagRun.execution_date == execution_date)
    return query.first()


def finish_zombie_dag_instances():
    for running_dag_instance in find_running_dag_instances():
        has_unfinished_task_instances = False
        expected_dag_instance_state = State.SUCCESS
        dag_id = running_dag_instance.dag_id
        execution_date = running_dag_instance.execution_date

        for task_instance in running_dag_instance.get_task_instances():
            task_instance_state = task_instance.state

            if task_instance_state in unfinished_states:
                has_unfinished_task_instances = True
            elif task_instance_state != State.SUCCESS:
                expected_dag_instance_state = State.FAILED

        if not has_unfinished_task_instances:
            msg = "Updating zombie DAG instance state: dag_id = {}, execution_date = {}, state = {}." \
                .format(dag_id, execution_date, expected_dag_instance_state)
            logging.info(msg)
            update_dag_instance_state(running_dag_instance, expected_dag_instance_state)
            task_instance = find_task_instance(dag_id.split('.')[-1], execution_date)

            if task_instance:
                update_task_instance_state(task_instance, expected_dag_instance_state)


def finish_zombie_sub_dag_operator_instances():
    for running_sub_dag_operator_instance in find_running_sub_dag_operator_instances():
        task_id = running_sub_dag_operator_instance.task_id
        dag_id = running_sub_dag_operator_instance.dag_id + "." + task_id
        execution_date = running_sub_dag_operator_instance.execution_date
        dag_instance_state = find_dag_instance(dag_id, execution_date).state

        if dag_instance_state != State.RUNNING:
            msg = "Updating zombie sub-DAG operator instance state: task_id = {}, execution_date = {}, state = {}." \
                .format(task_id, execution_date, dag_instance_state)
            logging.info(msg)
            update_task_instance_state(running_sub_dag_operator_instance, dag_instance_state)


airflow_zombie_killer = DAG(
    dag_id="airflow_zombie_killer",
    schedule_interval=timedelta(minutes=5),
    start_date=datetime(year=2017, month=1, day=1),
    catchup=False
)

zombie_dag_instance_killer = PythonOperator(
    task_id="zombie_dag_instance_killer",
    python_callable=finish_zombie_dag_instances,
    dag=airflow_zombie_killer
)

zombie_sub_dag_operator_instance_killer = PythonOperator(
    task_id="zombie_sub_dag_operator_instance_killer",
    python_callable=finish_zombie_sub_dag_operator_instances,
    dag=airflow_zombie_killer
)

zombie_dag_instance_killer >> zombie_sub_dag_operator_instance_killer
