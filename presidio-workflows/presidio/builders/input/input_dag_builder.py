import logging

from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

INPUT_JVM_ARGS_CONFIG_PATH = 'components.input.jvm_args'


class InputDagBuilder(PresidioDagBuilder):
    """
    An "Input DAG" builder - The "Input DAG" consists of multiple tasks / operators one per data source.
    The builder accepts the DAG's attributes through the c'tor, and the "build" method builds and
    returns the DAG according to the given attributes.
    """

    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_source: The data source whose events we should work on
        :type data_source: str
        """

        self.data_sources = data_sources
        self.jvm_args = InputDagBuilder.conf_reader.read(conf_key=INPUT_JVM_ARGS_CONFIG_PATH)

    def build(self, input_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param input_dag: The DAG to which all relevant "input" operators should be added
        :type input_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        logging.info("populating the input dag, dag_id=%s ", input_dag.dag_id)

        # Iterate all configured data sources
        for data_source in self.data_sources:
            java_args = {
                'schema': data_source,
            }

            # Create jar operator for each data source
            FixedDurationJarOperator(
                task_id='input_{}'.format(data_source),
                fixed_duration_strategy=timedelta(hours=1),
                command=PresidioDagBuilder.presidio_command,
                jvm_args=self.jvm_args,
                java_args=java_args,
                dag=input_dag)

        return input_dag
