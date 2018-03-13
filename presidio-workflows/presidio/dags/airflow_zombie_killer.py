import logging
import subprocess

from airflow.models import DAG, DagRun, TaskInstance
from airflow.operators.python_operator import PythonOperator
from airflow.operators.subdag_operator import SubDagOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from datetime import datetime, timedelta

unfinished_states = State.unfinished()
delta_from_max_date = timedelta(minutes=10)
delta_to_mark_as_stuck = timedelta(hours=12)


@provide_session
def find_dag_runs(first, dag_id=None, execution_date=None, state=None, session=None):
    query = session.query(DagRun)
    query = query if dag_id is None else query.filter(DagRun.dag_id == dag_id)
    query = query if execution_date is None else query.filter(DagRun.execution_date == execution_date)
    query = query if state is None else query.filter(DagRun.state == state)
    return query.first() if first else query.all()


@provide_session
def find_task_instances(first, task_id=None, dag_id=None, execution_date=None, state=None, operator=None, session=None):
    query = session.query(TaskInstance)
    query = query if task_id is None else query.filter(TaskInstance.task_id == task_id)
    query = query if dag_id is None else query.filter(TaskInstance.dag_id == dag_id)
    query = query if execution_date is None else query.filter(TaskInstance.execution_date == execution_date)
    query = query if state is None else query.filter(TaskInstance.state == state)
    query = query if operator is None else query.filter(TaskInstance.operator == operator)
    return query.first() if first else query.all()


def delta_passed(delta, dates):
    """
    Checks if all the datetime instances in the given list are "delta" time older than the current datetime.
    If every datetime in the list is None, True is returned.
    :param delta: A timedelta instance.
    :param dates: A list of datetime instances.
    :return: True if "delta" time has passed from each datetime in the list, False otherwise.
    """
    max_allowed_datetime = datetime.now() - delta

    for date in dates:
        if (date is not None) and (date > max_allowed_datetime):
            return False

    return True


def is_prefix_sub_list(sub_list, main_list):
    """
    Checks if the first list is an opening sub-list of the second list.
    For example, ['a', 'b', 'c'] is a prefix sub-list of ['a', 'b', 'c', 'd', 'e'], but ['c', 'd', 'e'] isn't.
    :param sub_list: The potential prefix sub-list.
    :param main_list: The containing list.
    :return: True if sub_list is a prefix sub-list of main_list, False otherwise.
    """
    if len(sub_list) > len(main_list):
        return False

    for i in range(0, len(sub_list)):
        if sub_list[i] != main_list[i]:
            return False

    return True


def update_full_task_ids_to_kill(execution_date_to_full_task_ids_to_kill_dictionary):
    """
    For each list of full task IDs to kill in the given dictionary, this function eliminates IDs with an
    ancestor-descendant relation and keeps only the ancestors (i.e. if one of the full task IDs is of a sub-DAG, all of
    its descendants are removed from the list). This is because killing a sub-DAG task instance with a certain execution
    date kills all of its descendants as well.
    :param execution_date_to_full_task_ids_to_kill_dictionary:
           Maps an execution date to its list of full task IDs to kill.
           Each full task ID should look like this: [..., <grandparent_dag_id>, <parent_dag_id>, <task_id>].
    :return: None.
    """
    for execution_date, full_task_ids_to_kill in execution_date_to_full_task_ids_to_kill_dictionary.iteritems():
        for i in range(0, len(full_task_ids_to_kill)):
            for j in range(i + 1, len(full_task_ids_to_kill)):
                if (full_task_ids_to_kill[i] is not None) and (full_task_ids_to_kill[j] is not None):
                    full_task_id_to_kill_i = full_task_ids_to_kill[i]
                    full_task_id_to_kill_j = full_task_ids_to_kill[j]

                    if is_prefix_sub_list(full_task_id_to_kill_i, full_task_id_to_kill_j):
                        full_task_ids_to_kill[j] = None
                    elif is_prefix_sub_list(full_task_id_to_kill_j, full_task_id_to_kill_i):
                        full_task_ids_to_kill[i] = None

    for execution_date in execution_date_to_full_task_ids_to_kill_dictionary:
        execution_date_to_full_task_ids_to_kill_dictionary[execution_date] = filter(
            lambda full_task_id_to_kill: full_task_id_to_kill is not None,
            execution_date_to_full_task_ids_to_kill_dictionary[execution_date]
        )


def kill_task_instances(execution_date_to_full_task_ids_to_kill_dictionary):
    """
    Kills all the task instances contained in the given dictionary. Each execution date (key) is mapped to a list of its
    full task IDs that should be killed (value).
    :param execution_date_to_full_task_ids_to_kill_dictionary:
           The dictionary containing the task instances to kill.
           Each full task ID should look like this: [..., <grandparent_dag_id>, <parent_dag_id>, <task_id>].
    :return: None.
    """
    for execution_date, full_task_ids_to_kill in execution_date_to_full_task_ids_to_kill_dictionary.iteritems():
        for full_task_id_to_kill in full_task_ids_to_kill:
            # Skip the full task ID of the main DAG, because it doesn't have task instances to kill
            if len(full_task_id_to_kill) > 1:
                dag_id = '.'.join(full_task_id_to_kill[:-1])
                task_id = full_task_id_to_kill[-1]
                task_instance = find_task_instances(
                    first=True, task_id=task_id, dag_id=dag_id, execution_date=execution_date
                )
                state = task_instance.state
                pid = str(task_instance.pid)

                if state == State.RUNNING:
                    msg = "Killing running task instance: execution_date = {}, dag_id = {}, task_id = {}, pid = {}." \
                        .format(execution_date, dag_id, task_id, pid)
                    logging.info(msg)
                    subprocess.Popen(['kill', pid])
                else:
                    msg = "Cannot kill task instance, because it's not running: " \
                          "execution_date = {}, dag_id = {}, task_id = {}, state = {}, pid = {}." \
                        .format(execution_date, dag_id, task_id, state, pid)
                    logging.info(msg)


def kill_zombie_sub_dag_task_instances():
    running_sub_dag_task_instances = find_task_instances(
        first=False, state=State.RUNNING, operator=SubDagOperator.__name__
    )

    for running_sub_dag_task_instance in running_sub_dag_task_instances:
        dag_id = "{}.{}".format(running_sub_dag_task_instance.dag_id, running_sub_dag_task_instance.task_id)
        execution_date = running_sub_dag_task_instance.execution_date
        dag_run = find_dag_runs(first=True, dag_id=dag_id, execution_date=execution_date)
        has_unfinished_task_instances = False
        task_instance_dates = []

        for task_instance in dag_run.get_task_instances():
            if task_instance.state in unfinished_states:
                has_unfinished_task_instances = True
                break
            else:
                task_instance_dates.extend([task_instance.start_date, task_instance.end_date])

        if (not has_unfinished_task_instances) and (delta_passed(delta_from_max_date, task_instance_dates)):
            pid = str(running_sub_dag_task_instance.pid)
            msg = "Killing zombie sub-DAG task instance: dag_id = {}, execution_date = {}, pid = {}." \
                .format(dag_id, execution_date, pid)
            dag_run_state = dag_run.state

            if dag_run_state != State.RUNNING:
                msg = "{} The DAG run state is inconsistent with the sub-DAG task instance state: " \
                      "Should be {}, but is {}." \
                    .format(msg, State.RUNNING, dag_run_state)

            logging.info(msg)
            subprocess.Popen(['kill', pid])


def kill_task_instances_stuck_in_up_for_retry():
    max_datetime_to_mark_as_stuck = datetime.now() - delta_to_mark_as_stuck
    execution_date_to_full_task_ids_to_kill_dictionary = {}

    for task_instance_in_up_for_retry in find_task_instances(first=False, state=State.UP_FOR_RETRY):
        if task_instance_in_up_for_retry.end_date <= max_datetime_to_mark_as_stuck:
            execution_date = task_instance_in_up_for_retry.execution_date
            full_task_ids_to_kill = execution_date_to_full_task_ids_to_kill_dictionary.get(execution_date)

            if full_task_ids_to_kill is None:
                full_task_ids_to_kill = []
                execution_date_to_full_task_ids_to_kill_dictionary[execution_date] = full_task_ids_to_kill

            # For a task instance stuck in up for retry, its running parent task instance should be killed
            full_task_ids_to_kill.append(task_instance_in_up_for_retry.dag_id.split('.'))

    update_full_task_ids_to_kill(execution_date_to_full_task_ids_to_kill_dictionary)
    kill_task_instances(execution_date_to_full_task_ids_to_kill_dictionary)


airflow_zombie_killer = DAG(
    dag_id="airflow_zombie_killer",
    schedule_interval=timedelta(minutes=15),
    start_date=datetime(year=2017, month=1, day=1),
    catchup=False,
    max_active_runs=1
)

zombie_sub_dag_task_instance_killer = PythonOperator(
    task_id="zombie_sub_dag_task_instance_killer",
    python_callable=kill_zombie_sub_dag_task_instances,
    dag=airflow_zombie_killer
)

stuck_in_up_for_retry_task_instance_killer = PythonOperator(
    task_id="stuck_in_up_for_retry_task_instance_killer",
    python_callable=kill_task_instances_stuck_in_up_for_retry,
    dag=airflow_zombie_killer
)

zombie_sub_dag_task_instance_killer >> stuck_in_up_for_retry_task_instance_killer
