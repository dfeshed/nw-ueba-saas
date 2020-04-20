from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class InputOperator(FixedDurationJarOperator):
    """
    Runs an input task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """


    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, schema, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param command: command
        :type command: string
        :param schema: schema
        :type schema: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """
        self.log.debug('input operator init kwargs=%s', str(kwargs))

        self.fixed_duration_strategy = fixed_duration_strategy
        self.schema = schema
        self.task_id = task_id or 'input_{}'.format(self.schema)

        java_args = {
            'schema': self.schema,
        }

        self.log.debug('agg operator. command=%s', command)
        super(InputOperator, self).__init__(
            task_id=self.task_id,
            fixed_duration_strategy=self.fixed_duration_strategy,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

