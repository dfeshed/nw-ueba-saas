import json
from datetime import timedelta

from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY
from presidio.utils.services.time_service import floor_time, convert_to_utc

START_INSTANCE_DYNAMIC_ARGUMENT = 'startInstant'
END_INSTANCE_DYNAMIC_ARGUMENT = 'endInstant'
JAVA_ARGS_NAME_KEY = 'name'
JAVA_ARGS_ARGUMENTS_KEY = 'arguments'


class InputPreProcessorOperator(SpringBootJarOperator):

    """
    Runs an input pre processor task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. type , schema_name and the static_arguments).
    Other arguments, such as the dynamic_arguments are evaluated before every run.
    """

    @apply_defaults
    def __init__(self, type, schema_name, static_arguments, dynamic_arguments,
                 command, name=None, *args, **kwargs):

        self.type = type
        if(name is None):
            self.task_id = 'input_pre_processor_{}'.format(self.type)
        else:
            self.task_id = 'input_pre_processor_{}'.format(task_id)
        self.schema_name = schema_name
        self.static_arguments = static_arguments
        self.dynamic_arguments = dynamic_arguments

        super(InputPreProcessorOperator, self).__init__(command=command, task_id=self.task_id, *args, **kwargs)

    def execute(self, context):
        """
        Add java args from the static and dynamic arguments
        """

        arguments = self._get_input_pre_processor_arguments(context)

        java_args = {
            JAVA_ARGS_NAME_KEY: self.type,
            JAVA_ARGS_ARGUMENTS_KEY: ("\\\"" + json.dumps(arguments).replace("\"", "\\\\\\\"") + "\\\"")
        }

        super(InputPreProcessorOperator, self).update_java_args(java_args)
        super(InputPreProcessorOperator, self).execute(context)

    def _get_input_pre_processor_arguments(self, context):
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()
        arguments = self.static_arguments.copy()

        if START_INSTANCE_DYNAMIC_ARGUMENT in self.dynamic_arguments:
            start_date = floor_time(execution_date, time_delta=FIX_DURATION_STRATEGY_DAILY)
            utc_start_date = convert_to_utc(start_date)
            arguments.update({START_INSTANCE_DYNAMIC_ARGUMENT: utc_start_date})
        if END_INSTANCE_DYNAMIC_ARGUMENT in self.dynamic_arguments:
            end_date = floor_time(execution_date + timedelta(days=1),
                                  time_delta=FIX_DURATION_STRATEGY_DAILY)
            utc_end_date = convert_to_utc(end_date)
            arguments.update({END_INSTANCE_DYNAMIC_ARGUMENT: utc_end_date})

        return arguments

    @staticmethod
    def handle_retry(context):
        """
        The input pre processor application does not need clean before retries.
        """
        pass

