from airflow import LoggingMixin
from presidio.operators.retention.output_retention_operator import OutputRetentionOperator


class OutputRetentionOperatorBuilder(LoggingMixin):
    """
    The "OutputRetentionOperatorBuilder" builds and returns output_retention operator.
    """

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
        Builds output_retention operator.
        :param dag: The DAG to which all relevant retention operators should be added
        :type dag: airflow.models.DAG
        """

        self.log.debug("populating the %s dag with output_retention tasks", dag.dag_id)

        output_retention = OutputRetentionOperator(
            command=self._retention_command,
            schema=self.schema,
            run_clean_command_before_retry=False,
            dag=dag)

        return output_retention
