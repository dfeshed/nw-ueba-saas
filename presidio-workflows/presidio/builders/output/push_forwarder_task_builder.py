import logging

from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

FORWARDER_JVM_ARGS_CONFIG_PATH = 'components.forwarder.jvm_args'


class PushForwarderTaskBuilder(object):

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self):

        config_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self.jvm_args = config_reader.read(conf_key=FORWARDER_JVM_ARGS_CONFIG_PATH)

    def build(self, presidio_core_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param presidio_core_dag: The DAG to which the task should be added
        :type presidio_core_dag: airflow.models.DAG
        :return: The created forwarder task
        :rtype: FixedDurationJarOperator
        """

        logging.debug("creating the forwarder task")

        # Create jar operator
        data_forwarding_operator = FixedDurationJarOperator(
            task_id='data_forwarding_operator',
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            dag=presidio_core_dag)

        return data_forwarding_operator
