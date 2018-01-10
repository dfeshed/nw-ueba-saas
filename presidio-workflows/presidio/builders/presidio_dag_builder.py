from abc import ABCMeta, abstractmethod

import logging
from datetime import timedelta

from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


RETRY_ARGS_CONF_KEY = "subdag_retry_args"
DAGS_CONF_KEY = "dags"


class PresidioDagBuilder(object):
    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    presidio_command = 'run'

    """
    The Presidio DAG Builder has 1 method that receives a DAG. The "build" method creates the DAG's operators, links
    them to the DAG and configures the dependencies between them. The inheritors are builders of different types of DAGs
    (Input DAG, ADE DAG, Output DAG, etc.), but they all have a common functional interface that receives and populates
    an Airflow DAG. Each implementation does this according to the specific DAG type.
    """

    __metaclass__ = ABCMeta

    @abstractmethod
    def build(self, dag):
        """
        Receives a DAG, creates its operators, links them to the DAG and configures the dependencies between them.
        :param dag: The DAG to populate
        :type dag: airflow.models.DAG
        :return: The given DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        pass

    def _create_sub_dag_operator(self, sub_dag_builder, sub_dag_id, dag):
        """
        create a sub dag of the received "dag" fill it with a flow using the sub_dag_builder
        and wrap it with a sub dag operator.
        :param sub_dag_builder: 
        :type sub_dag_builder: PresidioDagBuilder
        :param sub_dag_id:
        :type sub_dag_id: str
        :param dag: The ADE DAG to populate
        :type dag: airflow.models.DAG
        :return: The given ADE DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        sub_dag = DAG(
            dag_id='{}.{}'.format(dag.dag_id, sub_dag_id),
            schedule_interval=dag.schedule_interval,
            start_date=dag.start_date,
            default_args=dag.default_args
        )

        retry_args = self._calc_subdag_retry_args(sub_dag_id)

        return SubDagOperator(
            subdag=sub_dag_builder.build(sub_dag),
            task_id=sub_dag_id,
            dag=dag,
            retries=retry_args['retries'],
            retry_delay=timedelta(seconds=int(retry_args['retry_delay'])),
            retry_exponential_backoff=retry_args['retry_exponential_backoff'],
            max_retry_delay=timedelta(
                seconds=int(retry_args['max_retry_delay']))
        )

    @staticmethod
    def validate_the_gap_between_dag_start_date_and_current_execution_date(dag, gap, execution_date, schedule_interval):
        return (dag.start_date + gap) <= execution_date + schedule_interval

    def _calc_subdag_retry_args(self, task_id):
        retry_args = {}
        if task_id:
            # read task subdag retry args
            retry_args = PresidioDagBuilder.conf_reader.read(
                conf_key=self.get_retry_args_task_instance_conf_key_prefix(task_id))
        if not retry_args:
            # read default subdag retry args
            logging.debug((
                "did not found task retry configuration for operator=%s. settling for default configuration" % (
                    self.__class__.__name__)))
            retry_args = PresidioDagBuilder.conf_reader.read(
                conf_key=self.get_default_retry_args_conf_key())
        return retry_args

    def get_retry_args_task_instance_conf_key_prefix(self, task_id):
        return "%s.%s.%s" % (self.get_task_instance_conf_key_prefix(), task_id, RETRY_ARGS_CONF_KEY)

    def get_task_instance_conf_key_prefix(self):
        return "%s.tasks_instances" % (DAGS_CONF_KEY)

    def get_default_retry_args_conf_key(self):
        return "%s.operators.default_jar_values.%s" % (DAGS_CONF_KEY, RETRY_ARGS_CONF_KEY)
