import logging
import subprocess

from airflow.models import DAG, DagRun, TaskInstance
from airflow.operators.python_operator import PythonOperator
from airflow.operators.subdag_operator import SubDagOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from datetime import datetime, timedelta

unfinished_states = State.unfinished()
failed_states = [State.FAILED, State.UPSTREAM_FAILED]
delta_from_max_date = timedelta(minutes=10)
delta_to_mark_as_stuck = timedelta(hours=12)


@provide_session
def find_dag_instances(first, dag_id=None, execution_date=None, state=None, session=None):
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


@provide_session
def update_dag_instance_state(dag_instance, state, session=None):
    dag_instance.state = state
    dag_instance.end_date = datetime.now()
    session.merge(dag_instance)
    session.commit()


@provide_session
def update_task_instance_state(task_instance, state, session=None):
    task_instance.state = state
    task_instance.end_date = datetime.now()
    session.merge(task_instance)
    session.commit()


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
    sub_list_length = len(sub_list)
    main_list_length = len(main_list)

    if sub_list_length > main_list_length:
        return False

    for i in range(sub_list_length):
        if sub_list[i] != main_list[i]:
            return False

    return True


def update_task_instances_to_kill(new_task_instance_to_kill, outdated_task_instances_to_kill):
    """
    Creates an updated list of task instances to kill, given a new instance and an outdated list. This function assumes
    two things: All the task instances to kill have the same execution date, and none of the instances in the outdated
    list have an ancestor-descendant relation (i.e. if one of the instances is a sub-DAG, none of its descendants are
    present in the list). This is because killing a sub-DAG operator instance with a certain execution date kills all of
    its descendants as well. These invariants apply also to the updated list returned - There are no ancestor-descendant
    task instances in the list.
    :param new_task_instance_to_kill: Should look like this:
        {
            'full_task_id': [..., <grandparent_dag_id>, <parent_dag_id>, <task_id>],
            'pid': <task_instance_pid>
        }
    :param outdated_task_instances_to_kill: An outdated list of task instances, each one should look as described above.
    :return: An updated list of task instances to kill.
    """
    full_task_id = new_task_instance_to_kill['full_task_id']
    new_task_instance_to_kill_appended = False
    updated_task_instances_to_kill = []

    for outdated_task_instance_to_kill in outdated_task_instances_to_kill:
        if is_prefix_sub_list(full_task_id, outdated_task_instance_to_kill['full_task_id']):
            if not new_task_instance_to_kill_appended:
                updated_task_instances_to_kill.append(new_task_instance_to_kill)
                new_task_instance_to_kill_appended = True
        elif is_prefix_sub_list(outdated_task_instance_to_kill['full_task_id'], full_task_id):
            updated_task_instances_to_kill.append(outdated_task_instance_to_kill)
        else:
            if not new_task_instance_to_kill_appended:
                updated_task_instances_to_kill.append(new_task_instance_to_kill)
                new_task_instance_to_kill_appended = True

            updated_task_instances_to_kill.append(outdated_task_instance_to_kill)

    return updated_task_instances_to_kill if len(updated_task_instances_to_kill) > 0 else [new_task_instance_to_kill]


def kill_task_instances(execution_date_to_task_instances_to_kill_dictionary):
    """
    Kills all the task instances contained in the given dictionary.
    Each execution date (key) is mapped to a list of its task instances that should be killed (value).
    Each task instance should look like this:
        {
            'full_task_id': [..., <grandparent_dag_id>, <parent_dag_id>, <task_id>],
            'pid': <task_instance_pid>
        }
    :param execution_date_to_task_instances_to_kill_dictionary: The dictionary containing the task instances to kill.
    :return: None.
    """
    for execution_date, task_instances_to_kill in execution_date_to_task_instances_to_kill_dictionary.iteritems():
        for task_instance_to_kill in task_instances_to_kill:
            full_task_id = '.'.join(task_instance_to_kill['full_task_id'])
            pid = str(task_instance_to_kill['pid'])
            logging.info(
                "Killing task instance stuck in up for retry. execution_date = {}, full_task_id = {}, pid = {}."
                .format(execution_date, full_task_id, pid)
            )
            subprocess.Popen(['kill', pid])


def finish_zombie_dag_instances():
    for running_dag_instance in find_dag_instances(first=False, state=State.RUNNING):
        task_instance_dates = []
        has_unfinished_task_instances = False
        expected_dag_instance_state = State.SUCCESS

        for task_instance in running_dag_instance.get_task_instances():
            task_instance_dates.extend([task_instance.start_date, task_instance.end_date])
            task_instance_state = task_instance.state

            if task_instance_state in unfinished_states:
                has_unfinished_task_instances = True
            elif task_instance_state in failed_states:
                expected_dag_instance_state = State.FAILED

        if (not has_unfinished_task_instances) and (delta_passed(delta_from_max_date, task_instance_dates)):
            msg = "Updating zombie DAG instance state: dag_id = {}, execution_date = {}, state = {}."\
                .format(running_dag_instance.dag_id, running_dag_instance.execution_date, expected_dag_instance_state)
            logging.info(msg)
            update_dag_instance_state(running_dag_instance, expected_dag_instance_state)


def finish_zombie_sub_dag_operator_instances():
    running_sub_dag_operator_instances = find_task_instances(
        first=False, state=State.RUNNING, operator=SubDagOperator.__name__
    )

    for running_sub_dag_operator_instance in running_sub_dag_operator_instances:
        task_id = running_sub_dag_operator_instance.task_id
        dag_id = running_sub_dag_operator_instance.dag_id + "." + task_id
        execution_date = running_sub_dag_operator_instance.execution_date
        dag_instance = find_dag_instances(first=True, dag_id=dag_id, execution_date=execution_date)

        if dag_instance:
            dag_instance_state = dag_instance.state
            dag_instance_dates = [dag_instance.start_date, dag_instance.end_date]

            if (dag_instance_state != State.RUNNING) and (delta_passed(delta_from_max_date, dag_instance_dates)):
                msg = "Updating zombie sub-DAG operator instance state: task_id = {}, execution_date = {}, state = {}."\
                    .format(task_id, execution_date, dag_instance_state)
                logging.info(msg)
                update_task_instance_state(running_sub_dag_operator_instance, dag_instance_state)


def kill_task_instances_stuck_in_up_for_retry():
    max_datetime_to_mark_as_stuck = datetime.now() - delta_to_mark_as_stuck
    execution_date_to_task_instances_to_kill_dictionary = {}

    for task_instance_in_up_for_retry in find_task_instances(first=False, state=State.UP_FOR_RETRY):
        if task_instance_in_up_for_retry.end_date <= max_datetime_to_mark_as_stuck:
            full_task_id = task_instance_in_up_for_retry.dag_id.split('.')
            full_task_id.append(task_instance_in_up_for_retry.task_id)
            execution_date = task_instance_in_up_for_retry.execution_date

            new_task_instance_to_kill = {
                'full_task_id': full_task_id,
                'pid': task_instance_in_up_for_retry.pid
            }

            execution_date_to_task_instances_to_kill_dictionary[execution_date] = update_task_instances_to_kill(
                new_task_instance_to_kill,
                execution_date_to_task_instances_to_kill_dictionary.get(execution_date, [])
            )

    kill_task_instances(execution_date_to_task_instances_to_kill_dictionary)


airflow_zombie_killer = DAG(
    dag_id="airflow_zombie_killer",
    schedule_interval=timedelta(minutes=30),
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

stuck_in_up_for_retry_task_instance_killer = PythonOperator(
    task_id="stuck_in_up_for_retry_task_instance_killer",
    python_callable=kill_task_instances_stuck_in_up_for_retry,
    dag=airflow_zombie_killer
)

zombie_dag_instance_killer >> zombie_sub_dag_operator_instance_killer
zombie_sub_dag_operator_instance_killer >> stuck_in_up_for_retry_task_instance_killer
