import logging
from datetime import datetime, timedelta
import pytest
from airflow import DAG
from presidio.operators.fixed_duration_operator import FixedDurationOperator
from tests.utils.airflow.operators.base_test_operator import assert_task_success_state, get_task_instances

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)

# In order to run test locally change the path to:
# '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar'
JAR_PATH = '/home/presidio/jenkins/workspace/Presidio-Workflows/presidio-workflows/tests/resources/jars/test.jar'
MAIN_CLASS = 'HelloWorld.Main'


def test_invalid_execution_date():
    """

    Test invalid execution date
    :return:
    """

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
    with pytest.raises(Exception):
        task.run(start_date=default, end_date=default)


def test_valid_execution_date():
    """

    Test valid execution date
    :return:
    """
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
    task.execute(context={'execution_date':default})
    tis = get_task_instances(dag)
    assert_task_success_state(tis, task.task_id)

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC -cp ' + JAR_PATH + ' HelloWorld.Main'
    expected_java_args = {'a': 'one', 'b': 'two', 'fixed_duration_strategy': '3600.0',
                          'start_date': '2014-05-13T13:00:00', 'end_date': '2014-05-13T14:00:00'}
    assert_bash_comment(task, expected_bash_comment, expected_java_args)


def assert_bash_comment(task, expected_bash_comment, expected_java_args={}):
    """
    Checks whether jar operator build expected_bash_comment 
    :param task: 
    :param expected_bash_comment: 
    :param expected_java_args: 
    :return: 
    """
    task_bash_command = task.bash_command
    main_class_index = task_bash_command.rfind(MAIN_CLASS) + len(MAIN_CLASS)
    bash_command = task.bash_command[:main_class_index]

    assert bash_command == expected_bash_comment

    args = task_bash_command[-(len(task_bash_command) - main_class_index):].strip()
    java_args_dict = {k: v.strip('"') for k, v in [i.split("=", 1) for i in args.split(" ")]}

    assert java_args_dict == expected_java_args
