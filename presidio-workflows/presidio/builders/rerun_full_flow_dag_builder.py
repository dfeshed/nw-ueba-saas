import logging
import os
import signal
from airflow.bin import cli
from airflow.models import DagRun, DAG, DagModel
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from copy import copy
from elasticsearch import Elasticsearch


class RerunFullFlowDagBuilder(object):
    """
    The "rerun full flow run" DAG consists of all the actions needed in order to delete all presidio data
    """

    @classmethod
    def build(cls, dag, is_remove_ca_tables):
        """
        Receives a rerun full flow DAG, creates the operators, links them to the DAG and
        configures the dependencies between them.
        :return: The DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        logging.debug("populating the rerun full flow dag")

        dag_models = get_dag_models_by_prefix("full_flow")
        dag_ids_to_clean = map(lambda x: x.dag_id, dag_models)

        pause_dags_operator = build_pause_dags_operator(dag, dag_models)

        kill_dags_task_instances_operator = build_kill_dags_task_instances_operator(dag, dag_ids_to_clean)

        clean_mongo_operator = build_mongo_clean_bash_operator(dag, is_remove_ca_tables)

        clean_elastic_operator = build_clean_elastic_operator(dag)

        clean_adapter_operator = build_clean_adapter_operator(dag, is_remove_ca_tables)

        clean_dags_from_db_operator = build_clean_dags_from_db_operator(dag, dag_ids_to_clean)

        clean_logs_operator = build_clean_logs_operator(dag)

        pause_dags_operator >> kill_dags_task_instances_operator
        kill_dags_task_instances_operator >> clean_mongo_operator
        kill_dags_task_instances_operator >> clean_elastic_operator
        kill_dags_task_instances_operator >> clean_adapter_operator

        clean_mongo_operator >> clean_dags_from_db_operator
        clean_elastic_operator >> clean_dags_from_db_operator
        clean_adapter_operator >> clean_dags_from_db_operator
        clean_dags_from_db_operator >> clean_logs_operator

        if is_remove_ca_tables:
            clean_collector_operator = build_clean_collector_operator(dag)
            kill_dags_task_instances_operator >> clean_collector_operator
            clean_collector_operator >> clean_dags_from_db_operator

        logging.debug("Finished creating dag - %s", dag.dag_id)

        return dag


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
    adapter_clean_bash_command = "rm -f /opt/flume/conf/adapter/file_*" \
                                 " && rm -f /opt/flume/conf/adapter/authentication_*" \
                                 " && rm -f /opt/flume/conf/adapter/active_directory_*"

    clean_adapter_operator = BashOperator(task_id='clean_adapter',
                                          bash_command=adapter_clean_bash_command,
                                          dag=cleanup_dag)
    return clean_adapter_operator


def build_clean_collector_operator(cleanup_dag):
    collector_stop_service_command = "sudo systemctl stop presidio-collector %s"

    collector_clean_bash_command = "&& rm -rf /opt/flume/data/collector/*" \
                                   " && rm -rf /data/presidio/3p/flume/checkpoint/collector/file/*" \
                                   " && rm -rf /data/presidio/3p/flume/checkpoint/collector/authentication/*" \
                                   " && rm -rf /data/presidio/3p/flume/checkpoint/collector/active_directory/*" \
                                   " && rm -rf /data/presidio/3p/flume/checkpoint/collector/default/*" \
                                   " && rm -rf /data/presidio/3p/flume/data/collector/file/*" \
                                   " && rm -rf /data/presidio/3p/flume/data/collector/authentication/*" \
                                   " && rm -rf /data/presidio/3p/flume/data/collector/active_directory/*" \
                                   " && rm -rf /data/presidio/3p/flume/data/collector/default/*" \
                                   " && rm -rf $PRESIDIO_HOME/flume/counters/source %s"

    collector_start_service_command = "&& sudo systemctl start presidio-collector"

    clean_collector_operator = BashOperator(task_id='clean_collector',
                                            bash_command=collector_stop_service_command % collector_clean_bash_command % collector_start_service_command,
                                            dag=cleanup_dag)
    return clean_collector_operator


def clean_elastic_data():
    es = Elasticsearch(hosts=["localhost"])
    indexes = es.cat.indices(h="index").encode("utf-8").split("\n")

    for index in indexes:
        if not index.startswith(".") and not index == "": #escape system metrics
            if index.startswith(('presidio-monitoring', 'metricbeat', 'packetbeat')):
                es.indices.delete(index=index, ignore=[404], request_timeout=360)
            else:
                es.delete_by_query(index=index, body="{\"query\": {\"match_all\": {}}}", request_timeout=360)


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


def build_mongo_clean_bash_operator(cleanup_dag, is_remove_ca_tables):
    # build the mongo clean bash command
    mongo_clean_bash_command = "mongo presidio -u presidio -p P@ssw0rd --eval \"db.getCollectionNames().forEach(function(t){if (0==t.startsWith('system') %s)  {print('dropping: ' +t); db.getCollection(t).drop();}});\""
    if not is_remove_ca_tables:
        # we want to keep the ca tables
        mongo_clean_bash_command = mongo_clean_bash_command % "&& 0==t.startsWith('ca_')"
    else:
        mongo_clean_bash_command = mongo_clean_bash_command % ""
    clean_mongo_operator = BashOperator(task_id='clean_mongo',
                                        bash_command=mongo_clean_bash_command,
                                        dag=cleanup_dag)
    return clean_mongo_operator
