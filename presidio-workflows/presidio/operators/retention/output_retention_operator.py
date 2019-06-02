from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.retention.retention_operator import RetentionOperator


class OutputRetentionOperator(RetentionOperator):
    """
    Runs an output retention task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the schema).
    Other arguments, such as end date, are evaluated before every run.
    """

    # Color configurations for the Airflow UI
    ui_color = '#A6E6A6'

    @apply_defaults
    def __init__(self, command, schema, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param command: command
        :type command: string
        :param schema: The schema we should work on
        :type schema: str
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """
        self.log.debug('output retention operator init kwargs=%s', str(kwargs))

        self.task_id = task_id or 'output_retention_{0}'.format(schema)
        self.log.debug('agg operator. command=%s', command)
        self.schema = schema

        java_args = {
            'schema': self.schema,
        }

        super(OutputRetentionOperator, self).__init__(
            task_id=self.task_id,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )
