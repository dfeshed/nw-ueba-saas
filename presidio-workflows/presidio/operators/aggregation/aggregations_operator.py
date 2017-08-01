from abc import ABCMeta, abstractmethod

from airflow.utils.decorators import apply_defaults

from presidio.operators.fixed_duration_jar_operator import FixedDurationJarOperator
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string


class AggregationsOperator(FixedDurationJarOperator):
    """
    Runs an aggregations task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the data source).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    This is an abstract class and should not be instantiated - The inheritors should override
    methods that provide task specific information (such as the task name and the JAR file path).
    Currently the known inheritors are FeatureAggregationsOperator and ScoreAggregationsOperator.
    """

    __metaclass__ = ABCMeta

    @apply_defaults
    def __init__(self, fixed_duration_strategy, command, data_source, task_id=None, *args, **kwargs):
        """
        C'tor.
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: timedelta
        :param data_source: The data source whose events should be aggregated
        :type data_source: string
        :param task_id: The task ID of this operator - If None, the ID is generated automatically
        :type task_id: string
        """

        print('agg operator init kwargs=', kwargs)

        self.fixed_duration_strategy = fixed_duration_strategy
        self.data_source = data_source
        self.task_id = task_id or '{}_{}_{}'.format(
            fixed_duration_strategy_to_string(self.fixed_duration_strategy),
            self.data_source,
            self.get_task_name()
        )

        java_args = {
            'data_source': self.data_source,
        }

        print('agg operator. commad=', command)
        print('agg operator. kwargs=', kwargs)
        super(AggregationsOperator, self).__init__(
            task_id=self.task_id,
            fixed_duration_strategy=self.fixed_duration_strategy,
            command=command,
            java_args=java_args,
            *args,
            **kwargs
        )

    @abstractmethod
    def get_task_name(self):
        """
        :return: The task name (e.g. "Feature Aggregations" or "Score Aggregations")
        """
        pass

