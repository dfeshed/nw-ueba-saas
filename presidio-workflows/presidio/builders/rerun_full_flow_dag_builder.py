import logging
import os
import signal
import shutil
from airflow.bin import cli
from airflow.models import DagRun, DAG, DagModel
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from copy import copy
from datetime import datetime
from elasticsearch import Elasticsearch
from pymongo import MongoClient


class RerunFullFlowDagBuilder():
    """
    The "rerun full flow run" DAG consists of all the actions needed in order to delete all presidio data
    """
CHECKPOINT_ADAPTER_PATH = "/data/presidio/3p/flume/checkpoint/adapter/"
DATA_ADAPTER_PATH = "/data/presidio/3p/flume/data/adapter/"
FLUME_PATH = "$PRESIDIO_HOME/flume/counters/source"

@provide_session
def find_non_subdag_dags(session=None):
    DM = DagModel

    qry = session.query(DM)
    qry = qry.filter(DM.is_subdag == False)

    return qry.all()


def get_dag_models_by_prefix(dag_id_prefix):
    """

    :return: dict of DAGs that are not a sub DAG (can be found in DAG's folder) and has dag_id by prefix given
    :rtype: dict[str,DAG]
    """
    dag_models = find_non_subdag_dags()

    dag_models_by_prefix = [x for x in dag_models if x.dag_id.startswith(dag_id_prefix)]

    return dag_models_by_prefix


def pause_dag(dag_id):
    """

    :param dag_id: single dag id
    :type dag_id: str
    """
    cli.pause(dag=dag_id, args=None)


def pause_dags(dags):
    """

    :param dags: list of dag_ids to be paused
    :type dags: list[str]
    """
    for dag in dags:
        pause_dag(dag)


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

def mongo_data_clean(delete_ca):
    client = MongoClient("localhost", 27017)
    db = client.presidio
    for collection in db.collection_names():
        if not collection.startswith('system'):
            collection.drop()
        if delete_ca :
            if collection.startswith('ca_'):
                collection.drop()


def clean_adapter(ds, **kwargs):

    delete_ca = kwargs['dag_run'].conf['message']
    shutil.rmtree(CHECKPOINT_ADAPTER_PATH)
    shutil.rmtree(DATA_ADAPTER_PATH)
    if delete_ca :
        shutil.rmtree(FLUME_PATH)






@provide_session
def cleanup_dags_from_postgres(dag_ids, session=None):
    """
    :param dag_ids: dag id's to be cleaned from airflow db
    :type dag_ids: list[str]
    """
    for t in ["xcom", "task_instance", "sla_miss", "log", "job", "dag_run", "dag"]:
        for dag_id in dag_ids:
            sql = "DELETE FROM {} WHERE dag_id LIKE \'%{}%\'".format(t, dag_id)
            logging.info("executing: %s", sql)
            session.execute(sql)


def build(is_remove_ca_tables):
    """
    Receives a rerun full flow DAG, creates the operators, links them to the DAG and
    configures the dependencies between them.
    :return: The DAG, after it has been populated
    :rtype: airflow.models.DAG
    """

    logging.debug("populating the rerun full flow dag")

    dag_models = get_dag_models_by_prefix("full_flow")
    dag_ids_to_clean = map(lambda x: x.dag_id, dag_models)

    rerun_full_flow_dag = DAG(dag_id="rerun_full_flow", schedule_interval=None, start_date=datetime(2015, 6, 1))

    pause_dags_operator = build_pause_dags_operator(rerun_full_flow_dag, dag_models)

    kill_dags_task_instances_operator = build_kill_dags_task_instances_operator(rerun_full_flow_dag, dag_ids_to_clean)

    clean_mongo_operator = build_mongo_clean_task_instances_operator(rerun_full_flow_dag, is_remove_ca_tables)

    clean_elastic_operator = build_clean_elastic_operator(rerun_full_flow_dag)

    clean_adapter_operator = build_clean_adapter_operator(rerun_full_flow_dag)

    clean_dags_from_db_operator = build_clean_dags_from_db_operator(rerun_full_flow_dag, dag_ids_to_clean)

    clean_logs_operator = build_clean_logs_operator(rerun_full_flow_dag)

    pause_dags_operator >> kill_dags_task_instances_operator
    kill_dags_task_instances_operator >> clean_mongo_operator
    kill_dags_task_instances_operator >> clean_elastic_operator
    kill_dags_task_instances_operator >> clean_adapter_operator

    clean_mongo_operator >> clean_dags_from_db_operator
    clean_elastic_operator >> clean_dags_from_db_operator
    clean_adapter_operator >> clean_dags_from_db_operator
    clean_dags_from_db_operator >> clean_logs_operator

    logging.debug("Finished creating dag - %s", rerun_full_flow_dag.dag_id)

    return rerun_full_flow_dag


def build_pause_dags_operator(cleanup_dag, dag_models):
    pause_dags_operator = PythonOperator(task_id='pause_dags',
                                         python_callable=pause_dags,
                                         op_kwargs={'dags': dag_models},
                                         dag=cleanup_dag)
    return pause_dags_operator


def build_clean_logs_operator(cleanup_dag):
    clean_logs_operator = BashOperator(task_id='clean_logs',
                                       bash_command="rm -rf /var/log/presidio/3p/airflow/full_flow_* && rm -rf /var/log/presidio/3p/airflow/logs/scheduler/ && rm -f /tmp/spring.log*",
                                       dag=cleanup_dag)
    return clean_logs_operator


def build_clean_adapter_operator(cleanup_dag, is_remove_ca_tables):
    clean_adapter_operator = PythonOperator(task_id='clean_adapter',
                                          python_callable=clean_adapter,
                                          provide_context=True,
                                          dag=cleanup_dag)
    return clean_adapter_operator


def clean_elastic_data():
    es = Elasticsearch(hosts=["localhost"])
    indexes = es.cat.indices(h="index").encode("utf-8").split("\n")

    for index in indexes:
        if index not in [".kibana", ""]:
            es.delete_by_query(index=index, body="{\"query\": {\"match_all\": {}}}")


def build_clean_elastic_operator(cleanup_dag):
    clean_elastic_operator = PythonOperator(task_id='clean_elastic_data',
                                            python_callable=clean_elastic_data,
                                            dag=cleanup_dag)
    return clean_elastic_operator


def build_clean_dags_from_db_operator(cleanup_dag, dag_ids_to_clean):
    clean_dags_from_db_operator = PythonOperator(task_id='cleanup_dags_from_postgress',
                                                 python_callable=cleanup_dags_from_postgres,
                                                 op_kwargs={'dag_ids': copy(dag_ids_to_clean)},
                                                 dag=cleanup_dag)
    return clean_dags_from_db_operator


def build_kill_dags_task_instances_operator(cleanup_dag, dag_ids_to_clean):
    kill_dags_task_instances_operator = PythonOperator(task_id='kill_dags_task_instances',
                                                       python_callable=kill_dags_task_instances,
                                                       op_kwargs={'dag_ids': copy(dag_ids_to_clean)},
                                                       dag=cleanup_dag)
    return kill_dags_task_instances_operator


def build_mongo_clean_task_instances_operator(cleanup_dag, is_remove_ca_tables):
    clean_mongo_operator = PythonOperator(task_id='clean_mongo',
                                          python_callable=mongo_data_clean,
                                          op_kwargs={'delete_ca': is_remove_ca_tables},
                                          dag=cleanup_dag)
    return clean_mongo_operator
