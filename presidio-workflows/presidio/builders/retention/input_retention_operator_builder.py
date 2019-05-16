from datetime import timedelta

from airflow import LoggingMixin

from presidio.operators.retention.input_retention_operator import InputRetentionOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


class InputRetentionOperatorBuilder(LoggingMixin):

    RETENTION_COMMAND_CONFIG_PATH = 'retention.input.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._retention_command = conf_reader.read(InputRetentionOperatorBuilder.RETENTION_COMMAND_CONFIG_PATH,
                                                   InputRetentionOperatorBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.schema = schema

    def build(self, dag):
        """
        Builds input_retention_operator.
        :param dag: The DAG to which all relevant retention operators should be added
        :type dag: airflow.models.DAG
        :return: input_retention_operator
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with input_retention tasks", dag.dag_id)

        input_retention_operator = InputRetentionOperator(
            fixed_duration_strategy=timedelta(hours=1),
            command=self._retention_command,
            schema=self.schema,
            run_clean_command_before_retry=False,
            dag=dag)

        return input_retention_operator
