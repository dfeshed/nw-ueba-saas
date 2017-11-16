import logging

from datetime import timedelta, datetime

import airflow

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio_extension.builders.adapter.adapter_dag_builder_extension import AdapterDagBuilderExtension

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class AdapterDagBuilder(PresidioDagBuilder):
    """
    A "Adapter DAG" builder - The "Adapter DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_server_reader

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """

        self.data_sources = data_sources
        self.jvm_args = AdapterDagBuilder.conf_reader.read(conf_key=ADAPTER_JVM_ARGS_CONFIG_PATH)

    def build(self, adapter_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param adapter_dag: The DAG to which all relevant "input" operators should be added
        :type adapter_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        logging.debug("populating the adapter dag, dag_id=%s ", adapter_dag.dag_id)

        # Iterate all configured data sources
        for data_source in self.data_sources:
            java_args = {
                'schema': data_source,
            }

            # Create jar operator for each data source
            jar_operator = FixedDurationJarOperator(
                task_id='adapter_{}'.format(data_source),
                fixed_duration_strategy=timedelta(hours=1),
                command=PresidioDagBuilder.presidio_command,
                jvm_args=self.jvm_args,
                java_args=java_args,
                dag=adapter_dag)

            adapter_dag_extened = AdapterDagBuilderExtension()
            adapter_dag_extened.build(adapter_dag, data_source, jar_operator)

        return adapter_dag

builder = AdapterDagBuilder("file")
adapter_config = airflow.models.DAG("adapter")
#adapter_config.date_range(datetime.now(), 1)
adapter_config.dag_id = "123"
adapter_config.default_args = ""
adapter_config.params = ""
adapter_config.schedule_interval = 100
adapter_config.start_date = datetime.now()
builder.build(adapter_config)