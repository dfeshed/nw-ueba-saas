from abc import ABCMeta, abstractmethod

from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class AbstractOutputOperator(FixedDurationJarOperator):
    """
    Runs an output task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """
    __metaclass__ = ABCMeta

    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, smart_record_conf_name, entity_type, java_args={}, java_retry_args={}, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param command: command
        :type command: string
        :param smart_record_conf_name: smart_record_conf_name
        :type smart_record_conf_name: string
        :param entity_type: entity_type
        :type entity_type: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """
        self.log.debug('output operator init kwargs=%s', str(kwargs))
        self.fixed_duration_strategy = fixed_duration_strategy
        self.smart_record_conf_name = smart_record_conf_name
        self.entity_type = entity_type
        self.task_id = task_id or self.get_task_name()

        java_args_smart_record_conf_name = {
            'smart_record_conf_name': self.smart_record_conf_name,
        }

        java_args_entity_type = {
            'entity_type': self.entity_type,
        }

        new_java_args = {}
        new_java_args.update(java_args)
        new_java_args.update(java_args_smart_record_conf_name)
        new_java_args.update(java_args_entity_type)

        self.log.debug('output operator. command=%s', command)
        super(AbstractOutputOperator, self).__init__(
            task_id=self.task_id,
            fixed_duration_strategy=self.fixed_duration_strategy,
            command=command,
            java_args=new_java_args,
            java_retry_args=java_retry_args,
            *args,
            **kwargs
        )

    @abstractmethod
    def get_task_name(self):
        """
        :return: The task name
        """
        pass



