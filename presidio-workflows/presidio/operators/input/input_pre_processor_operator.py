import json
from datetime import timedelta

from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY
from presidio.utils.services.time_service import floor_time, convert_to_utc


class InputPreProcessorOperator(SpringBootJarOperator):

    @apply_defaults
    def __init__(self, name, schema_name, static_arguments, dynamic_arguments,
                 command, task_id=None, *args, **kwargs):

        self.name = name
        self.task_id = task_id or 'input_pre_processor_{}'.format(self.name)
        self.schema_name = schema_name
        self.static_arguments = static_arguments
        self.dynamic_arguments = dynamic_arguments

        super(InputPreProcessorOperator, self).__init__(command=command, task_id=self.task_id, *args, **kwargs)

    def execute(self, context):
        """
        Add java args from the static and dynamic arguments
        """

        arguments = self.add_dynamic_arguments(context)
        arguments.update({'schema' : self.schema_name})

        java_args = {
            'name': self.name,
            'arguments': json.dumps(arguments)
        }

        super(InputPreProcessorOperator, self).update_java_args(java_args)
        super(InputPreProcessorOperator, self).execute(context)

    def add_dynamic_arguments(self, context):
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        if 'startInstant' in self.dynamic_arguments:
            start_date = floor_time(execution_date, time_delta=FIX_DURATION_STRATEGY_DAILY)
            utc_start_date = convert_to_utc(start_date)
            self.static_arguments.update({'startInstant': utc_start_date})
        if 'endInstant' in self.dynamic_arguments:
            end_date = floor_time(execution_date + timedelta(days=1),
                                  time_delta=FIX_DURATION_STRATEGY_DAILY)
            utc_end_date = convert_to_utc(end_date)
            self.static_arguments.update({'endInstant': utc_end_date})

        return self.static_arguments

    @staticmethod
    def handle_retry(context):
        """
        The input pre processor application does not need clean before retries.
        """
        pass

