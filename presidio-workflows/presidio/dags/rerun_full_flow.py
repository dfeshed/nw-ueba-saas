import logging
import signal
from copy import copy
from datetime import datetime

import os
from airflow.bin import cli
from airflow.models import DagRun, DAG, DagModel
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State


@provide_session
def find_non_subdag_dags(session=None):
    DM = DagModel

    qry = session.query(DM)
    qry = qry.filter(DM.is_subdag == False)

    return qry.all()


def get_dag_modelss_by_prefix(dag_id_prefix):
    """

    :return: dict of DAGs that are not a sub DAG (can be found in DAG's folder) and has dag_id by prefix given
    :rtype: dict[str,DAG]
    """
    dag_models = find_non_subdag_dags()

    dag_models_by_prefix = [x for x in dag_models if x.dag_id.startswith(dag_id_prefix)]

    return dag_models_by_prefix


def pause_dags(dags):
    """

    :param dags: list of dag_ids to be paused 
    :type dags: list[str]
    """
    for dag in dags:
        pause_dag(dag)


def pause_dag(dag_id):
    """

    :param dag_id: single dag id 
    :type dag_id: str
    """
    cli.pause(dag=dag_id, args=None)


def get_dag_active_dag_runs(dag_id):
    """

    :type dag_id: str
    :rtype: list[DagRun]
    """
    return DagRun.find(state=State.RUNNING, dag_id=dag_id)


def stop_kill_dag_run_task_instances(dag_run):
    """

    :param dag_run: dag run to have it's tasks killed 
    :type dag_run: DagRun
    """
    task_instances = dag_run.get_task_instances(state=State.RUNNING)
    for task_instance in task_instances:
        pid = task_instance.pid
        logging.info("killing pid {}".format(pid))
        os.kill(int(pid), signal.SIGTERM)


def kill_dags_task_instances(dag_ids):
    for dag_id in dag_ids:
        dag_runs = get_dag_active_dag_runs(dag_id=dag_id)
        for dag_run in dag_runs:
            stop_kill_dag_run_task_instances(dag_run=dag_run)

@provide_session
def cleanup_dags_from_postgres(dag_ids,session=None):
    """

    :param dag_ids: dag id's to be cleaned from airflow db
    :type dag_ids: list[str]
    """
    for t in ["xcom", "task_instance", "sla_miss", "log", "job", "dag_run", "dag"]:
        for dag_id in dag_ids:
            sql = "DELETE FROM {} WHERE dag_id LIKE \'%{}%\'".format(t, dag_id)
            logging.info("executing: %s",sql)
            session.execute(sql)

dag_models = get_dag_modelss_by_prefix("full_flow")
dag_ids_to_clean = map(lambda x: x.dag_id, dag_models)

cleanup_dag = DAG(dag_id="rerun_full_flow", schedule_interval=None, start_date=datetime(2015, 6, 1))
pause_dags_operator = PythonOperator(task_id='pause_dags',
                                     python_callable=pause_dags,
                                     op_kwargs={'dags': dag_models},
                                     dag=cleanup_dag)
kill_dags_task_instances_operator = PythonOperator(task_id='kill_dags_task_instances',
                                                   python_callable=kill_dags_task_instances,
                                                   op_kwargs={'dag_ids': copy(dag_ids_to_clean)},
                                                   dag=cleanup_dag)

clean_dags_from_db_operator = PythonOperator(task_id='cleanup_dags_from_postgress',
                                             python_callable=cleanup_dags_from_postgres,
                                             op_kwargs={'dag_ids': copy(dag_ids_to_clean)},
                                             dag=cleanup_dag)


clean_mongo_operator = BashOperator(task_id='clean_mongo',
                                    bash_command="mongo presidio -u presidio -p P@ssw0rd --eval \"db.getCollectionNames().forEach(function(t){if (0==t.startsWith('ca_')&&0==t.startsWith('system'))  {print('dropping: ' +t); db.getCollection(t).drop();}});\"",
                                    dag=cleanup_dag)

clean_elastic_operator = BashOperator(task_id='clean_elastic', bash_command="curl -X DELETE http://localhost:9200/_all",
                                      dag=cleanup_dag)

clean_adapter_operator = BashOperator(task_id='clean_adapter',
                                      bash_command="rm -rf /data/presidio/3p/flume/checkpoint/adapter/ && rm -rf /data/presidio/3p/flume/data/adapter/",
                                      dag=cleanup_dag)

clean_logs_operator = BashOperator(task_id='clean_logs',
                                   bash_command="rm -rf /var/log/presidio/3p/airflow/full_flow_* && rm -rf /var/log/presidio/3p/airflow/logs/scheduler/ && rm -f /tmp/spring.log*",
                                   dag=cleanup_dag)

pause_dags_operator >> kill_dags_task_instances_operator
kill_dags_task_instances_operator >> clean_mongo_operator
kill_dags_task_instances_operator >> clean_elastic_operator
kill_dags_task_instances_operator >> clean_adapter_operator

clean_mongo_operator >> clean_dags_from_db_operator
clean_elastic_operator >> clean_dags_from_db_operator
clean_adapter_operator >> clean_dags_from_db_operator
clean_dags_from_db_operator >> clean_logs_operator


