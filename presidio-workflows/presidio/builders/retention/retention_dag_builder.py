import logging


from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.builders.retention.adapter import AdapterRetentionDagBuilder
from airflow.operators.python_operator import ShortCircuitOperator
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid

ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.adapter.jvm_args'

class RetentionDagBuilder(PresidioDagBuilder):

    min_time_to_start_retention_in_days_conf_key = "retention.min_time_to_start_retention_in_days"
    min_time_to_start_retention_in_days_default_value = 1

    retention_interval_in_hours_conf_key = "retention.retention_interval_in_hours"
    retention_interval_in_hours_default_value = 1

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """

        self.data_sources = data_sources
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self._min_time_to_start_retention_in_days = conf_reader.read(RetentionDagBuilder.min_time_to_start_retention_in_days_conf_key,
                                                                     RetentionDagBuilder.min_time_to_start_retention_in_days_default_value)

        self._retention_interval_in_hours = conf_reader.read(RetentionDagBuilder.retention_interval_in_hours_conf_key,
                                                                     RetentionDagBuilder.retention_interval_in_hours_default_value)

    def build(self, retention_dag):
        """
        Builds jar operators for each data source and adds them to the given DAG.
        :param retention_dag: The DAG to which all relevant "input" operators should be added
        :type retention_dag: airflow.models.DAG
        :return: The input DAG, after the "input" operators were added
        :rtype: airflow.models.DAG
        """

        logging.debug("populating the retention dag, dag_id=%s ", retention_dag.dag_id)

        retention_short_circuit_operator = ShortCircuitOperator(
            task_id='retention_short_circuit',
            dag=retention_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._retention_interval_in_hours,
                                                                     retention_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(retention_dag,
                                                                                                                                   self._min_gap_from_dag_start_date_to_start_modeling,
                                                                                                                                   kwargs['execution_date'],
                                                                                                                                   retention_dag.schedule_interval),
            provide_context=True
        )

        adapter_retention_sub_dag = self._get_presidio_adapter_retention_sub_dag_operator(self.data_sources, retention_dag)

        retention_short_circuit_operator >> adapter_retention_sub_dag

        return retention_dag

    def _get_presidio_adapter_retention_sub_dag_operator(self, data_sources, retention_dag):
        adapter_retention_dag_id = 'adapter_retention_dag'

        return self._create_sub_dag_operator(AdapterRetentionDagBuilder(data_sources), adapter_retention_dag_id, retention_dag)
        