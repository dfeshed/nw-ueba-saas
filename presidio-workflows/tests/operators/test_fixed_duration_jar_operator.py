import logging
from datetime import datetime, timedelta

import pytest
from airflow import DAG

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_reader_test_builder import ConfigServerConfigurationReaderTestBuilder
from tests.utils.airflow.operators.base_test_operator import assert_task_success_state, get_task_instances
from tests.utils.airflow.operators.test_spring_boot_jar_operator import EXPECTED_ADD_OPENS_JVM_OPTIONS
from tests.utils.airflow.operators.test_spring_boot_jar_operator import JAR_PATH, MAIN_CLASS, LAUNCHER
from tests.utils.airflow.operators.test_spring_boot_jar_operator import assert_bash_comment

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)
COMMAND = 'run'


class TestFixedDurationJarOperator(object):
    @classmethod
    def setup_class(cls):
        ConfigServerConfigurationReaderTestBuilder().build()

    def test_invalid_execution_date(self):
        logging.info('Test invalid execution date')
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

        jvm_args = {'jar_path': JAR_PATH, 'main_class': MAIN_CLASS}
        dag = DAG("test_skipped_state", default_args=default_args, schedule_interval=timedelta(minutes=5))
        java_args = {'a': 'one', 'b': 'two'}

        task = FixedDurationJarOperator(
            task_id='fixed_duration_operator',
            jvm_args=jvm_args,
            java_args=java_args,
            command=COMMAND,
            fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
            dag=dag,
            condition=lambda context: True
        )

        task.clear()

        with pytest.raises(Exception):
            task.run(start_date=default, end_date=default)

    def test_valid_execution_date(self):
        logging.info('Test valid execution date')
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
        jvm_args = {'jar_path': JAR_PATH, 'main_class': MAIN_CLASS}
        dag = DAG("test_success_state", default_args=default_args, schedule_interval=timedelta(minutes=5))
        java_args = {'a': 'one', 'b': 'two'}

        task = FixedDurationJarOperator(
            task_id='fixed_duration_operator',
            jvm_args=jvm_args,
            java_args=java_args,
            command=COMMAND,
            fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
            dag=dag,
            condition=lambda context: True
        )

        task.clear()
        dummy_task_instance = DummyTaskInstance(dag_id=dag.dag_id, task_id=task.task_id, execution_date=default)
        task.execute(context={'execution_date': default, 'task_instance': dummy_task_instance})
        tis = get_task_instances(dag)
        assert_task_success_state(tis, task.task_id)

        bash_command = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC {} -cp {} -Dloader.main={} {}'.format(
            EXPECTED_ADD_OPENS_JVM_OPTIONS,
            JAR_PATH,
            MAIN_CLASS,
            LAUNCHER
        )

        java_args = {
            'a': 'one',
            'b': 'two',
            'fixed_duration_strategy': '3600.0',
            'start_date': '2014-05-13T13:00:00Z',
            'end_date': '2014-05-13T14:00:00Z'
        }

        assert_bash_comment(task, bash_command, java_args)


class DummyTaskInstance(object):
    def __init__(self, dag_id, task_id, execution_date):
        self.dag_id = dag_id
        self.task_id = task_id
        self.execution_date = execution_date
