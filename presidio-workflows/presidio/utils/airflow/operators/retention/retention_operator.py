from airflow.utils.decorators import apply_defaults

from presidio.utils.services.time_service import convert_to_utc, floor_time
from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator


class RetentionOperator(SpringBootJarOperator):
    """
    Execute retention operator.

    :param task_id: The task id
    :type task_id: string
    :param command: command
    :type command: string
    :param java_args: The java args.
    :type java_args: dict
    """

    @apply_defaults
    def __init__(self, task_id, command, java_args={}, *args, **kwargs):

        kwargs['retry_callback'] = RetentionOperator.handle_retry

        super(RetentionOperator, self).__init__(command=command,
                                                java_args=java_args,
                                                retry_java_args_method=RetentionOperator.add_java_args,
                                                task_id=task_id,
                                                *args, **kwargs)

    def execute(self, context):
        """
        Add end_date to java_args
        """
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        until_date = execution_date

        java_args = {
            'end_date': convert_to_utc(until_date),
        }

        super(RetentionOperator, self).update_java_args(java_args)
        super(RetentionOperator, self).execute(context)

    def add_java_args(self, context):
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        end_date = execution_date

        java_args = {
            'end_date': convert_to_utc(end_date)
        }

        java_args = ' '.join(
            SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in java_args.iteritems()
        )
        return java_args

    @staticmethod
    def handle_retry(context):
        """
        The retention application does not need clean before retries.
        """
        pass
