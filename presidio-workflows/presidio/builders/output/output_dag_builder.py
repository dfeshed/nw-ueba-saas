import logging

from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

OUTPUT_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'


class OutputDagBuilder(PresidioDagBuilder):
    """
    An "Output DAG" builder - The "Output DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_server_reader

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_source: The data source whose events we should work on
        :type data_source: str
        """

        self.data_sources = data_sources
        self.jvm_args = OutputDagBuilder.conf_reader.read(conf_key=OUTPUT_JVM_ARGS_CONFIG_PATH)

    def build(self, output_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param output_dag: The DAG to which all relevant "output" operators should be added
        :type output_dag: airflow.models.DAG
        :return: The input DAG, after the "output" operators were added
        :rtype: airflow.models.DAG
        """

        logging.info("populating the output dag, dag_id=%s ", output_dag.dag_id)

        java_args = {
        }

        # Create jar operator
        FixedDurationJarOperator(
            task_id='output_processor',
            fixed_duration_strategy=timedelta(hours=1),
            command=PresidioDagBuilder.presidio_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            dag=output_dag)
        # Iterate all configured data sources

        return output_dag
