from datetime import timedelta

from airflow import LoggingMixin

from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


# todo: should be changed after new smart entity will be added: split into 2 output_retention tasks:
# todo: output_retention with smart_cof_name arg for alerts retention
# todo: output_retention with schema arg for output collection retention


class OutputRetentionOperatorBuilder(LoggingMixin):
    """
    The "OutputRetentionOperatorBuilder" builds and returns output_retention operator.
    """

    ADAPTER_JVM_ARGS_CONFIG_PATH = 'components.output.jvm_args'
    RETENTION_COMMAND_CONFIG_PATH = 'retention.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'
    output_min_time_to_start_retention_in_days_conf_key = "retention.output.min_time_to_start_retention_in_days"
    output_min_time_to_start_retention_in_days_default_value = 2
    output_retention_interval_in_hours_conf_key = "retention.output.retention_interval_in_hours"
    output_retention_interval_in_hours_default_value = 24

    def __init__(self):
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._retention_command = conf_reader.read(
            OutputRetentionOperatorBuilder.RETENTION_COMMAND_CONFIG_PATH,
            OutputRetentionOperatorBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.jvm_args = conf_reader.read(
            conf_key=OutputRetentionOperatorBuilder.ADAPTER_JVM_ARGS_CONFIG_PATH)

    @staticmethod
    def get_output_min_time_to_start_retention_in_days(conf_reader):
        return conf_reader.read(
            OutputRetentionOperatorBuilder.output_min_time_to_start_retention_in_days_conf_key,
            OutputRetentionOperatorBuilder.output_min_time_to_start_retention_in_days_default_value)

    @staticmethod
    def get_output_retention_interval_in_hours(conf_reader):
        return timedelta(
            hours=conf_reader.read(
                OutputRetentionOperatorBuilder.output_retention_interval_in_hours_conf_key,
                OutputRetentionOperatorBuilder.output_retention_interval_in_hours_default_value))

    def build(self, dag):
        """
        Builds output_retention operator.
        :param dag: The DAG to which all relevant retention operators should be added
        :type dag: airflow.models.DAG
        :return: output_retention
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with output_retention tasks", dag.dag_id)

        output_retention = FixedDurationJarOperator(
            task_id='retention_output',
            fixed_duration_strategy=timedelta(hours=1),
            command=self._retention_command,
            jvm_args=self.jvm_args,
            run_clean_command_before_retry=False,
            dag=dag)

        return output_retention
