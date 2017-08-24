from datetime import timedelta

from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator


class SmartModelAccumulateOperator(FixedDurationJarOperator):
    """
    Runs a accumulate aggregations task (a JAR file) for aggregation events model building, using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the data source).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, smart_events_conf, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the feature bucket aggregations (currently only daily)
        :type fixed_duration_strategy: timedelta
        :param smart_events_conf: The smart event conf to do the accumulation on
        :type smart_events_conf: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        self._smart_events_conf = smart_events_conf
        self.task_id = task_id or '{}_{}'.format(
            self._smart_events_conf,
            self.get_task_name()
        )

        java_args = {
            'smart_record_conf_name': self._smart_events_conf,
        }

        super(SmartModelAccumulateOperator, self).__init__(
            task_id=self.task_id,
            fixed_duration_strategy=fixed_duration_strategy,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

    def add_fixed_duration_strategy(self, java_args):
        java_args.update({'accumulate_duration': self.fixed_duration_strategy.total_seconds()})

    def get_task_name(self):
        """
        :return: The task name
        """
        return 'smart_model_accumulation'

