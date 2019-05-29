from datetime import timedelta

from airflow import LoggingMixin

from presidio.operators.retention.output_retention_operator import OutputRetentionOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

# todo: should be changed after new smart entity will be added: split into 2 output_retention tasks:
# todo: output_retention with smart_cof_name arg for output retention
# todo: output_retention with schema arg for output collection retention


class OutputRetentionOperatorBuilder(LoggingMixin):
    """
    The "OutputRetentionOperatorBuilder" builds and returns output_retention operator.
    """

    RETENTION_COMMAND_CONFIG_PATH = 'retention.output.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'

    def __init__(self, schema):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        """
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._retention_command = conf_reader.read(OutputRetentionOperatorBuilder.RETENTION_COMMAND_CONFIG_PATH,
                                                   OutputRetentionOperatorBuilder.RETENTION_COMMAND_DEFAULT_VALUE)
        self.schema = schema

    def build(self, dag):
        """
        Builds output_retention operator.
        :param dag: The DAG to which all relevant retention operators should be added
        :type dag: airflow.models.DAG
        :return: output_retention
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with output_retention tasks", dag.dag_id)

        output_retention = OutputRetentionOperator(
            command=self._retention_command,
            schema=self.schema,
            run_clean_command_before_retry=False,
            dag=dag)

        return output_retention
