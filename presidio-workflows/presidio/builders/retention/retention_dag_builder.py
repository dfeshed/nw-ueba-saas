import logging
from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.input.input_retention_dag_builder import InputRetentionDagBuilder
from presidio.builders.retention.output.output_retention_dag_builder import OutputRetentionDagBuilder
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid

presidio_extension = __import__('presidio_extension.builders.retention.adapter_retention_dag_builder',
                                fromlist=['AdapterRetentionDagBuilder'])
RetentionDagExtensionBuilder = getattr(presidio_extension, 'AdapterRetentionDagBuilder')

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'


class RetentionDagBuilder(PresidioDagBuilder):
    input_min_time_to_start_retention_in_days_conf_key = "retention.input.min_time_to_start_retention_in_days"
    input_min_time_to_start_retention_in_days_default_value = 2
    input_retention_interval_in_hours_conf_key = "retention.input.retention_interval_in_hours"
    input_retention_interval_in_hours_default_value = 24

    output_min_time_to_start_retention_in_days_conf_key = "retention.output.min_time_to_start_retention_in_days"
    output_min_time_to_start_retention_in_days_default_value = 2
    output_retention_interval_in_hours_conf_key = "retention.output.retention_interval_in_hours"
    output_retention_interval_in_hours_default_value = 24

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """

        self.data_sources = data_sources
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self._input_min_time_to_start_retention_in_days = conf_reader.read(
            RetentionDagBuilder.input_min_time_to_start_retention_in_days_conf_key,
            RetentionDagBuilder.input_min_time_to_start_retention_in_days_default_value)

        self._input_retention_interval_in_hours = timedelta(
            hours=conf_reader.read(RetentionDagBuilder.input_retention_interval_in_hours_conf_key,
                                   RetentionDagBuilder.input_retention_interval_in_hours_default_value))

        self._output_min_time_to_start_retention_in_days = conf_reader.read(
            RetentionDagBuilder.output_min_time_to_start_retention_in_days_conf_key,
            RetentionDagBuilder.output_min_time_to_start_retention_in_days_default_value)

        self._output_retention_interval_in_hours = timedelta(
            hours=conf_reader.read(RetentionDagBuilder.output_retention_interval_in_hours_conf_key,
                                   RetentionDagBuilder.output_retention_interval_in_hours_default_value))

    def build(self, retention_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param retention_dag: The DAG to which all relevant "input" operators should be added
        :type retention_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        logging.debug("populating the retention dag, dag_id=%s ", retention_dag.dag_id)

        input_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='input_retention_short_circuit',
            dag=retention_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._input_retention_interval_in_hours,
                                                                     retention_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 retention_dag,
                                                 timedelta(days=self._input_min_time_to_start_retention_in_days),
                                                 kwargs['execution_date'],
                                                 retention_dag.schedule_interval)
        )

        output_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_retention_short_circuit',
            dag=retention_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._output_retention_interval_in_hours,
                                                                     retention_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 retention_dag,
                                                 timedelta(days=self._output_min_time_to_start_retention_in_days),
                                                 kwargs['execution_date'],
                                                 retention_dag.schedule_interval)
        )

        input_retention_sub_dag = self._get_presidio_input_retention_sub_dag_operator(self.data_sources, retention_dag)
        output_retention_sub_dag = self._get_presidio_output_retention_sub_dag_operator(self.data_sources,
                                                                                        retention_dag)

        input_retention_short_circuit_operator >> input_retention_sub_dag
        output_retention_short_circuit_operator >> output_retention_sub_dag

        retention_dag_extended = RetentionDagExtensionBuilder()
        retention_dag_extended.build(retention_dag)

        return retention_dag

    def _get_presidio_input_retention_sub_dag_operator(self, data_sources, retention_dag):
        input_retention_dag_id = 'input_retention_dag'

        return self._create_sub_dag_operator(InputRetentionDagBuilder(data_sources), input_retention_dag_id,
                                             retention_dag)

    def _get_presidio_output_retention_sub_dag_operator(self, data_sources, retention_dag):
        output_retention_dag_id = 'output_retention_dag'

        return self._create_sub_dag_operator(OutputRetentionDagBuilder(data_sources), output_retention_dag_id,
                                             retention_dag)
