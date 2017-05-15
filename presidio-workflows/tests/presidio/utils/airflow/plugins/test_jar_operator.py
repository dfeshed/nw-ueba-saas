from presidio.operators.jar_operator import JarOperator
from datetime import datetime, timedelta
import pytest
import logging
from airflow import DAG
import os

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


@pytest.fixture
def java_args():
    return {
        'a': 'one',
        'b': 'two'
    }


def test_jvm_memory_allocation(default_args, java_args):
    """

    Test xmx and xmx
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return: 
    """
    logging.info('Test xms and xmx options:')
    jvm_args = {
        'xms': 500,
        'xmx': 2050,
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_jvm_memory_allocation_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


def test_timezone(default_args, java_args):
    """

    Test timezone
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return: 
    """
    logging.info('Test timezone:')
    jvm_args = {
        'timezone': '-Duser.timezone=Europe/Madrid',
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_timezone_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


def test_logback(default_args, java_args):
    """

    Test logback
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return: 
    """
    logging.info('Test logback:')
    jvm_args = {
        'java_overriding_logback_conf_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/xmls/test.xml',
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_logback_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


def test_remote_debug(default_args, java_args):
    """

    Test remote debug
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return: 
    """
    logging.info('test remote debug:')
    jvm_args = {
        'remote_debug_enabled': True,
        'remote_debug_port': 9200,
        'remote_debug_suspend': False,
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_remote_debug_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


@pytest.mark.skipif(os.geteuid() != 0,reason="The test should to be skipped if user is not root")
def test_jmx(default_args, java_args):
    """
    
    The test should to be skipped if user is not root, jmxremote.password file readable only for root.

    Test remote jmx
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args:
    :type java_args: dict
    :return:
    """
    logging.info('test jmx:')
    jvm_args = {
        'jmx_enabled': True,
        'jmx_port': 9302,
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_jmx_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


def test_jar_path(default_args, java_args):
    """

    Test jar path
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return: 
    """
    logging.info('test jar path:')
    jvm_args = {
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_jar_path_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


def test_all_params(default_args, java_args):
    """
    
    Test task with all options of jvm_args
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return: 
    """
    logging.info('test jar operator with all params:')
    jvm_args = {
        'xms': 101,
        'xmx': 2049,
        'remote_debug_enabled': True,
        'remote_debug_port': 9200,
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
        'java_overriding_logback_conf_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/xmls/test.xml',
        'java_path': '/usr/bin/java',
        'timezone': '-Duser.timezone=UTC',
        'remote_debug_suspend': False,
        'jmx_enabled': False,
        'jmx_port': 9302,
    }

    dag = DAG(
        'test_all_params_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        jvm_args=jvm_args,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


def test_main_class_missing(default_args, java_args):
    """

    Test task without main_class parameter
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return:
    """
    logging.info('test jar operator with partial params:')
    jvm_args = {
        'jar_path': '//home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
    }

    dag = DAG(
        'test_main_class_missing_dag', default_args=default_args, schedule_interval=timedelta(1))

    with pytest.raises(Exception, message="Expecting error of jar operator"):
        task = JarOperator(
            task_id='run_jar_file',
            jvm_args=jvm_args,
            java_args=java_args,
            dag=dag)

        task.execute(context={})


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
        task = JarOperator(
            task_id='run_jar_file',
            jvm_args={},
            java_args={},
            dag=dag)
        task.execute(context={})


def test_no_jar(default_args, java_args):
    """

    Test task with no jar file
    :param default_args: default_args to dag
    :type default_args: dict
    :param java_args: 
    :type java_args: dict
    :return:
    """
    logging.info('test jar operator without jar file:')
    jvm_args = {
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/no_jar.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_no_jar_dag', default_args=default_args, schedule_interval=timedelta(1))

    with pytest.raises(Exception, message="Expecting error of bash operator"):
        task = JarOperator(
            task_id='run_jar_file',
            jvm_args=jvm_args,
            java_args=java_args,
            dag=dag)
        task.execute(context={})
