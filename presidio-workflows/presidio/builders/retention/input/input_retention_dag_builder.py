from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid


class InputRetentionDagBuilder(PresidioDagBuilder):
    ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.input.jvm_args'
    RETENTION_COMMAND_CONFIG_PATH = 'retention.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'
    input_min_time_to_start_retention_in_days_conf_key = "retention.input.min_time_to_start_retention_in_days"
    input_min_time_to_start_retention_in_days_default_value = 2
    input_retention_interval_in_hours_conf_key = "retention.input.retention_interval_in_hours"
    input_retention_interval_in_hours_default_value = 24

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self.schema = schema
        self._retention_command = conf_reader.read(InputRetentionDagBuilder.RETENTION_COMMAND_CONFIG_PATH,
                                                   InputRetentionDagBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.jvm_args = InputRetentionDagBuilder.conf_reader.read(
            conf_key=InputRetentionDagBuilder.ADAPTER_JVM_ARGS_CONFIG_PATH)

        self._input_min_time_to_start_retention_in_days = conf_reader.read(
            InputRetentionDagBuilder.input_min_time_to_start_retention_in_days_conf_key,
            InputRetentionDagBuilder.input_min_time_to_start_retention_in_days_default_value)

        self._input_retention_interval_in_hours = timedelta(
            hours=conf_reader.read(InputRetentionDagBuilder.input_retention_interval_in_hours_conf_key,
                                   InputRetentionDagBuilder.input_retention_interval_in_hours_default_value))

    def build(self, indicator_dag):
        """
        Builds jar operators that do retention for schema and adds them to the given DAG.
        :param indicator_dag: The DAG to which all relevant retention operators should be added
        :type indicator_dag: airflow.models.DAG
        :return: The indicator DAG, after the retention operators were added
        :rtype: airflow.models.DAG
        """

        self.log.debug("populating the %s dag with input_retention tasks", indicator_dag.dag_id)
        input_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='input_retention_short_circuit',
            dag=indicator_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._input_retention_interval_in_hours,
                                                                     get_schedule_interval(indicator_dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 indicator_dag,
                                                 timedelta(days=self._input_min_time_to_start_retention_in_days),
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(indicator_dag)))

        java_args = {
            'schema': self.schema
        }

        input_retention_operator =  FixedDurationJarOperator(
            task_id='retention_input_{}'.format(self.schema),
            fixed_duration_strategy=timedelta(hours=1),
            command=self._retention_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            run_clean_command_before_retry=False,
            dag=indicator_dag)

        input_retention_short_circuit_operator >> input_retention_operator

        return indicator_dag
