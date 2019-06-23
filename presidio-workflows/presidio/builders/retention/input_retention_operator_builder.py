from airflow import LoggingMixin
from presidio.operators.retention.input_retention_operator import InputRetentionOperator


class InputRetentionOperatorBuilder(LoggingMixin):

    def __init__(self, schema, command):
        """
        C'tor.
        :param schema: The schema we should work on
        :type schema: str
        :param command: command
        :type command: str
        """
        self._retention_command = command
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
            command=self._retention_command,
            schema=self.schema,
            run_clean_command_before_retry=False,
            dag=dag)

        return input_retention_operator
