import logging
from copy import copy

from airflow import configuration
from airflow.bin import cli
from airflow.models import DagRun, DAG, DagModel
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.utils import helpers
from airflow.utils.db import provide_session
from airflow.utils.state import State

from presidio.builders.elasticsearch.elasticsearch_operator_builder import build_clean_elasticsearch_data_operator
from presidio.builders.presidioconfiguration.presidio_configuration_operator_builder import \
    build_reset_presidio_configuration_operator
from presidio.utils.airflow.operators import spring_boot_jar_operator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

TASK_KILL_TIMEOUT = 60


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
        config_reader = ConfigServerConfigurationReaderSingleton().config_reader

        dag_models = get_dag_models_by_prefix("full_flow")
        dag_ids_to_clean = map(lambda x: x.dag_id, dag_models)

        pause_dags_operator = build_pause_dags_operator(dag, dag_models)

        kill_dags_task_instances_operator = build_kill_dags_task_instances_operator(dag, dag_ids_to_clean)

        clean_mongo_operator = build_mongo_clean_bash_operator(config_reader, dag, is_remove_ca_tables)

        clean_elasticsearch_data_operator = build_clean_elasticsearch_data_operator(dag)

        clean_adapter_operator = build_clean_adapter_operator(dag, is_remove_ca_tables)

        reset_presidio_configuration_operator = build_reset_presidio_configuration_operator(dag)

        clean_dags_from_db_operator = build_clean_dags_from_db_operator(dag, dag_ids_to_clean)

        clean_logs_operator = build_clean_logs_operator(dag)

        pause_dags_operator >> kill_dags_task_instances_operator
        kill_dags_task_instances_operator >> clean_mongo_operator
        kill_dags_task_instances_operator >> clean_elasticsearch_data_operator
        kill_dags_task_instances_operator >> clean_adapter_operator
        kill_dags_task_instances_operator >> clean_logs_operator
        clean_mongo_operator >> reset_presidio_configuration_operator
        clean_elasticsearch_data_operator >> reset_presidio_configuration_operator
        clean_adapter_operator >> reset_presidio_configuration_operator
        clean_logs_operator >> reset_presidio_configuration_operator
        reset_presidio_configuration_operator >> clean_dags_from_db_operator

        logging.debug("Finished creating dag - %s", dag.dag_id)

        return dag


@provide_session
def find_non_subdag_dags(session=None):
    DM = DagModel

    qry = session.query(DM)
    qry = qry.filter(DM.is_subdag == False)
    try:
        return qry.all()
    except:
        return None


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
        logging.info("killing pid {} task {} execution_date {} dagId {}".format(pid, task_instance.task_id,
                                                                                task_instance.execution_date,
                                                                                task_instance.dag_id))
        try:
            helpers.kill_process_tree(logger=logging.getLogger(), pid=pid, timeout=TASK_KILL_TIMEOUT)
        except Exception as e:
            logging.exception("failed to kill pid: {} ".format(pid))


def kill_dags_task_instances(dag_ids):
    for dag_id in dag_ids:
        dag_runs = get_dag_active_dag_runs(dag_id=dag_id)
        for dag_run in dag_runs:
            stop_kill_dag_run_task_instances(dag_run=dag_run)


@provide_session
def cleanup_dags_from_postgres(prefix_dag_ids, session=None):
    """
    :param prefix_dag_ids: dag id's to be cleaned from airflow db
    :type prefix_dag_ids: list[str]
    """
    for t in ["xcom", "task_instance", "sla_miss", "log", "job", "dag_run", "dag"]:
        for prefix_dag_id in prefix_dag_ids:
            query = session.query(DagModel).filter(DagModel.dag_id.like(prefix_dag_id), DagModel.is_paused == True)
            logging.info("query: %s", query)
            dag = query.first()
            logging.info("dag_id: %s", dag.dag_id)

            sql = "DELETE FROM {} WHERE dag_id=\'{}\'".format(t, dag.dag_id)
            logging.info("executing: %s", sql)
            session.execute(sql)

    sql = "DELETE FROM variable WHERE key LIKE \'{}%\'".format(spring_boot_jar_operator.RETRY_STATE_KEY_PREFIX)
    logging.info("executing: %s", sql)
    session.execute(sql)


def build_pause_dags_operator(cleanup_dag, dag_models):
    pause_dags_operator = PythonOperator(task_id='pause_dags',
                                         python_callable=pause_dags,
                                         op_kwargs={'dags': dag_models},
                                         dag=cleanup_dag)
    return pause_dags_operator


def build_clean_logs_operator(cleanup_dag):
    airflow_base_log_folder = str(configuration.get('core', 'BASE_LOG_FOLDER'))
    clean_logs_operator = BashOperator(task_id='clean_logs',
                                       bash_command="rm -rf {}/full_flow_* && rm -rf {}/logs/scheduler/ ".format(
                                           airflow_base_log_folder, airflow_base_log_folder),
                                       dag=cleanup_dag, retries=5)
    return clean_logs_operator


def build_clean_adapter_operator(cleanup_dag, is_remove_ca_tables):
    adapter_clean_bash_command = "rm -f /var/lib/netwitness/presidio/flume/conf/adapter/file_*" \
                                 " && rm -f /var/lib/netwitness/presidio/flume/conf/adapter/authentication_*" \
                                 " && rm -f /var/lib/netwitness/presidio/flume/conf/adapter/active_directory_*"

    clean_adapter_operator = BashOperator(task_id='clean_adapter',
                                          bash_command=adapter_clean_bash_command,
                                          dag=cleanup_dag)
    return clean_adapter_operator


def build_clean_dags_from_db_operator(cleanup_dag, dag_ids_to_clean):
    clean_dags_from_db_operator = PythonOperator(task_id='cleanup_dags_from_postgress',
                                                 python_callable=cleanup_dags_from_postgres,
                                                 op_kwargs={'prefix_dag_ids': copy(dag_ids_to_clean)},
                                                 dag=cleanup_dag)
    return clean_dags_from_db_operator


def build_kill_dags_task_instances_operator(cleanup_dag, dag_ids_to_clean):
    kill_dags_task_instances_operator = PythonOperator(task_id='kill_dags_task_instances',
                                                       python_callable=kill_dags_task_instances,
                                                       op_kwargs={'dag_ids': copy(dag_ids_to_clean)},
                                                       dag=cleanup_dag)
    return kill_dags_task_instances_operator


def build_mongo_clean_bash_operator(config_reader, cleanup_dag, is_remove_ca_tables):
    encpass = config_reader.read(conf_key="mongo.db.password")
    # build the mongo clean bash command
    mongo_clean_bash_command = "MONGO_PASS=$(java -jar /var/lib/netwitness/presidio/install/configserver/EncryptionUtils.jar decrypt {0})".format(
        encpass) + "&& mongo presidio -u presidio -p $MONGO_PASS --eval \"db.getCollectionNames().forEach(function(t){if (0==t.startsWith('system')) {print('dropping: ' +t); db.getCollection(t).drop();}});\""
    clean_mongo_operator = BashOperator(task_id='clean_mongo',
                                        bash_command=mongo_clean_bash_command,
                                        dag=cleanup_dag)
    return clean_mongo_operator
