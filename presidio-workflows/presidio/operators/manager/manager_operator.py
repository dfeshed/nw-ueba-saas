from airflow.utils.decorators import apply_defaults

from presidio.utils.services.time_service import convert_to_utc

from presidio.utils.airflow.context_wrapper import ContextWrapper

from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator


class ManagerOperator(SpringBootJarOperator):
    """
    Runs a "ManagerOperator" task (JAR).
    The jar cleanup all the enriched collections.
    """

    # Color configurations for the Airflow UI
    ui_color = '#1abc9c'
    ui_fgcolor = '#000000'

    cleanup_command = "enriched_ttl_cleanup"

    @apply_defaults
    def __init__(self, command, *args, **kwargs):
        self.interval = kwargs.get('dag').schedule_interval
        super(ManagerOperator, self).__init__(command=command, task_id=self.get_task_id(), *args, **kwargs)

    def execute(self, context):
        """
        Add until_date to java_args
        """
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()
        until_date = execution_date + self.interval

        java_args = {
            'until_date': convert_to_utc(until_date),
        }

        super(ManagerOperator, self).update_java_args(java_args)
        super(ManagerOperator, self).execute(context)

    @staticmethod
    def get_task_id():
        """
        :return: The task id
        """
        return '{}'.format('ade_manager')
