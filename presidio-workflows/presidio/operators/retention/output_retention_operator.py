from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.retention.retention_operator import RetentionOperator


class OutputRetentionOperator(RetentionOperator):
    """
    Runs an output retention task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    # Color configurations for the Airflow UI
    ui_color = '#A6E6A6'

    @apply_defaults
    def __init__(self, command, schema, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param command: command
        :type command: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """
        self.log.debug('input operator init kwargs=%s', str(kwargs))

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
