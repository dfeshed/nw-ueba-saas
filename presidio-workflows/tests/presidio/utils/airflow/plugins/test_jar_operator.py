from airflow.operators import JarOperator
from datetime import datetime, timedelta
import pytest
from airflow import DAG
import logging

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


# Test task with all options
def test_jar_operator_all_params(default_args, java_args):
    logging.info('test jar operator with all params:')
    options = {
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
        options=options,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


# Test task with only mandatory options
def test_jar_operator_mandatory_params(default_args, java_args):
    logging.info('test jar operator with mandatory params:')
    options = {
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_mandatory_params_dag', default_args=default_args, schedule_interval=timedelta(1))

    task = JarOperator(
        task_id='run_jar_file',
        options=options,
        java_args=java_args,
        dag=dag)

    task.execute(context={})


# Test task without main_class parameter
def test_jar_operator_partial_params(default_args, java_args):
    logging.info('test jar operator with partial params:')
    options = {
        'jar_path': '//home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/test.jar',
    }

    dag = DAG(
        'test_mandatory_params_dag', default_args=default_args, schedule_interval=timedelta(1))

    with pytest.raises(Exception, message="Expecting error of jar operator"):
        task = JarOperator(
            task_id='run_jar_file',
            options=options,
            java_args=java_args,
            dag=dag)

        task.execute(context={})


# Test expecting error when no options passed
def test_jar_operator_no_params(default_args):
    logging.info('test jar operator without params:')
    dag = DAG('test_no_params_dag', default_args=default_args)

    with pytest.raises(Exception, message="Expecting error of jar operator"):
        task = JarOperator(
            task_id='run_jar_file',
            options={},
            java_args={},
            dag=dag)
        task.execute(context={})


# Test task with no jar file
def test_jar_operator_no_jar(default_args, java_args):
    logging.info('test jar operator without jar file:')
    options = {
        'jar_path': '/home/presidio/dev-projects/presidio-core/presidio-workflows/tests/resources/jars/no_jar.jar',
        'main_class': 'HelloWorld.Main',
    }

    dag = DAG(
        'test_no_jar_dag', default_args=default_args, schedule_interval=timedelta(1))

    with pytest.raises(Exception, message="Expecting error of bash operator"):
        task = JarOperator(
            task_id='run_jar_file',
            options=options,
            java_args=java_args,
            dag=dag)
        task.execute(context={})
