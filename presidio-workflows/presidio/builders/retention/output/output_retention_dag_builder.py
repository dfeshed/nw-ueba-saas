from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid

# todo: should be changed after new smart will be added: split into 2 output_retention tasks:
# todo: output_retention with smart_cof_name arg for alerts retention
# todo: output_retention with schema arg for output collection retention


class OutputRetentionDagBuilder(PresidioDagBuilder):
    ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'
    RETENTION_COMMAND_CONFIG_PATH = 'retention.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'
    output_min_time_to_start_retention_in_days_conf_key = "retention.output.min_time_to_start_retention_in_days"
    output_min_time_to_start_retention_in_days_default_value = 2
    output_retention_interval_in_hours_conf_key = "retention.output.retention_interval_in_hours"
    output_retention_interval_in_hours_default_value = 24

    def __init__(self, smart_record_conf_name):
        """
        C'tor.
        :param smart_record_conf_name: The smart_record_conf_name we should work on
        :type smart_record_conf_name: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self.smart_record_conf_name = smart_record_conf_name
        self._retention_command = conf_reader.read(OutputRetentionDagBuilder.RETENTION_COMMAND_CONFIG_PATH,
                                                   OutputRetentionDagBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.jvm_args = OutputRetentionDagBuilder.conf_reader.read(
            conf_key=OutputRetentionDagBuilder.ADAPTER_JVM_ARGS_CONFIG_PATH)

        self._output_min_time_to_start_retention_in_days = conf_reader.read(
            OutputRetentionDagBuilder.output_min_time_to_start_retention_in_days_conf_key,
            OutputRetentionDagBuilder.output_min_time_to_start_retention_in_days_default_value)

        self._output_retention_interval_in_hours = timedelta(
            hours=conf_reader.read(OutputRetentionDagBuilder.output_retention_interval_in_hours_conf_key,
                                   OutputRetentionDagBuilder.output_retention_interval_in_hours_default_value))

    def build(self, smart_dag):
        """
        Builds jar operators that do retention and adds them to the given DAG.
        :param smart_dag: The DAG to which all relevant retention operators should be added
        :type smart_dag: airflow.models.DAG
        :return: The smart DAG, after the retention operators were added
        :rtype: airflow.models.DAG
        """

        self.log.debug("populating the %s dag with output_retention tasks", smart_dag.dag_id)

        output_retention_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='output_retention_short_circuit',
            dag=smart_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._output_retention_interval_in_hours,
                                                                     smart_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 smart_dag,
                                                 timedelta(days=self._output_min_time_to_start_retention_in_days),
                                                 kwargs['execution_date'],
                                                 smart_dag.schedule_interval))

        output_retention = FixedDurationJarOperator(
            task_id='retention_output',
            fixed_duration_strategy=timedelta(hours=1),
            command=self._retention_command,
            jvm_args=self.jvm_args,
            run_clean_command_before_retry=False,
            dag=smart_dag)

        output_retention_short_circuit_operator >> output_retention

        return smart_dag
