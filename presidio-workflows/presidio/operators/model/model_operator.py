from abc import ABCMeta, abstractmethod
from airflow.utils.decorators import apply_defaults
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator
from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.services.time_service import convert_to_utc



class ModelOperator(SpringBootJarOperator):
    """
    Runs a model task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the data source).
    Other arguments, such as end date, are evaluated before every run.
    This is an abstract class and should not be instantiated - The inheritors should override
    methods that provide task specific information (such as the task name and the JAR file path).
    """

    __metaclass__ = ABCMeta

    def __init__(self, command, session_id, group_name, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        self.task_id = task_id or self.get_task_id()
        self.interval = kwargs.get('dag').schedule_interval

        java_args = {
            'group_name': group_name,
            'session_id': session_id
        }

        retry_extra_params = {}
        retry_extra_params['schedule_interval'] = self.interval

        super(ModelOperator, self).__init__(
            task_id=self.task_id,
            java_args=java_args,
            command=command,
            retry_extra_params=retry_extra_params,
            retry_java_args_method=ModelOperator.add_java_args,
            *args,
            **kwargs
        )

    def execute(self, context):
        """

        Runs the model jar with end date which equals to execution_date + schedule_interval

        :raise InvalidExecutionDateError - Raise error if the execution_date is not the last interval of fixed duration.
        """
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        end_date = execution_date + self.interval
        java_args = {
            'end_date': convert_to_utc(end_date)
        }

        super(ModelOperator, self).update_java_args(java_args)
        super(ModelOperator, self).execute(context)

    @staticmethod
    def add_java_args(context):

        params = context['params']
        interval = params['retry_extra_params']['schedule_interval']
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        end_date = execution_date + interval
        java_args = {
            'end_date': convert_to_utc(end_date)
        }
        java_args = ' '.join(SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in java_args.iteritems())
        return java_args

    @abstractmethod
    def get_task_id(self):
        """
        :return: The task id 
        """
        pass

