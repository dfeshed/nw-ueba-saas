from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid


class OutputRetentionDagBuilder(PresidioDagBuilder):
    ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'
    RETENTION_COMMAND_CONFIG_PATH = 'retention.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'
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
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self.data_sources = data_sources
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

    def build(self, output_retention_dag):
        """
        Builds jar operators that do retention for each data source and adds them to the given DAG.
        :param output_retention_dag: The DAG to which all relevant retention operators should be added
        :type output_retention_dag: airflow.models.DAG
        :return: The input DAG, after the retention operators were added
        :rtype: airflow.models.DAG
        """

        self.log.debug("populating the retention dag, dag_id=%s ", output_retention_dag.dag_id)

        def output_retention_condition(context): return is_execution_date_valid(context['execution_date'],
                                                                                          self._output_retention_interval_in_hours,
                                                                                          output_retention_dag.schedule_interval) \
                                                        & PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                        output_retention_dag,
                                                        timedelta(days=self._output_min_time_to_start_retention_in_days),
                                                        context['execution_date'],
                                                        output_retention_dag.schedule_interval)

        FixedDurationJarOperator(
            task_id='retention_output',
            fixed_duration_strategy=timedelta(hours=1),
            command=self._retention_command,
            jvm_args=self.jvm_args,
            run_clean_command_before_retry=False,
            dag=output_retention_dag,
            condition=output_retention_condition)

        return output_retention_dag
