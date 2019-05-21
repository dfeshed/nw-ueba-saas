from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.input_retention_operator_builder import InputRetentionOperatorBuilder
from presidio.builders.retention.output_retention_operator_builder import OutputRetentionOperatorBuilder
from presidio.operators.retention.ade_manager_operator import AdeManagerOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid


class RetentionDagBuilder(PresidioDagBuilder):

    min_time_to_start_retention_in_days_conf_key = "retention.min_time_to_start_retention_in_days"
    min_time_to_start_retention_in_days_default_value = 2
    retention_interval_in_hours_conf_key = "retention.retention_interval_in_hours"
    retention_interval_in_hours_default_value = 24

    """
        The "Retention" builder consists of input_retention, ade manager and output_retention
        This dag run once a day.
    """

    def __init__(self):
        """
        C'tor.
        :param data_sources: The data source whose events we should work on
        :type data_sources: str
        """

        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self._min_time_to_start_retention_in_days = conf_reader.read(
            RetentionDagBuilder.min_time_to_start_retention_in_days_conf_key,
            RetentionDagBuilder.min_time_to_start_retention_in_days_default_value)

        self._retention_interval_in_hours = timedelta(
            hours=conf_reader.read(RetentionDagBuilder.retention_interval_in_hours_conf_key,
                                   RetentionDagBuilder.retention_interval_in_hours_default_value))

    def build(self, dag):
        retention_short_circuit_operator = self._build_retention_short_circuit_operator(dag)
        schemas = dag.default_args['schemas']
        self._build_ade_manager_operator(dag, retention_short_circuit_operator)
        for schema in schemas:
            self._build_input_retention(dag, schema, retention_short_circuit_operator)
            self._build_output_retention_operator(dag, schema, retention_short_circuit_operator)
        return dag

    def _build_output_retention_operator(self, dag, schema, retention_short_circuit_operator):
        """
        Create OutputRetentionOperator in order to output documents after all tasks finished to use it.

        :param dag: The retention DAG
        :type dag: airflow.models.DAG
        :param schema: The schema to process the retention
        :type schema: String
        """
        output_retention_operator = OutputRetentionOperatorBuilder(schema).build(dag)
        retention_short_circuit_operator >> output_retention_operator

    def _build_input_retention(self, dag, schema, retention_short_circuit_operator):
        input_retention_operator = InputRetentionOperatorBuilder(schema).build(dag)
        retention_short_circuit_operator >> input_retention_operator

    def _build_ade_manager_operator(self, dag, retention_short_circuit_operator):
        """
        Create AdeManagerOperator in order to clean enriched data after all enriched data customer tasks finished to use it.

        :param dag: The retention DAG
        :type dag: airflow.models.DAG
        """

        ade_manager_operator = AdeManagerOperator(dag=dag)
        retention_short_circuit_operator >> ade_manager_operator

    def _build_retention_short_circuit_operator(self, dag):
        retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='retention_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._retention_interval_in_hours,
                                                                     dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 timedelta(days=self._min_time_to_start_retention_in_days),
                                                 kwargs['execution_date'],
                                                 dag.schedule_interval))
        return retention_short_circuit_operator
