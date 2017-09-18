import logging
from datetime import datetime, timedelta

from airflow import DAG

from presidio.builders.full_flow_dag_builder import FullFlowDagBuilder
from presidio.utils.configuration.config_server_reader_test_builder import ConfigServerConfigurationReaderTestBuilder

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)


def test_valid_build():
    """

    Test valid full flow dag build
    :return:
    """

    logging.info('Test valid full flow dag build')
    ConfigServerConfigurationReaderTestBuilder().build()

    default = datetime(2014, 5, 13, 23, 00, 0)

    default_args = {
        'owner': 'airflow',
        'depends_on_past': False,
        'start_date': default,
        'email': ['airflow@airflow.com'],
        'email_on_failure': False,
        'email_on_retry': False,
        'hourly_smart_events_confs': ['userId_hourly'],
        'daily_smart_events_confs': [],
        'data_sources': 'file,authentication,active_directory',
    }

    dag = DAG(
        "full_flow_test_dag", default_args=default_args, schedule_interval=timedelta(hours=1), start_date=default)

    dag = FullFlowDagBuilder().build(dag)

    assert_task_id_uniqueness(dag)

    assert dag.task_count == 4


def assert_task_id_uniqueness(dag):
    subdags = dag.subdags
    tasks_dict = {}

    # save task references from all the sub dags
    for subdag in subdags:
        for current_task in subdag.tasks:
            task_id = current_task.task_id
            if not tasks_dict.has_key(task_id):
                tasks_dict[task_id] = []
            tasks_dict[task_id].append(current_task)
    for key in tasks_dict:
        task_instances = tasks_dict.get(key)
        if len(task_instances) == 1:
            continue
        first_task = task_instances[0]
        for current_task in task_instances[1:]:
            assert first_task is current_task, "task=%s must have a unique id across the flow. found duplication: " \
                                               "\nfirst's task vars=\n%s \nsecond task vars=\n%s" % (
                                                   current_task.task_id, (vars(first_task)), (vars(current_task)))
