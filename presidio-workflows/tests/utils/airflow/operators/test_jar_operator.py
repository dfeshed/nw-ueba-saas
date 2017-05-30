import logging
import os
from datetime import datetime, timedelta
import pytest
from airflow import DAG
from presidio.utils.airflow.operators.jar_operator import JarOperator
from tests.utils.airflow.operators.base_test_operator import assert_task_success_state, get_task_instances

DEFAULT_DATE = datetime(2014, 1, 1)
# In order to run test locally change the path to:
# '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar'
JAR_PATH = '/home/presidio/jenkins/workspace/Presidio-Workflows/presidio-workflows/tests/resources/jars/test.jar'
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


@pytest.fixture
def java_args():
    return {
        'a': 'one',
        'b': 'two'
    }


def build_and_run_task(jvm_args, dag, java_args, expected_bash_comment):
    """
    Create and run the task 
    :param jvm_args: 
    :param dag: 
    :param java_args: 
    :param expected_bash_comment: 
    :return: 
    """
    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.clear()
    task.run(start_date=DEFAULT_DATE, end_date=DEFAULT_DATE)
    task_instances = get_task_instances(dag)

    assert_bash_comment(task, expected_bash_comment)
    assert_task_success_state(task_instances, task.task_id)


def assert_bash_comment(task, expected_bash_comment):
    """
    Checks whether jar operator build expected_bash_comment 
    :param task: 
    :param expected_bash_comment: 
    :return: 
    """
    assert task.bash_command == expected_bash_comment


def test_jvm_memory_allocation(default_args, java_args):
    """

    Test xmx and xmx
    :param default_args: default_args to dag
    :type default_args: dict
    :return: 
    """
    logging.info('Test xms and xmx options:')
    jvm_args = {
        'xms': 500,
        'xmx': 2050,
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_jvm_memory_allocation_dag", default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms500m -Xmx2050m -Duser.timezone=UTC -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


def test_timezone(default_args, java_args):
    """

    Test timezone
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('Test timezone:')
    jvm_args = {
        'timezone': '-Duser.timezone=America/New_York',
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_timezone", default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=America/New_York -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


def test_logback(default_args, java_args):
    """

    Test logback
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('Test logback:')
    jvm_args = {
        'java_overriding_logback_conf_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/xmls/test_logback.xml',
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_logback", default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC -Dlogback.configurationFile=/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/xmls/test_logback.xml -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


def test_remote_debug(default_args, java_args):
    """

    Test remote debug
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test remote debug:')
    jvm_args = {
        'remote_debug_enabled': True,
        'remote_debug_port': 9200,
        'remote_debug_suspend': False,
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        "test_remote_debug", default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC -agentlib:jdwp=transport=dt_socket,address=9200,server=y,suspend=n -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


def test_jar_path(default_args, java_args):
    """

    Test jar path
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test jar path:')
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }
    dag = DAG(
        "test_jar_path", default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


def test_all_params(default_args, java_args):
    """

    Test task with all options of jvm_args
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test jar operator with all params:')
    jvm_args = {
        'xms': 101,
        'xmx': 2049,
        'remote_debug_enabled': True,
        'remote_debug_port': 9200,
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
        'java_overriding_logback_conf_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/xmls/test_logback.xml',
        'java_path': '/usr/bin/java',
        'timezone': '-Duser.timezone=America/New_York',
        'remote_debug_suspend': False,
        'jmx_enabled': False,
        'jmx_port': 9302,
    }
    dag = DAG(
        "test_all_params", default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms101m -Xmx2049m -Duser.timezone=America/New_York -Dlogback.configurationFile=/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/xmls/test_logback.xml -agentlib:jdwp=transport=dt_socket,address=9200,server=y,suspend=n -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


@pytest.mark.skipif(os.geteuid() != 0, reason="The test should to be skipped if user is not root")
def test_jmx(default_args, java_args):
    """

    The test should to be skipped if user is not root, jmxremote.password file readable only for root.

    Test remote jmx
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test jmx:')
    jvm_args = {
        'jmx_enabled': True,
        'jmx_port': 9302,
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        'test_jmx_dag', default_args=default_args, schedule_interval=timedelta(1))

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC -Djavax.management.builder.initial= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9302 -cp ' + JAR_PATH + ' HelloWorld.Main a=one b=two'
    build_and_run_task(jvm_args, dag, java_args, expected_bash_comment)


def test_no_main_class(default_args, java_args):
    """

    Test task without main_class parameter
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test jar operator without main class:')
    jvm_args = {
        'jar_path': JAR_PATH,
    }

    dag = DAG(
        'test_main_class_missing_dag', default_args=default_args, schedule_interval=timedelta(1))

    with pytest.raises(Exception, message="Expecting error of jar operator"):
        build_and_run_task(jvm_args, dag, java_args, '')


def test_no_params(default_args):
    """

    Test expecting error when no options of jvm_args passed
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test jar operator without params:')
    dag = DAG('test_no_params_dag', default_args=default_args)

    with pytest.raises(Exception, message="Expecting error of jar operator"):
        build_and_run_task({}, dag, {}, '')


def test_no_jar(default_args, java_args):
    """

    Test task with no jar file
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test jar operator without jar file:')
    jvm_args = {
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/no_jar.jar',
        'main_class': MAIN_CLASS,
    }

    dag = DAG(
        'test_no_jar_dag', default_args=default_args, schedule_interval=timedelta(1))

    with pytest.raises(Exception, message="Expecting error of bash operator"):
        build_and_run_task(jvm_args, dag, java_args, '')


def test_update_java_args(default_args, java_args):
    """

    Test update of java args
    :param default_args: default_args to dag
    :type default_args: dict
    :return:
    """
    logging.info('test update of java args:')
    jvm_args = {
        'jar_path': JAR_PATH,
        'main_class': MAIN_CLASS,
    }
    dag = DAG(
        "test_java_args_update", default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    args = {
        'c': 'three',
    }

    task.update_java_args(args)

    task.clear()
    task.run(start_date=DEFAULT_DATE, end_date=DEFAULT_DATE)
    tis = get_task_instances(dag)

    expected_bash_comment = '/usr/bin/java -Xms100m -Xmx2048m -Duser.timezone=UTC -cp ' + JAR_PATH + ' HelloWorld.Main a=one c=three b=two'
    assert_bash_comment(task, expected_bash_comment)
    assert_task_success_state(tis, 'run_jar_file')
