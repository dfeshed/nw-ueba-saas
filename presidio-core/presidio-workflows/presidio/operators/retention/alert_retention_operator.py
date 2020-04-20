from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.retention.retention_operator import RetentionOperator


class AlertRetentionOperator(RetentionOperator):
    """
    Runs an alert retention task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """


    @apply_defaults
    def __init__(self, command, entity_type, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param command: command
        :type command: string
        :param entity_type: the entity type to process the alert cleanup.
        :type entity_type: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """
        self.log.debug('alert retention operator init kwargs=%s', str(kwargs))
        self.task_id = task_id or 'alert_retention'
        self.log.debug('agg operator. command=%s', command)
        java_args = {'entity_type': entity_type}

        super(AlertRetentionOperator, self).__init__(
            task_id=self.task_id,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

