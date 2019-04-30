from datetime import timedelta

from airflow import LoggingMixin

from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class InputRetentionOperatorBuilder(LoggingMixin):
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
        self._retention_command = conf_reader.read(InputRetentionOperatorBuilder.RETENTION_COMMAND_CONFIG_PATH,
                                                   InputRetentionOperatorBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.jvm_args = conf_reader.read(
            conf_key=InputRetentionOperatorBuilder.ADAPTER_JVM_ARGS_CONFIG_PATH)

    @staticmethod
    def get_input_min_time_to_start_retention_in_days(conf_reader):
        return conf_reader.read(
            InputRetentionOperatorBuilder.input_min_time_to_start_retention_in_days_conf_key,
            InputRetentionOperatorBuilder.input_min_time_to_start_retention_in_days_default_value)

    @staticmethod
    def get_input_retention_interval_in_hours(conf_reader):
        return timedelta(
            hours=conf_reader.read(InputRetentionOperatorBuilder.input_retention_interval_in_hours_conf_key,
                                   InputRetentionOperatorBuilder.input_retention_interval_in_hours_default_value))

    def build(self, dag):
        """
        Builds input_retention_operator.
        :param dag: The DAG to which all relevant retention operators should be added
        :type dag: airflow.models.DAG
        :return: input_retention_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with input_retention tasks", dag.dag_id)

        java_args = {
            'schema': self.schema
        }

        input_retention_operator = FixedDurationJarOperator(
            task_id='retention_input_{}'.format(self.schema),
            fixed_duration_strategy=timedelta(hours=1),
            command=self._retention_command,
            jvm_args=self.jvm_args,
            java_args=java_args,
            run_clean_command_before_retry=False,
            dag=dag)

        return input_retention_operator
