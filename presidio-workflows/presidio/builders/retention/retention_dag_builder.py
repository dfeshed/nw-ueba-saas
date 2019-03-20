from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.input.input_retention_dag_builder import InputRetentionDagBuilder
from presidio.builders.retention.output.output_retention_dag_builder import OutputRetentionDagBuilder
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid

presidio_extension = __import__('presidio_extension.builders.retention.adapter.adapter_retention_dag_builder', fromlist=['AdapterRetentionDagBuilder'])
RetentionDagExtensionBuilder = getattr(presidio_extension, 'AdapterRetentionDagBuilder')

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class RetentionDagBuilder(PresidioDagBuilder):

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """

        self.data_sources = data_sources

    def build(self, retention_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param retention_dag: The DAG to which all relevant "input" operators should be added
        :type retention_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        self.log.debug("populating the retention dag, dag_id=%s ", retention_dag.dag_id)

        self._get_presidio_input_retention_group_connector_operator(self.data_sources, retention_dag)
        self._get_presidio_output_retention_group_connector_operator(self.data_sources, retention_dag)

        retention_dag_extended = RetentionDagExtensionBuilder(self.data_sources)
        retention_dag_extended.build(retention_dag)

        return retention_dag

    def _get_presidio_input_retention_group_connector_operator(self, data_sources, retention_dag):
        input_retention_dag_id = 'input_retention_dag'

        return self._create_single_point_group_connector(InputRetentionDagBuilder(data_sources), input_retention_dag_id,
                                                         retention_dag, None, False)

    def _get_presidio_output_retention_group_connector_operator(self, data_sources, retention_dag):
        output_retention_dag_id = 'output_retention_dag'

        return self._create_single_point_group_connector(OutputRetentionDagBuilder(data_sources), output_retention_dag_id,
                                                         retention_dag, None, False)
