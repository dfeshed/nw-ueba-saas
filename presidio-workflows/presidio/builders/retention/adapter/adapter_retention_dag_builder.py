import logging
from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class AdapterRetentionDagBuilder(PresidioDagBuilder):
    ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'
    RETENTION_COMMAND_CONFIG_PATH = 'retention.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self.data_sources = data_sources
        self._retention_command = conf_reader.read(AdapterRetentionDagBuilder.RETENTION_COMMAND_CONFIG_PATH,
                                                   AdapterRetentionDagBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.jvm_args = AdapterRetentionDagBuilder.conf_reader.read(
            conf_key=AdapterRetentionDagBuilder.ADAPTER_JVM_ARGS_CONFIG_PATH)

    def build(self, adapter_retention_dag):
        """
        Builds jar operators that do retention for each data source and adds them to the given DAG.
        :param adapter_retention_dag: The DAG to which all relevant retention operators should be added
        :type adapter_retention_dag: airflow.models.DAG
        :return: The input DAG, after the retention operators were added
        :rtype: airflow.models.DAG
        """

        logging.debug("populating the retention dag, dag_id=%s ", adapter_retention_dag.dag_id)

        # Iterate all configured data sources
        for data_source in self.data_sources:
            java_args = {
                'schema': data_source,
                'command': self._retention_command
            }

            jar_operator = FixedDurationJarOperator(
                task_id='retention_adapter_{}'.format(data_source),
                fixed_duration_strategy=timedelta(hours=1),
                command=PresidioDagBuilder.presidio_command,
                jvm_args=self.jvm_args,
                java_args=java_args,
                run_clean_command_before_retry=False,
                dag=adapter_retention_dag)

            jar_operator

        return adapter_retention_dag
