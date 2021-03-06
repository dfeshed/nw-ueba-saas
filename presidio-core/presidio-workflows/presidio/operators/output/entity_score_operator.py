from airflow.utils import apply_defaults

from presidio.operators.output.abstract_output_operator import AbstractOutputOperator


class EntityScoreOperator(AbstractOutputOperator):
    """
    Runs an entity score task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, smart_record_conf_name, entity_type, task_id=None, *args, **kwargs):

        self.smart_record_conf_name = smart_record_conf_name

        super(EntityScoreOperator, self).__init__(
            task_id=task_id,
            fixed_duration_strategy=fixed_duration_strategy,
            command=command,
            smart_record_conf_name=smart_record_conf_name,
            entity_type=entity_type,
            *args,
            **kwargs
        )

    def get_task_name(self):
        """
        :return: The task name
        """
        return '{0}_score_processor'.format(self.smart_record_conf_name)
