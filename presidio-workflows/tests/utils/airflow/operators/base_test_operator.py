from airflow.models import TaskInstance
from airflow.settings import Session
from airflow.utils.state import State


def get_task_instances(dag):
    """

    Get_task_instances according to dag_id
    :param dag:
    :return: tis
    """
    session = Session()
    task_instances = session.query(TaskInstance).filter(
        TaskInstance.dag_id == dag.dag_id,
    )
    session.close()
    return task_instances


def assert_task_success_state(task_instances, task_id):
    """

    Assert task state
    :param task_instances: task instances
    :param task_id:
    :return:
    """
    for task_instance in task_instances:
        if task_instance.task_id == task_id:
            assert task_instance.state == State.SUCCESS
        else:
            raise

