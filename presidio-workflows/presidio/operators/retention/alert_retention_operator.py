from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class AlertRetentionOperator(FixedDurationJarOperator):
    """
    Runs an alert retention task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """


    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, entity_type, task_id=None, *args, **kwargs):
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

        self.fixed_duration_strategy = fixed_duration_strategy
        self.task_id = task_id or 'alert_retention'

        self.log.debug('agg operator. command=%s', command)

        java_args = {'entity_type': entity_type}
        super(AlertRetentionOperator, self).__init__(
            task_id=self.task_id,
            fixed_duration_strategy=self.fixed_duration_strategy,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

