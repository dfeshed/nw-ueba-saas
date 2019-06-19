from airflow.utils import apply_defaults

from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.time_service import convert_to_utc
from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator


class OutputForwarderOperator(SpringBootJarOperator):
    """
    Runs an user score task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    @apply_defaults
    def __init__(self, command, entity_type, task_id=None, *args, **kwargs):

        java_args = {'entity_type': entity_type}
        self.interval = get_schedule_interval(kwargs.get('dag'))

        super(OutputForwarderOperator, self).__init__(
            task_id=task_id,
            command=command,
            java_args=java_args,
            retry_java_args_method=OutputForwarderOperator.retry_java_args,
            *args,
            **kwargs
        )

    def execute(self, context):
        """
        Add start_date and end_date to java_args
        """
        java_args = self.get_java_args(context)

        super(OutputForwarderOperator, self).update_java_args(java_args)
        super(OutputForwarderOperator, self).execute(context)

    def retry_java_args(self, context):
        java_args = ' '.join(
            SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in
            self.get_java_args(context).iteritems()
        )
        return java_args

    def get_java_args(self, context):
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        start_date = execution_date
        end_date = execution_date + self.interval

        java_args = {
            'start_date' : convert_to_utc(start_date),
            'end_date': convert_to_utc(end_date)
        }

        return java_args

    def get_task_name(self):
        """
        :return: The task name
        """
        return 'output_forwarding_task'
